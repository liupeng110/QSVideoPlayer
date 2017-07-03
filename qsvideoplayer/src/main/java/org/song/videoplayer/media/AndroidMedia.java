package org.song.videoplayer.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by song on 2017/2/10.
 * 安卓系统硬解
 */

public class AndroidMedia extends BaseMedia implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnVideoSizeChangedListener {

    public MediaPlayer mediaPlayer;

    public AndroidMedia(IMediaCallback iMediaCallback) {
        super(iMediaCallback);
    }

    /////////////以下MediaPlayer控制/////////////
    @Override
    public void doPrepar(Context context, String url, Map<String, String> headers) {
        try {
            release();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Class<MediaPlayer> clazz = MediaPlayer.class;
            Method method = clazz.getDeclaredMethod("setDataSource", String.class, Map.class);
            method.invoke(mediaPlayer, url, headers);//反射不传context
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.prepareAsync();
            //mediaPlayer.setDisplay();
        } catch (Exception e) {
            e.printStackTrace();
            onError(mediaPlayer, 10086, 10086);
        }
    }

    @TargetApi(14)
    @Override
    public void setSurface(Surface surface) {
        try {
            if (mediaPlayer != null)
                mediaPlayer.setSurface(surface);
            this.surface = surface;
        } catch (Exception e) {
            e.printStackTrace();
            onError(mediaPlayer, 10010, 10010);
        }
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        try {
            if (mediaPlayer != null)
                mediaPlayer.setDisplay(surfaceHolder);
            if (surfaceHolder != null)
                this.surface = surfaceHolder.getSurface();
        } catch (Exception e) {
            e.printStackTrace();
            onError(mediaPlayer, 10010, 10010);
        }
    }

    @Override
    public void doPlay() {
        if (!isPrepar)
            return;
        mediaPlayer.start();
    }

    @Override
    public void doPause() {
        if (!isPrepar)
            return;
        mediaPlayer.pause();
    }

    @Override
    public void seekTo(int duration) {
        if (!isPrepar)
            return;
        mediaPlayer.seekTo(duration);
    }

    @Override
    public int getCurrentPosition() {
        if (!isPrepar)
            return 0;
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getDuration() {
        if (!isPrepar)
            return 0;
        try {
            return mediaPlayer.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getVideoHeight() {
        if (!isPrepar)
            return 0;
        return mediaPlayer.getVideoHeight();
    }

    @Override
    public int getVideowidth() {
        if (!isPrepar)
            return 0;
        return mediaPlayer.getVideoWidth();
    }

    @Override
    public boolean isPlaying() {
        if (!isPrepar)
            return false;
        return mediaPlayer.isPlaying();
    }

    @Override
    public void release() {
        if (mediaPlayer != null)
            mediaPlayer.release();
        mediaPlayer = null;
        this.surface = null;
        isPrepar = false;
    }

    /////////////以下MediaPlayer回调//////////////
    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepar = true;
        iMediaCallback.onPrepared(this);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        iMediaCallback.onCompletion(this);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        isPrepar = false;
        iMediaCallback.onError(this, what, extra);
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        iMediaCallback.onBufferingUpdate(this, percent / 100.0f);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        iMediaCallback.onInfo(this, what, extra);
        return false;
    }


    @Override
    public void onSeekComplete(MediaPlayer mp) {
        iMediaCallback.onSeekComplete(this);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        iMediaCallback.onVideoSizeChanged(this, width, height);
    }

}
