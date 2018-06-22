package cmpp;

/**
 * Created by zmd on 2017/12/5/005.
 */

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class CMPP_SUBMIT_RESP extends Message_Header{
    private long Msg_Id;
    private byte Result;
    private int ResultInt;

    public CMPP_SUBMIT_RESP(byte[] data){
        //System.out.println("kkk  sdata;::"+data);
        ByteArrayInputStream bins=new ByteArrayInputStream(data);
        DataInputStream dins=new DataInputStream(bins);
        try {
            this.setTotalLength(data.length+4);
            //System.out.println("kkk  data.length;::"+data.length+4);
            this.setCommandId(dins.readInt());
            this.setSequenceId(dins.readInt());
            //String byte bid=dins.readInt();
            this.Msg_Id=dins.readLong();
            this.Result=dins.readByte();
            this.ResultInt=this.Result & 0xFF;

            //this.Result=dins.readInt();
            dins.close();
            bins.close();
        } catch (IOException e){}
    }


    public long getMsg_Id() {
        return Msg_Id;
    }


    public void setMsg_Id(long msgId) {
        Msg_Id = msgId;
    }


    public byte getResult() {
        return Result;
   }
    public int getResultInt() {
        return ResultInt;
    }


    public void setResult(byte result) {
        Result = result;
    }


}