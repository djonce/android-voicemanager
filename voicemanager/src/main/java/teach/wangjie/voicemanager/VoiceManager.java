package teach.wangjie.voicemanager;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 播放语音管理器
 *
 * Created by wangj on 2015/12/18
 */
public class VoiceManager {
    public static final String TAG = VoiceManager.class.getSimpleName();

    private static ConcurrentLinkedQueue<VoiceTask> taskQueue = new ConcurrentLinkedQueue<>();
    private VoicePlayer voicePlayer = new VoicePlayer();

    public static VoiceManager voiceManager;
    private VoiceTask currentTask;

    public static VoiceManager getInstance() {

        if (voiceManager == null) {
            synchronized (VoiceManager.class) {
                if (voiceManager == null) {
                    voiceManager = new VoiceManager();
                }
            }
        }
        return voiceManager;
    }

    /**
     * 添加任务:推送时调用
     */
    public synchronized void submit(VoiceTask task) {
        try {
            taskQueue.add(task);
            if (!voicePlayer.isPlaying()) {
                takeOneToPlay();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 立即播放:点击时调用
     */
    public synchronized void executeNow(final VoiceTask task) {
        currentTask = task;
        voicePlayer.play(task, new Runnable() {
            @Override
            public void run() {
                currentTask = null;
                if (!task.isCanceled()) {
                    takeOneToPlay();
                }
            }
        });
    }

    /**
     * 清除对立中任务
     */
    public synchronized void removeTask(VoiceTask task) {
        taskQueue.remove(task);
    }

    /**
     * 清空所有的任务，未播放完继续播放
     */
    public void clearTask() {
        if (taskQueue != null) {
            taskQueue.clear();
        }
    }

    /**
     * 清空所有的任务，并停止播放。
     */
    public void clearAndRelease() {
        if (voiceManager != null) {
            if (taskQueue != null) {
                taskQueue.clear();
            }
            voicePlayer.release();
            voiceManager = null;
        }
    }

    /**
     * 立即停止当前
     */
    public void cancelCurrentTask(VoiceTask task) {
        if (task == null || task.getUrl() == null || currentTask == null) {
            return;
        }
        if (currentTask == task || task.getUrl().equals(currentTask.getUrl())) {
            cancelCurrentTask();
        }
    }

    /**
     * 立即停止播放当前
     */
    public void cancelCurrentTask() {
        if (currentTask != null) {
            currentTask.setCanceled(true);
        }
        voicePlayer.release();
    }

    /**
     * 立即播放下一个，如果正在播放，会中断当前播放。
     */
    public void startTask() {
        takeOneToPlay();
    }

    /**
     * 执行一个任务
     */
    private void takeOneToPlay() {
        VoiceTask task = taskQueue.poll();
        if (task != null) {
            // 有任务执行
            executeNow(task);
            Log.i(TAG, "当前剩余任务数：" + taskQueue.size());
        } else {
            Log.i(TAG, "没有可执行的任务了");
        }
    }

}
