package tech.wangjie.voicemanager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

/**
 * 播放语音
 * Created by wangj on 15/8/8.
 */
public class VoicePlayer {

    private static final String TAG = VoicePlayer.class.getSimpleName();
    /**
     * 播放实例
     */
    private static MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    /**
     * 有界面的语音播放
     */
    public synchronized void play(final VoiceTask task, final Runnable scheduleNext) {
        final VoiceListener listener = task.getListener();
        try {
            isPlaying = true;
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            if(mediaPlayer != null && isPlaying){
                mediaPlayer.reset();
            }

            String url = task.getUrl();
            if (url == null) {
                if (listener != null) {
                    listener.onError(task, VoiceError.NullUrl);
                }
            }
            if (listener != null) {
                listener.onStart(task);
            }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "播音完成");
                    mp.reset();
                    isPlaying = false;
                    if (listener != null) {
                        listener.onCompleted(task);
                    }
                    scheduleNext.run();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.reset();
                    isPlaying = false;
                    if (listener != null) {
                        listener.onError(task, VoiceError.NullUrl.setMsg("media player error: " + what + " , extra: " + extra));
                    }
                    scheduleNext.run();
                    return true;
                }
            });

            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

        } catch (Exception e) {
            Log.d(TAG, "播放语音异常 :"+ e.toString());
            e.printStackTrace();
            release();
            if (listener != null) {
                listener.onError(task, VoiceError.MediaError.setMsg("播放语音异常"));
            }
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    static Ringtone rt;

    public static void playRingtone(Context context) {
        if (rt == null) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//系统自带提示音
            rt = RingtoneManager.getRingtone(context, uri);
        }
        rt.play();
    }
}
