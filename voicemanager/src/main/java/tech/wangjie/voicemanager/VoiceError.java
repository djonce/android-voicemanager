package tech.wangjie.voicemanager;

/**
 *
 * Created by wangj on 2015/12/18
 */
public enum VoiceError {

    NullUrl(1, "文件地址为空"),
    MediaError(2, "系统播放器异常");

    private int code;
    private String des;
    private String msg;

    VoiceError(int code, String des) {
        this.code = code;
        this.des = des;
    }

    public String getMsg() {
        return msg;
    }

    public VoiceError setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getDes() {
        return des;
    }

    public VoiceError setDes(String des) {
        this.des = des;
        return this;
    }

    public int getCode() {
        return code;
    }

    public VoiceError setCode(int code) {
        this.code = code;
        return this;
    }
}
