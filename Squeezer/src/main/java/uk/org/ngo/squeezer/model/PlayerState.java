/*
 * Copyright (c) 2009 Google Inc.  All Rights Reserved.
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

package uk.org.ngo.squeezer.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import uk.org.ngo.squeezer.R;
import uk.org.ngo.squeezer.service.ServerString;


public class PlayerState implements Parcelable {

    public PlayerState() {
    }

    public static final Creator<PlayerState> CREATOR = new Creator<PlayerState>() {
        @Override
        public PlayerState[] newArray(int size) {
            return new PlayerState[size];
        }

        @Override
        public PlayerState createFromParcel(Parcel source) {
            return new PlayerState(source);
        }
    };

    private PlayerState(Parcel source) {
        playStatus = PlayStatus.valueOf(source.readString());
        poweredOn = (source.readByte() == 1);
        shuffleStatus = ShuffleStatus.valueOf(source.readInt());
        repeatStatus = RepeatStatus.valueOf(source.readInt());
        currentSong = source.readParcelable(null);
        currentPlaylistIndex = source.readInt();
        currentTimeSecond = source.readInt();
        currentSongDuration = source.readInt();
        currentVolume = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playStatus.name());
        dest.writeByte(poweredOn ? (byte) 1 : (byte) 0);
        dest.writeInt(shuffleStatus.getId());
        dest.writeInt(repeatStatus.getId());
        dest.writeParcelable(currentSong, 0);
        dest.writeInt(currentPlaylistIndex);
        dest.writeInt(currentTimeSecond);
        dest.writeInt(currentSongDuration);
        dest.writeInt(currentVolume);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private boolean poweredOn;

    private PlayStatus playStatus;

    private ShuffleStatus shuffleStatus;

    private RepeatStatus repeatStatus;

    private Song currentSong;

    private String currentPlaylist;

    private int currentPlaylistIndex;

    private int currentTimeSecond;

    private int currentSongDuration;

    private int currentVolume;

    public boolean isPlaying() {
        return playStatus == PlayStatus.play;
    }

    public PlayStatus getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(PlayStatus state) {
        playStatus = state;
    }

    public boolean isPoweredOn() {
        return poweredOn;
    }

    public void setPoweredOn(boolean state) {
        poweredOn = state;
    }

    public ShuffleStatus getShuffleStatus() {
        return shuffleStatus;
    }

    public void setShuffleStatus(ShuffleStatus status) {
        shuffleStatus = status;
    }

    public RepeatStatus getRepeatStatus() {
        return repeatStatus;
    }

    public void setRepeatStatus(RepeatStatus status) {
        repeatStatus = status;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public String getCurrentSongName() {
        return (currentSong != null) ? currentSong.getName() : "";
    }

    public void setCurrentSong(Song song) {
        currentSong = song;
    }

    public String getCurrentPlaylist() {
        return currentPlaylist;
    }

    public int getCurrentPlaylistIndex() {
        return currentPlaylistIndex;
    }

    public void setCurrentPlaylist(String playlist) {
        currentPlaylist = playlist;
    }

    public PlayerState setCurrentPlaylistIndex(int value) {
        currentPlaylistIndex = value;
        return this;
    }

    public int getCurrentTimeSecond() {
        return currentTimeSecond;
    }

    public PlayerState setCurrentTimeSecond(int value) {
        currentTimeSecond = value;
        return this;
    }

    public int getCurrentSongDuration() {
        return currentSongDuration;
    }

    public PlayerState setCurrentSongDuration(int value) {
        currentSongDuration = value;
        return this;
    }

    public int getCurrentVolume() {
        return currentVolume;
    }

    public PlayerState setCurrentVolume(int value) {
        currentVolume = value;
        return this;
    }

    public static enum PlayStatus {
        play,
        pause,
        stop
    }

    public static enum ShuffleStatus implements EnumWithId {
        SHUFFLE_OFF(0, R.drawable.btn_shuffle_off, ServerString.SHUFFLE_OFF),
        SHUFFLE_SONG(1, R.drawable.btn_shuffle_song, ServerString.SHUFFLE_ON_SONGS),
        SHUFFLE_ALBUM(2, R.drawable.btn_shuffle_album, ServerString.SHUFFLE_ON_ALBUMS);

        private int id;

        private int icon;

        private ServerString text;

        private static EnumIdLookup<ShuffleStatus> lookup = new EnumIdLookup<ShuffleStatus>(
                ShuffleStatus.class);

        private ShuffleStatus(int id, int icon, ServerString text) {
            this.id = id;
            this.icon = icon;
            this.text = text;
        }

        @Override
        public int getId() {
            return id;
        }

        public int getIcon() {
            return icon;
        }

        public ServerString getText() {
            return text;
        }

        public static ShuffleStatus valueOf(int id) {
            return lookup.get(id);
        }
    }

    public static enum RepeatStatus implements EnumWithId {
        REPEAT_OFF(0, R.drawable.btn_repeat_off, ServerString.REPEAT_OFF),
        REPEAT_ONE(1, R.drawable.btn_repeat_one, ServerString.REPEAT_ONE),
        REPEAT_ALL(2, R.drawable.btn_repeat_all, ServerString.REPEAT_ALL);

        private int id;

        private int icon;

        private ServerString text;

        private static EnumIdLookup<RepeatStatus> lookup = new EnumIdLookup<RepeatStatus>(
                RepeatStatus.class);

        private RepeatStatus(int id, int icon, ServerString text) {
            this.id = id;
            this.icon = icon;
            this.text = text;
        }

        @Override
        public int getId() {
            return id;
        }

        public int getIcon() {
            return icon;
        }

        public ServerString getText() {
            return text;
        }

        public static RepeatStatus valueOf(int id) {
            return lookup.get(id);
        }
    }

    public interface EnumWithId {

        int getId();
    }

    public static class EnumIdLookup<E extends Enum<E> & EnumWithId> {

        private SparseArray<E> map = new SparseArray<E>();

        public EnumIdLookup(Class<E> enumType) {
            for (E v : enumType.getEnumConstants()) {
                map.put(v.getId(), v);
            }
        }

        public E get(int num) {
            return map.get(num);
        }
    }

}
