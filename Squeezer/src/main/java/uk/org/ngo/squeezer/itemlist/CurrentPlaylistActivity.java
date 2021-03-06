/*
 * Copyright (c) 2011 Kurt Aaholst <kaaholst@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.ngo.squeezer.itemlist;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.EnumSet;
import java.util.List;

import uk.org.ngo.squeezer.IServiceMusicChangedCallback;
import uk.org.ngo.squeezer.R;
import uk.org.ngo.squeezer.framework.BaseListActivity;
import uk.org.ngo.squeezer.framework.ItemAdapter;
import uk.org.ngo.squeezer.framework.ItemListAdapter;
import uk.org.ngo.squeezer.framework.ItemView;
import uk.org.ngo.squeezer.itemlist.dialog.PlaylistItemMoveDialog;
import uk.org.ngo.squeezer.itemlist.dialog.PlaylistSaveDialog;
import uk.org.ngo.squeezer.model.PlayerState;
import uk.org.ngo.squeezer.model.Song;
import uk.org.ngo.squeezer.util.ImageFetcher;

import static uk.org.ngo.squeezer.framework.BaseItemView.ViewHolder;

/**
 * Activity that shows the songs in the current playlist.
 */
public class CurrentPlaylistActivity extends BaseListActivity<Song> {

