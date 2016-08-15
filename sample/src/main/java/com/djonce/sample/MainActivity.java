package com.djonce.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.djonce.voicemanager.VoiceError;
import com.djonce.voicemanager.VoiceListener;
import com.djonce.voicemanager.VoiceManager;
import com.djonce.voicemanager.VoiceTask;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void addTask(View view) {
        /**
         * 添加任务
         */
        VoiceTask task = new VoiceTask();
        task.setId(0);
        task.setUrl("http://test.19ba.cn/brave.mp3");
        VoiceManager.getInstance().submit(task);

    }
    VoiceTask task;
    public void playTask(View view) {
        task = new VoiceTask();
        task.setId(1000);
        task.setCanceled(false);
        task.setUrl("http://test.19ba.cn/cyrh.mp3");
        task.setListener(new VoiceListener() {
            @Override
            public void onCompleted(VoiceTask task) {
                Log.e(TAG, "播放完成：" + task);
            }

            @Override
            public void onError(VoiceTask task, VoiceError error) {
                super.onError(task, error);
                Log.e(TAG, error.toString());
            }

            @Override
            public void onStart(VoiceTask task) {
                super.onStart(task);
                Log.e(TAG, task.toString());
            }
        });
        VoiceManager.getInstance().executeNow(task);
    }

    public void stopPlayTask(View view) {
        VoiceManager.getInstance().cancelCurrentTask(task);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        VoiceManager.getInstance().clearAndRelease();

    }
}
