package teach.wangjie.voicemanager;

/**
 *
 * Created by wangj on 2015/12/18 0018.
 */
public abstract class VoiceListener {

    public void onStart(VoiceTask task){}

    public abstract void onCompleted(VoiceTask task);

    public void onError(VoiceTask task, VoiceError error){}
}
