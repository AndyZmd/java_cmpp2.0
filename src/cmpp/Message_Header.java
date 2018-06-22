package cmpp;
/**
 * Created by zmd on 2017/12/5/005.
 */


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;



public class Message_Header {

    private int totalLength;//Unsigned Integer
    private int commandId;//Unsigned Integer
    private int sequenceId;//Unsigned Integer
    public byte[] toByteArray(){
        ByteArrayOutputStream bous=new ByteArrayOutputStream();
        DataOutputStream dous=new DataOutputStream(bous);

        try {
            dous.writeInt(this.getTotalLength());
            dous.writeInt(this.getCommandId());
            dous.writeInt(this.getSequenceId());
            dous.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("封装CMPP消息头二进制数组失败。");
        }

        return bous.toByteArray();

    }

    public Message_Header(byte[] data){
        ByteArrayInputStream bins=new ByteArrayInputStream(data);
        DataInputStream dins=new DataInputStream(bins);
        try {
            this.setTotalLength(data.length+4);
            this.setCommandId(dins.readInt());
            this.setSequenceId(dins.readInt());
            dins.close();
            bins.close();
        } catch (IOException e){}
    }

    public Message_Header(){
        super();
    }

    public int getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

}
