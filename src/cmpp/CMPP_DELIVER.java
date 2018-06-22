package cmpp;

/**
 * Created by zmd on 2017/12/5/005.
 */

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;



public class CMPP_DELIVER extends Message_Header{
    private long Msg_Id;
    private String Dest_Id;
    private String Service_Id;
    private byte TP_pid=0x00;
    private byte TP_udhi=0x00;
    private byte Msg_Fmt=15;
    private String Src_terminal_Id;
    private byte Registered_Delivery=0x00;
    private byte Msg_Length;
    private String Msg_Content;
    private String Reserved;


    private long Msg_Id2;
    private String Stat;
    private String Submit_time;
    private String Done_time;
    private String Dest_terminal_Id;
    private int SMSC_sequence;

    public CMPP_DELIVER(byte[] data){
        ByteArrayInputStream bins=new ByteArrayInputStream(data);
        DataInputStream dins=new DataInputStream(bins);

        this.setTotalLength(data.length+4);
        try {
            this.setCommandId(dins.readInt());
            this.setSequenceId(dins.readInt());
            this.Msg_Id=dins.readLong();
            byte[] dest_Idbyte=new byte[21];
            dins.read(dest_Idbyte);
            this.Dest_Id=new String(dest_Idbyte);
            byte[] Service_IdByte=new byte[10];
            dins.read(Service_IdByte);
            this.Service_Id=new String(Service_IdByte);
            this.TP_pid=dins.readByte();
            this.TP_udhi=dins.readByte();
            this.Msg_Fmt=dins.readByte();
            byte[] Src_terminal_IdByte=new byte[21];
            dins.read(Src_terminal_IdByte);
            this.Src_terminal_Id=new String(Src_terminal_IdByte);
            this.Registered_Delivery=dins.readByte();
            this.Msg_Length=dins.readByte();
            byte[] Msg_ContentByte=new byte[Msg_Length];
            dins.read(Msg_ContentByte);

            if(Registered_Delivery==1){
                this.Msg_Content=new String(Msg_ContentByte,"gb2312");
                if(Msg_Length==8+7+10+10+21+4){
                    ByteArrayInputStream binsc=new ByteArrayInputStream(data);
                    DataInputStream dinsc=new DataInputStream(binsc);
                    this.Msg_Id2=dinsc.readLong();
                    byte[] startByte=new byte[7];
                    dinsc.read(startByte);
                    this.Stat=new String(startByte,"gb2312");
                    byte[] Submit_timeByte=new byte[10];
                    dinsc.read(Submit_timeByte);
                    this.Submit_time=new String(Submit_timeByte,"gb2312");
                    byte[] Done_timeByte=new byte[10];
                    dinsc.read(Done_timeByte);
                    this.Done_time=new String(Done_timeByte,"gb2312");
                    byte[] Dest_terminal_IdByte=new byte[21];
                    dinsc.read(Dest_terminal_IdByte);
                    this.Dest_terminal_Id=new String(Dest_terminal_IdByte,"gb2312");
                    this.SMSC_sequence=dinsc.readInt();
                    dinsc.close();
                    binsc.close();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.print("还没有接收到MO或者状态报告");
        }

    }






    public long getMsg_Id() {
        return Msg_Id;
    }
    public void setMsg_Id(long msgId) {
        Msg_Id = msgId;
    }
    public String getDest_Id() {
        return Dest_Id;
    }
    public void setDest_Id(String destId) {
        Dest_Id = destId;
    }
    public String getService_Id() {
        return Service_Id;
    }
    public void setService_Id(String serviceId) {
        Service_Id = serviceId;
    }
    public byte getTP_pid() {
        return TP_pid;
    }
    public void setTP_pid(byte tPPid) {
        TP_pid = tPPid;
    }
    public byte getTP_udhi() {
        return TP_udhi;
    }
    public void setTP_udhi(byte tPUdhi) {
        TP_udhi = tPUdhi;
    }
    public byte getMsg_Fmt() {
        return Msg_Fmt;
    }
    public void setMsg_Fmt(byte msgFmt) {
        Msg_Fmt = msgFmt;
    }
    public String getSrc_terminal_Id() {
        return Src_terminal_Id;
    }
    public void setSrc_terminal_Id(String srcTerminalId) {
        Src_terminal_Id = srcTerminalId;
    }
    public byte getRegistered_Delivery() {
        return Registered_Delivery;
    }
    public void setRegistered_Delivery(byte registeredDelivery) {
        Registered_Delivery = registeredDelivery;
    }
    public byte getMsg_Length() {
        return Msg_Length;
    }
    public void setMsg_Length(byte msgLength) {
        Msg_Length = msgLength;
    }
    public String getMsg_Content() {
        return Msg_Content;
    }
    public void setMsg_Content(String msgContent) {
        Msg_Content = msgContent;
    }
    public String getReserved() {
        return Reserved;
    }
    public void setReserved(String reserved) {
        Reserved = reserved;
    }
    public long getMsg_Id2() {
        return Msg_Id2;
    }
    public void setMsg_Id2(long msgId2) {
        Msg_Id2 = msgId2;
    }
    public String getStat() {
        return Stat;
    }
    public void setStat(String stat) {
        Stat = stat;
    }
    public String getSubmit_time() {
        return Submit_time;
    }
    public void setSubmit_time(String submitTime) {
        Submit_time = submitTime;
    }
    public String getDone_time() {
        return Done_time;
    }
    public void setDone_time(String doneTime) {
        Done_time = doneTime;
    }
    public String getDest_terminal_Id() {
        return Dest_terminal_Id;
    }
    public void setDest_terminal_Id(String destTerminalId) {
        Dest_terminal_Id = destTerminalId;
    }
    public int getSMSC_sequence() {
        return SMSC_sequence;
    }
    public void setSMSC_sequence(int sMSCSequence) {
        SMSC_sequence = sMSCSequence;
    }




}
