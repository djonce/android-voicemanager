package com.djonce.voice;

/**
 *
 * Created by wangj on 2015/12/18 0018.
 */
public class VoiceTask {

    private long id;
    private String url;
    private boolean isCanceled;

    private VoiceListener listener;

    public long getId() {
        return id;
    }

    public VoiceTask setId(long id) {
        this.id = id;
        return this;
    }

    public VoiceTask setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public VoiceTask setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
        return this;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public VoiceTask setListener(VoiceListener listener) {
        this.listener = listener;
        return this;
    }

    public VoiceListener getListener() {
        return listener;
    }
}
