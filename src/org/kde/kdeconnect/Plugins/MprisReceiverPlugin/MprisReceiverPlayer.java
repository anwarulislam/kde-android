/*
 * Copyright 2018 Nicolas Fella <nicolas.fella@gmx.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License or (at your option) version 3 or any later version
 * accepted by the membership of KDE e.V. (or its successor approved
 * by the membership of KDE e.V.), which shall act as a proxy
 * defined in Section 14 of version 3 of the license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kde.kdeconnect.Plugins.MprisReceiverPlugin;

import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import org.apache.commons.lang3.StringUtils;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class MprisReceiverPlayer {

    private final MediaController controller;

    private final String name;

    MprisReceiverPlayer(MediaController controller, String name) {
        this.controller = controller;
        this.name = name;
    }

    boolean isPlaying() {
        PlaybackState state = controller.getPlaybackState();
        if (state == null) return false;

        return state.getState() == PlaybackState.STATE_PLAYING;
    }

    boolean canPlay() {
        PlaybackState state = controller.getPlaybackState();
        if (state == null) return false;

        if (state.getState() == PlaybackState.STATE_PLAYING) return true;

        return (state.getActions() & (PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE)) != 0;
    }

    boolean canPause() {
        PlaybackState state = controller.getPlaybackState();
        if (state == null) return false;

        if (state.getState() == PlaybackState.STATE_PAUSED) return true;

        return (state.getActions() & (PlaybackState.ACTION_PAUSE | PlaybackState.ACTION_PLAY_PAUSE)) != 0;
    }

    boolean canGoPrevious() {
        PlaybackState state = controller.getPlaybackState();
        if (state == null) return false;

        return (state.getActions() & PlaybackState.ACTION_SKIP_TO_PREVIOUS) != 0;
    }

    boolean canGoNext() {
        PlaybackState state = controller.getPlaybackState();
        if (state == null) return false;

        return (state.getActions() & PlaybackState.ACTION_SKIP_TO_NEXT) != 0;
    }

    boolean canSeek() {
        PlaybackState state = controller.getPlaybackState();
        if (state == null) return false;

        return (state.getActions() & PlaybackState.ACTION_SEEK_TO) != 0;
    }

    void playPause() {
        if (isPlaying()) {
            controller.getTransportControls().pause();
        } else {
            controller.getTransportControls().play();
        }
    }

    String getName() {
        return name;
    }

    String getAlbum() {
        MediaMetadata metadata = controller.getMetadata();
        if (metadata == null) return "";

        return StringUtils.defaultString(metadata.getString(MediaMetadata.METADATA_KEY_ALBUM));
    }

    String getArtist() {
        MediaMetadata metadata = controller.getMetadata();
        if (metadata == null) return "";

        String artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
        if (TextUtils.isEmpty(artist)) artist = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST);
        if (TextUtils.isEmpty(artist)) artist = metadata.getString(MediaMetadata.METADATA_KEY_AUTHOR);
        if (TextUtils.isEmpty(artist)) artist = metadata.getString(MediaMetadata.METADATA_KEY_WRITER);

        return StringUtils.defaultString(artist);
    }

    String getTitle() {
        MediaMetadata metadata = controller.getMetadata();
        if (metadata == null) return "";

        String title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
        if (TextUtils.isEmpty(title)) title = metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE);
        return StringUtils.defaultString(title);
    }

    void previous() {
        controller.getTransportControls().skipToPrevious();
    }

    void next() {
        controller.getTransportControls().skipToNext();
    }

    void play() {
        controller.getTransportControls().play();
    }

    void pause() {
        controller.getTransportControls().pause();
    }

    void stop() {
        controller.getTransportControls().stop();
    }

    int getVolume() {
        MediaController.PlaybackInfo info = controller.getPlaybackInfo();
        if (info == null) return 0;
        return 100 * info.getCurrentVolume() / info.getMaxVolume();
    }

    void setVolume(int volume) {
        MediaController.PlaybackInfo info = controller.getPlaybackInfo();
        if (info == null) return;

        //Use rounding for the volume, since most devices don't have a very large range
        double unroundedVolume = info.getMaxVolume() * volume / 100.0 + 0.5;
        controller.setVolumeTo((int) unroundedVolume, 0);
    }

    long getPosition() {
        if (controller.getPlaybackState() == null)
            return 0;
        return controller.getPlaybackState().getPosition();
    }

    void setPosition(long position) {
        controller.getTransportControls().seekTo(position);
    }

    long getLength() {
        MediaMetadata metadata = controller.getMetadata();
        if (metadata == null) return 0;

        return metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
    }
}
