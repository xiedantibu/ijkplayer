/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2013 Zhang Rui <bbcallen@gmail.com>
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

package tv.danmaku.ijk.media.player;

import java.io.IOException;
import java.lang.ref.WeakReference;

import tv.danmaku.ijk.media.player.misc.IMediaAudioStreamType;
import tv.danmaku.ijk.media.player.misc.IMediaWakeMode;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.Surface;
import android.view.SurfaceHolder;

public final class AndroidMediaPlayer extends BaseMediaPlayer implements
        IMediaWakeMode, IMediaAudioStreamType {
    private MediaPlayer mInternalMediaPlayer;
    private AndroidMediaPlayerListenerHolder mInternalListenerAdapter;

    public AndroidMediaPlayer() {
        mInternalMediaPlayer = new MediaPlayer();
        mInternalListenerAdapter = new AndroidMediaPlayerListenerHolder(this);
        attachInternalListeners();
    }

    @Override
    public void setDisplay(SurfaceHolder sh) {
        mInternalMediaPlayer.setDisplay(sh);
    }

    @Override
    public void setSurface(Surface surface) {
        mInternalMediaPlayer.setSurface(surface);
    }

    @Override
    public void setDataSource(String path) throws IOException,
            IllegalArgumentException, SecurityException, IllegalStateException {
        mInternalMediaPlayer.setDataSource(path);
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mInternalMediaPlayer.prepareAsync();
    }

    @Override
    public void start() throws IllegalStateException {
        mInternalMediaPlayer.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        mInternalMediaPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        mInternalMediaPlayer.pause();
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        mInternalMediaPlayer.setScreenOnWhilePlaying(screenOn);
    }

    @Override
    public int getVideoWidth() {
        return mInternalMediaPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mInternalMediaPlayer.getVideoHeight();
    }

    @Override
    public boolean isPlaying() {
        return mInternalMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        mInternalMediaPlayer.seekTo((int) msec);
    }

    @Override
    public long getCurrentPosition() {
        return mInternalMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mInternalMediaPlayer.getDuration();
    }

    @Override
    public void release() {
        mInternalMediaPlayer.release();

        resetListeners();
        attachInternalListeners();
    }

    @Override
    public void reset() {
        mInternalMediaPlayer.reset();

        resetListeners();
        attachInternalListeners();
    }

    /*--------------------
     * IMediaPlayer2
     */
    @Override
    public void setWakeMode(Context context, int mode) {
        mInternalMediaPlayer.setWakeMode(context, mode);
    }

    @Override
    public void setAudioStreamType(int streamtype) {
        mInternalMediaPlayer.setAudioStreamType(streamtype);
    }

    /*--------------------
     * Listeners adapter
     */
    private final void attachInternalListeners() {
        mInternalMediaPlayer.setOnPreparedListener(mInternalListenerAdapter);
        mInternalMediaPlayer
                .setOnBufferingUpdateListener(mInternalListenerAdapter);
        mInternalMediaPlayer.setOnCompletionListener(mInternalListenerAdapter);
        mInternalMediaPlayer
                .setOnSeekCompleteListener(mInternalListenerAdapter);
        mInternalMediaPlayer
                .setOnVideoSizeChangedListener(mInternalListenerAdapter);
        mInternalMediaPlayer.setOnErrorListener(mInternalListenerAdapter);
        mInternalMediaPlayer.setOnInfoListener(mInternalListenerAdapter);
    }

    private class AndroidMediaPlayerListenerHolder implements
            MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
            MediaPlayer.OnBufferingUpdateListener,
            MediaPlayer.OnSeekCompleteListener,
            MediaPlayer.OnVideoSizeChangedListener,
            MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener {
        public WeakReference<AndroidMediaPlayer> mWeakMediaPlayer;

        public AndroidMediaPlayerListenerHolder(AndroidMediaPlayer mp) {
            mWeakMediaPlayer = new WeakReference<AndroidMediaPlayer>(mp);
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            if (self == null)
                return false;

            return notifyOnInfo(what, extra);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            if (self == null)
                return false;

            return notifyOnError(what, extra);
        }

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            if (self == null)
                return;

            notifyOnVideoSizeChanged(width, height, 1, 1);
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            if (self == null)
                return;

            notifyOnSeekComplete();
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            if (self == null)
                return;

            notifyOnBufferingUpdate(percent);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            if (self == null)
                return;

            notifyOnCompletion();
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            if (self == null)
                return;

            notifyOnPrepared();
        }
    }
}
