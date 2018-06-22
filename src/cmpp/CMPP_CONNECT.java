package cmpp;

/**
 * Created by zmd on 2017/12/5/005.
 */


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CMPP_CONNECT extends Message_Header{
    private String sourceAddr;//源地址，此处为SP_Id，即SP的企业代码。
    private byte[] authenticatorSource;//用于鉴别源地址。其值通过单向MD5 hash计算得出，表示如下：
                                       //    AuthenticatorSource =
                                       //    MD5（Source_Addr+9 字节的0 +shared secret+timestamp）
                                       //    Shared secret 由中国移动与源地址实体事先商定，timestamp格式为：MMDDHHMMSS，即月日时分秒，10位。

    private byte version;//双方协商的版本号(高位4bit表示主版本号,低位4bit表示次版本号)
    private int timestamp;//时间戳的明文,由客户端产生,格式为MMDDHHMMSS，即月日时分秒，10位数字的整型，右对齐 。

    public byte[] toByteArray(){
        ByteArrayOutputStream bous=new ByteArrayOutputStream();
        DataOutputStream dous=new DataOutputStream(bous);
        try {
            dous.writeInt(this.getTotalLength());
            dous.writeInt(this.getCommandId());
            dous.writeInt(this.getSequenceId());
            MsgUtils.writeString(dous,this.sourceAddr,6);
            dous.write(authenticatorSource);
            dous.writeByte(version);
            //System.out.println("链接服务器时间戳为："+timestamp);
            dous.writeInt(timestamp);
            dous.close();
        } catch (IOException e) {
            System.out.print("封装链接二进制数组失败。");
        }
        return bous.toByteArray();
    }

    public String getSourceAddr() {
        return sourceAddr;
    }
    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }
    public byte[] getAuthenticatorSource() {
        return authenticatorSource;
    }
    public void setAuthenticatorSource(byte[] authenticatorSource) {
        this.authenticatorSource = authenticatorSource;
    }
    public byte getVersion() {
        return version;
    }
    public void setVersion(byte version) {
        this.version = version;
    }
    public int getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

}
