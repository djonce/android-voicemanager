package com.djonce.voice;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

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
//                mediaPlayer.stop();
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
//            mediaPlayer.setDataSource("http://test.19ba.cn/cyrh.mp3");
            mediaPlayer.prepare();
            mediaPlayer.start();

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

    public static long getAmrDuration(File file) throws IOException {
        long duration = -1;
        int[] packedSize = {12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0};
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            long length = file.length();//文件的长度
            int pos = 6;//设置初始位置
            int frameCount = 0;//初始帧数
            int packedPos = -1;
            /////////////////////////////////////////////////////
            byte[] datas = new byte[1];//初始数据值
            while (pos <= length) {
                randomAccessFile.seek(pos);
                if (randomAccessFile.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }
            /////////////////////////////////////////////////////
            duration += frameCount * 20;//帧数*20
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
        return duration;
    }

    public static long getAmrDuration(byte[] voice) {
        long duration = -1;
        int[] packedSize = {12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0};
        long length = voice.length;//文件的长度
        int pos = 6;//设置初始位置
        int frameCount = 0;//初始帧数
        int packedPos = -1;
        /////////////////////////////////////////////////////
        byte[] datas = new byte[1];//初始数据值
        while (pos <= length) {
            if (pos == length) {
                duration = length > 0 ? ((length - 6) / 650) : 0;
                break;
            }
            datas[0] = voice[pos];
            packedPos = (datas[0] >> 3) & 0x0F;
            pos += packedSize[packedPos] + 1;
            frameCount++;
        }
        /////////////////////////////////////////////////////
        duration += frameCount * 20;//帧数*20
        return duration;
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