    public static void show(Context context) {
        final Intent intent = new Intent(context, CurrentPlaylistActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    private int currentPlaylistIndex;

    /**
     * A list adapter that highlights the view that's currently playing.
     */
    private class HighlightingListAdapter extends ItemListAdapter<Song> {

        public HighlightingListAdapter(ItemView<Song> itemView,
                ImageFetcher imageFetcher) {
            super(itemView, imageFetcher);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            Object viewTag = view.getTag();

            // This test because the view tag wont be set until the album is received from the server
            if (viewTag != null && viewTag instanceof ViewHolder) {
                ViewHolder viewHolder = (ViewHolder) viewTag;
                if (position == currentPlaylistIndex) {
                    viewHolder.text1
                            .setTextAppearance(getActivity(), R.style.SqueezerCurrentTextItem);
                    view.setBackgroundResource(R.drawable.list_item_background_current);
                } else {
                    viewHolder.text1.setTextAppearance(getActivity(), R.style.SqueezerTextItem);
                    view.setBackgroundResource(R.drawable.list_item_background_normal);
                }
            }
            return view;
        }
    }

    @Override
    protected ItemAdapter<Song> createItemListAdapter(
            ItemView<Song> itemView) {
        return new HighlightingListAdapter(itemView, getImageFetcher());
    }

    @Override
    public ItemView<Song> createItemView() {
        SongViewWithArt view = new SongViewWithArt(this) {
            /**
             * Jumps to whichever song the user chose.
             */
            @Override
            public void onItemSelected(int index, Song item) throws RemoteException {
                getActivity().getService().playlistIndex(index);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                super.onCreateContextMenu(menu, v, menuInfo);

                menu.setGroupVisible(R.id.group_playlist, true);
                menu.findItem(R.id.add_to_playlist).setVisible(false);
                menu.findItem(R.id.play_next).setVisible(false);

                if (menuInfo.position == 0) {
                    menu.findItem(R.id.playlist_move_up).setVisible(false);
                }

                if (menuInfo.position == menuInfo.adapter.getCount() - 1) {
                    menu.findItem(R.id.playlist_move_down).setVisible(false);
                }
            }

            @Override
            public boolean doItemContext(MenuItem menuItem, int index, Song selectedItem)
                    throws RemoteException {
                switch (menuItem.getItemId()) {
                    case R.id.play_now:
                        getService().playlistIndex(index);
                        return true;

                    case R.id.remove_from_playlist:
                        getService().playlistRemove(index);
                        clearAndReOrderItems();
                        return true;

                    case R.id.playlist_move_up:
                        getService().playlistMove(index, index - 1);
                        clearAndReOrderItems();
                        return true;

                    case R.id.playlist_move_down:
                        getService().playlistMove(index, index + 1);
                        clearAndReOrderItems();
                        return true;

                    case R.id.playlist_move:
                        PlaylistItemMoveDialog.addTo(CurrentPlaylistActivity.this,
                                index);
                        return true;
                }

                return super.doItemContext(menuItem, index, selectedItem);
            }
        };

        view.setDetails(EnumSet.of(
                SongView.Details.DURATION,
                SongView.Details.ALBUM,
                SongView.Details.ARTIST));

        return view;
    }

    @Override
    protected void orderPage(int start) throws RemoteException {
        getService().currentPlaylist(start);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.currentplaylistmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_playlist_clear:
                if (getService() != null) {
                    try {
                        getService().playlistClear();
                        finish();
                    } catch (RemoteException e) {
                        Log.e(getTag(), "Error trying to clear playlist: " + e);
                    }
                }
                return true;
            case R.id.menu_item_playlist_save:
                PlaylistSaveDialog.addTo(this, getCurrentPlaylist());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getCurrentPlaylist() {
        if (getService() == null) {
            return null;
        }
        try {
            return getService().getCurrentPlaylist();
        } catch (RemoteException e) {
            Log.e(getTag(), "Service exception in getCurrentPlaylist(): " + e);
        }
        return null;
    }

    @Override
    protected void registerCallback() throws RemoteException {
        getService().registerCurrentPlaylistCallback(currentPlaylistCallback);
        getService().registerSongListCallback(songListCallback);
        getService().registerMusicChangedCallback(musicChangedCallback);
    }

    @Override
    protected void unregisterCallback() throws RemoteException {
        getService().unregisterCurrentPlaylistCallback(currentPlaylistCallback);
        getService().unregisterSongListCallback(songListCallback);
        getService().unregisterMusicChangedCallback(musicChangedCallback);
    }

    private final IServiceCurrentPlaylistCallback currentPlaylistCallback
            = new IServiceCurrentPlaylistCallback.Stub() {
        @Override
        public void onAddTracks(PlayerState playerState) {
            getUIThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    clearAndReOrderItems();
                    getItemAdapter().notifyDataSetChanged();
                }
            });
        }

        public void onDelete(PlayerState playerState, int index) {
            getUIThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    // TODO: Investigate feasibility of deleting single items from the adapter.
                    clearAndReOrderItems();
                    getItemAdapter().notifyDataSetChanged();
                }
            });
        }
    };

    private final IServiceMusicChangedCallback musicChangedCallback
            = new IServiceMusicChangedCallback.Stub() {
        @Override
        public void onMusicChanged(PlayerState playerState) throws RemoteException {
            Log.d(getTag(), "onMusicChanged " + playerState.getCurrentSong());
            currentPlaylistIndex = playerState.getCurrentPlaylistIndex();
            getUIThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    getItemAdapter().notifyDataSetChanged();
                }
            });
        }
    };

    private final IServiceSongListCallback songListCallback = new IServiceSongListCallback.Stub() {
        @Override
        public void onSongsReceived(int count, int start, List<Song> items) throws RemoteException {
            currentPlaylistIndex = getService().getPlayerState().getCurrentPlaylistIndex();
            onItemsReceived(count, start, items);
            // Initially position the list at the currently playing song.
            // Do it again once it has loaded because the newly displayed items
            // may push the current song outside the displayed area.
            if (start == 0 || (start <= currentPlaylistIndex && currentPlaylistIndex < start + items
                    .size())) {
                selectCurrentSong(currentPlaylistIndex, start);
            }
        }
    };

    private void selectCurrentSong(final int currentPlaylistIndex, final int start) {
        Log.i(getTag(), "set selection(" + start + "): " + currentPlaylistIndex);
        getListView().post(new Runnable() {
            @Override
            public void run() {
                // TODO: this doesn't work if the current playlist is displayed in a grid
                ((ListView) getListView()).setSelectionFromTop(currentPlaylistIndex, 0);
            }
        });
    }

}
