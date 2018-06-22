import cmpp.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zmd on 2017/12/5/005.
 */
public class CmppSocket {
    public static Dom dom = new Dom();
    public static Socket socket;
    public static String IP=dom.getgwip();//服务器IP 10.219.21.4   172.16.101.11  dom.getgwip()
    public static int port=dom.getmtport();//端口号  7890   9966   dom.getmtport()
    public static String spid=dom.getspid();//企业代码 和 spid 一样  905275  2123  dom.getspid()
    public static String secret=dom.getuserpass();//  WYpt5275#  abc@aaa  dom.getuserpass()
    public static String src_Id=dom.getspno();//  dom.getspno()
    public static DataInputStream in;
    public static DataOutputStream out;
    private static final Logger logger = LogManager.getLogger();
    /***
     * 创建指定地址socket连接
     * @return
     */
    public static Socket getSocketInstance(){
        try {
            socket=new Socket(IP,port);
            socket.setKeepAlive(true);
            socket.setSoTimeout(0);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return socket;
    }

    /***
     * connect初始化连接服务器
     * @throws IOException
     */
    public static String connectISMG() throws IOException{
        System.out.println("正在连接到服务器"+IP);
        CMPP_CONNECT connect=new CMPP_CONNECT();

        connect.setTotalLength(12+6+16+1+4);
        connect.setCommandId(0x00000001);//请求连接
        connect.setSequenceId(MsgUtils.getSequence());
        connect.setSourceAddr(spid);//源地址，此处为SP_Id，即SP的企业代码。

        String timestamp=MsgUtils.getTimestamp();
        connect.setAuthenticatorSource(MsgUtils.getAuthenticatorSource(spid, secret, timestamp));//用于鉴别源地址。其值通过单向MD5 hash计算得出
        connect.setTimestamp(Integer.parseInt(timestamp));//时间戳的明文,由客户端产生,格式为MMDDHHMMSS，即月日时分秒，10位数字的整型，右对齐 。
        //connect.setVersion((byte)0x20);//双方协商的版本号(高位4bit表示主版本号,低位4bit表示次版本号)
        connect.setVersion((byte)0x14);//双方协商的版本号(高位4bit表示主版本号,低位4bit表示次版本号)
        List<byte[]> dataList=new ArrayList<byte[]>();
        dataList.add(connect.toByteArray());
        out=new DataOutputStream(getSocketInstance().getOutputStream());
        if(out!=null&&null!=dataList){
            for(byte[]data:dataList){
                out.write(data);
                out.flush();
                //System.out.println("connect数据发送完成,已成功建立与服务器的连接");
            }
        }
        in=new DataInputStream(socket.getInputStream());
        int len=in.readInt();
        List<byte[]> getData=new ArrayList<byte[]>();
        if(null!=in&&0!=len){
            byte[] data=new byte[len-4];
            in.read(data);
            getData.add(data);
            for(byte[] returnData:getData){
                Message_Header header=new Message_Header(returnData);
                switch(header.getCommandId()){
                    case 0x80000001:
                        CMPP_CONNECT_RESP connectResp=new CMPP_CONNECT_RESP(returnData);
                        CMPP_CODEMSG codeMsg=new CMPP_CODEMSG();
                        String code=String.valueOf(connectResp.getStatus());
                        String msg=codeMsg.getCodeMsg(code);
                        //System.out.println("CMPP初始化链接状态值["+code+"]=>"+msg);
                        if(!code.equals("0")){
                            return "error";
                        }else {
                            return "true";
                        }
                }
            }
        }
        return "error";
    }
    /***
     * 发送短短信
     * @param msg
     * @param phoneNumber
     * @throws IOException
     */
    public static int sendShortMsg(int msgid,String msg,String phoneNumber) throws IOException{
        int seq=MsgUtils.getSequence();
        try {
            byte[] msgByte=msg.getBytes("gb2312");
            CMPP_SUBMIT submit=new CMPP_SUBMIT();
            submit.setTotalLength(159+msgByte.length);
            submit.setCommandId(0x00000004);//提交短信
            submit.setSequenceId(seq);
            submit.setService_Id("cmcctest");
            submit.setMsg_Id(0x10123);//信息标识，由SP接入的短信网关本身产生，本处填空。
            submit.setMsg_src(spid);//账户 信息内容来源(SP_Id)
            submit.setSrc_Id(src_Id);//通道号 源号码  SP的服务代码或前缀为服务代码的长号码, 10658562
            // 网关将该号码完整的填到SMPP协议Submit_SM消息相应的source_addr字段，
            // 该号码最终在用户手机上显示为短消息的主叫号码
            submit.setFee_terminal_Id(phoneNumber);//被计费用户的号码
            submit.setDest_terminal_Id(phoneNumber);//接收短信的MSISDN号码
            submit.setMsg_Length((byte)msgByte.length);//信息长度(Msg_Fmt值为0时：<160个字节；其它<=140个字节)
            submit.setMsg_Content(msgByte);//信息内容
            List<byte[]> dataList=new ArrayList<byte[]>();
            dataList.add(submit.toByteArray());
            //System.out.println("phoneNumber===>"+phoneNumber);
            if(out!=null&&null!=dataList){
                for(byte[]data:dataList){
                    out.write(data);
                    out.flush();
                    //System.out.println("submit数据发送完成");
                }
            }

            in=new DataInputStream(socket.getInputStream());
            int len=in.readInt();
            List<byte[]> getData=new ArrayList<byte[]>();
            if(null!=in&&0!=len){
                byte[] data=new byte[len-4];
                in.read(data);
                getData.add(data);
                for(byte[] returnData:getData){
                    Message_Header header=new Message_Header(returnData);
                    //System.out.println("header.getCommandId()===>"+header.getCommandId());
                    switch(header.getCommandId()){
                        case 0x80000004:
                            CMPP_SUBMIT_RESP submitResp=new CMPP_SUBMIT_RESP(returnData);
                            //System.out.println("submitresp状态值"+submitResp.getResult());
                            //System.out.println("submitresp流水号值"+submitResp.getMsg_Id());

                            CMPP_CODEMSG codeMsg=new CMPP_CODEMSG();
                            String codeStr=String.valueOf(submitResp.getResultInt());;
                            String msgStr=codeMsg.getCodeMsg(codeStr);
                            //System.out.println("CMPP返回===>：["+codeStr+"]=>"+msgStr);
                            if(!codeStr.equals("0")){
                                logger.error("发生错误错误信息为：["+codeStr+"]=>"+msgStr);
                                return msgid;
                            }
                            else {
                                return 0000;
                            }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            logger.error("信息发送过程中发生连接错误--"+e.getMessage());
            return msgid;
        }
        return msgid;
    }
    /***
     * 获取上行及状态报告回执信息
     * @throws IOException
     */
    public static void CMPP_DELIVER() throws IOException {
            in=new DataInputStream(socket.getInputStream());
            int len=in.readInt();
            List<byte[]> getData=new ArrayList<byte[]>();
            if(null!=in&&0!=len){
                byte[] data=new byte[len-4];
                in.read(data);
                getData.add(data);
                //System.out.println(getData.size()+"~~~~~~~~~~~~~~~~");
                for(byte[] returnData:getData){
                    //System.out.println("获取状态报告中......");
                    CMPP_DELIVER deliver=new CMPP_DELIVER(returnData);
                    //System.out.println(returnData.length);
                    switch(deliver.getCommandId()){
                        case 0x00000005:
                            //CMPP_SUBMIT_RESP submitResp=new CMPP_SUBMIT_RESP(returnData);
                            //System.out.println("获取状态报告成功！");
                            //System.out.println("CMPP_DELIVER状态值"+deliver.getStat());
                            //System.out.println("CMPP_DELIVER流水号值"+deliver.getMsg_Id2());
                    }
                }
            }
    }
    /***
     * 长连接链路检测
     * @throws IOException
     */
    public static void CMPP_ACTIVE_TEST() throws IOException{
        Message_Header header=new Message_Header();
        header.setTotalLength(12);
        header.setCommandId(0x00000008);
        header.setSequenceId(MsgUtils.getSequence());
        List<byte[]> dataList=new ArrayList<byte[]>();
        dataList.add(header.toByteArray());
        if(out!=null&&null!=dataList){
            for(byte[]data:dataList){
                out.write(data);
                out.flush();
                System.out.println("长连接链路检测中......");
            }
        }
    }
    /***
     * 发送长短信
     * @param  // zmd --2018 2- 12
     * @param phoneNumber
     * @throws IOException
     * //拆分短信内容（移动规定每条短信不能超过70个汉字，如果是超长短信则还要包括6字节的协议头此处定义为不能超过62个汉字）
     * //判断短信内容是否为空或超出总长度（根据移动的SMPP协议短信的总长度不能超过140*255字节，我们这里规定不能超过140*100字节，也就是说不能超过7000个汉字）
     */
    public static int sendLongMsg(int msgid,String msg, String phoneNumber) throws IOException{
        int seq=MsgUtils.getSequence();
        try {
            //切分短信内容
            List<byte[]> list = longmsg(msg);
            for(byte[] msgByte:list){
            CMPP_SUBMIT submit = new CMPP_SUBMIT();
            submit.setTP_udhi((byte) 0x01);
            submit.setPk_total(msgByte[4]);
            submit.setPk_number(msgByte[5]);
            submit.setMsg_Fmt((byte) 0x08);
            submit.setTotalLength(159 + msgByte.length);
            submit.setCommandId(0x00000004);//提交短信
            submit.setSequenceId(seq);
            submit.setService_Id("cmcctest");
            submit.setMsg_Id(0x10123);//信息标识，由SP接入的短信网关本身产生，本处填空。
            submit.setMsg_src(spid);//账户 信息内容来源(SP_Id)
            submit.setSrc_Id(src_Id);//通道号 源号码  SP的服务代码或前缀为服务代码的长号码, 10658562
            // 网关将该号码完整的填到SMPP协议Submit_SM消息相应的source_addr字段，
            // 该号码最终在用户手机上显示为短消息的主叫号码
            submit.setFee_terminal_Id(phoneNumber);//被计费用户的号码
            submit.setDest_terminal_Id(phoneNumber);//接收短信的MSISDN号码
            submit.setMsg_Length((byte) msgByte.length);//信息长度(Msg_Fmt值为0时：<160个字节；其它<=140个字节)
            submit.setMsg_Content(msgByte);//信息内容
            List<byte[]> dataList = new ArrayList<byte[]>();
            dataList.add(submit.toByteArray());
            //System.out.println("接收短信的手机号码为:" + phoneNumber);
            if (out != null && null != dataList) {
                for (byte[] data : dataList) {
                    out.write(data);
                    out.flush();
                    //System.out.println("submit信息数据发送完成");
                }
            }
            //获得服务器的返回信息
            in=new DataInputStream(socket.getInputStream());
            int len=in.readInt();
            List<byte[]> getData=new ArrayList<byte[]>();
            if(null!=in&&0!=len){
                byte[] data=new byte[len-4];
                in.read(data);
                getData.add(data);
                for(byte[] returnData:getData){
                    Message_Header header=new Message_Header(returnData);
                    //System.out.println("消息头ID===>"+header.getCommandId());
                    switch(header.getCommandId()){
                        case 0x80000004:
                            CMPP_SUBMIT_RESP submitResp=new CMPP_SUBMIT_RESP(returnData);
                            //System.out.println("submitresp状态值"+submitResp.getResult());
                            //System.out.println("submitresp流水号值"+submitResp.getMsg_Id());

                            CMPP_CODEMSG codeMsg=new CMPP_CODEMSG();
                            String codeStr=String.valueOf(submitResp.getResultInt());;
                            String msgStr=codeMsg.getCodeMsg(codeStr);
                            //System.out.println("CMPP返回===>：["+codeStr+"]=>"+msgStr);
                            if(!codeStr.equals("0")){
                                logger.error("发生错误错误信息为：["+codeStr+"]=>"+msgStr);
                                return msgid;
                            }
                    }
                }
            }
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            logger.error("信息发送过程中发生连接错误--"+e.getMessage());
            return msgid;
        }
        return 0000;
    }
    /*
        处理长短信
        zmd------2018-2-12
     */
    public static List<byte[]> longmsg(String msg) throws UnsupportedEncodingException {
        List<byte[]> list = new ArrayList<byte[]>();
        byte[] messageUCS2 = msg.getBytes("UnicodeBigUnmarked");
        int messageUCS2Len = messageUCS2.length;
        //System.out.println("该短信总长:"+messageUCS2Len+"");
        int maxMessageLen = 124;
        int messageUCS2Count = messageUCS2Len / (maxMessageLen - 6) + 1;
            //长短信分为多少条发送
            //System.out.println("短信将分为"+messageUCS2Count+"条发送");
            byte[] tp_udhiHead = new byte[6];
            tp_udhiHead[0] = 0x05;
            tp_udhiHead[1] = 0x00;
            tp_udhiHead[2] = 0x03;
            tp_udhiHead[3] =//0x0A;
                    tp_udhiHead[4] = (byte)messageUCS2Count;
            tp_udhiHead[5] = 0x01;
            //默认为第一条
            for (int i = 0; i < messageUCS2Count; i++)
            {
                tp_udhiHead[5] = (byte)(i + 1);
                byte[] msgContent;
                if (i != messageUCS2Count - 1)
                {
                    //不为最后一条
                    msgContent =byteAdd(tp_udhiHead, messageUCS2, i * (maxMessageLen - 6), (i + 1) * (maxMessageLen - 6));
                    list.add(msgContent);
                }
                else
                {
                    msgContent = byteAdd(tp_udhiHead, messageUCS2, i * (maxMessageLen - 6), messageUCS2Len);
                    list.add(msgContent);
                }
            }
            return list;
    }
    //长信息分割
    public static byte[] byteAdd(byte[] tpUdhiHead, byte[] messageUCS2, int i, int j) {
        byte[] msgb = new byte[j-i+6];
        System.arraycopy(tpUdhiHead,0,msgb,0,6);
        System.arraycopy(messageUCS2,i,msgb,6,j-i);
        return  msgb;
    }
    /**
     * @param args
     */
//    public static void main(String[] args) {
//        // TODO Auto-generated method stub   cmpp功能测试用方法
////        try {
////            connectISMG();//连接cmpp
////            //短短信最多 126字节  还有6个字节的协议头 不超过60个汉字
////            sendLongMsg("网网优中心网网网优中心网络集中网优中心网络集中分析网网优中心网络集中网优中心网络集中分析络集中网优中心网络集中分析管理系统给您派发工单年日前进行处理网优中心网络集中分析管理系统给您派发工单年日前进行处理","18247184358");
////            if(in!=null){
////                int a=0;
////                try {
////                    while (true) {
////                        CMPP_ACTIVE_TEST();
////                        CMPP_DELIVER();//上行接收 有问题 服务端 编码压缩错误
////                        Thread.sleep(1000*3);//3秒查一次
////                    }
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }
////        } catch (IOException e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        }
//    }

}
