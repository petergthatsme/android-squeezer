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
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Spinner;

import java.util.List;

import uk.org.ngo.squeezer.framework.BaseListActivity;
import uk.org.ngo.squeezer.framework.Item;
import uk.org.ngo.squeezer.framework.ItemView;
import uk.org.ngo.squeezer.itemlist.GenreSpinner.GenreSpinnerCallback;
import uk.org.ngo.squeezer.itemlist.dialog.ArtistFilterDialog;
import uk.org.ngo.squeezer.menu.BaseMenuFragment;
import uk.org.ngo.squeezer.menu.FilterMenuFragment;
import uk.org.ngo.squeezer.menu.FilterMenuFragment.FilterableListActivity;
import uk.org.ngo.squeezer.model.Album;
import uk.org.ngo.squeezer.model.Artist;
import uk.org.ngo.squeezer.model.Genre;

public class ArtistListActivity extends BaseListActivity<Artist> implements
        GenreSpinnerCallback, FilterableListActivity {

    private String searchString = null;

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    private Album album;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    Genre genre;

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    private GenreSpinner genreSpinner;

    public void setGenreSpinner(Spinner spinner) {
        genreSpinner = new GenreSpinner(this, this, spinner);
    }

    @Override
    public ItemView<Artist> createItemView() {
        return new ArtistView(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseMenuFragment.add(this, FilterMenuFragment.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                if (Album.class.getName().equals(key)) {
                    album = extras.getParcelable(key);
                } else if (Genre.class.getName().equals(key)) {
                    genre = extras.getParcelable(key);
                } else {
                    Log.e(getTag(), "Unexpected extra value: " + key + "("
                            + extras.get(key).getClass().getName() + ")");
                }
            }
        }
    }

    @Override
    protected void registerCallback() throws RemoteException {
        getService().registerArtistListCallback(artistsListCallback);
        if (genreSpinner != null) {
            genreSpinner.registerCallback();
        }
    }

    @Override
    protected void unregisterCallback() throws RemoteException {
        getService().unregisterArtistListCallback(artistsListCallback);
        if (genreSpinner != null) {
            genreSpinner.unregisterCallback();
        }
    }

    @Override
    protected void orderPage(int start) throws RemoteException {
        getService().artists(start, getSearchString(), album, genre);
    }

    @Override
    public boolean onSearchRequested() {
        showFilterDialog();
        return false;
    }

    public void showFilterDialog() {
        new ArtistFilterDialog().show(getSupportFragmentManager(), "ArtistFilterDialog");
    }

    public static void show(Context context, Item... items) {
        final Intent intent = new Intent(context, ArtistListActivity.class);
        for (Item item : items) {
            intent.putExtra(item.getClass().getName(), item);
        }
        context.startActivity(intent);
    }

    private final IServiceArtistListCallback artistsListCallback
            = new IServiceArtistListCallback.Stub() {
        public void onArtistsReceived(int count, int start, List<Artist> items)
                throws RemoteException {
            onItemsReceived(count, start, items);
        }
    };

}
