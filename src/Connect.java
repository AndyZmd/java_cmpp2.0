import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cmpp.CMPP_DATABASE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
* cmpp客户端发送接收功能
* */
public class Connect extends Thread{
    private static final Logger logger = LogManager.getLogger();
    GetListByTime connect = new GetListByTime();
    public List<CMPP_DATABASE> list;
    public CmppSocket cs;
    public String nowdate;
    public Connect(List<CMPP_DATABASE> list,CmppSocket cs,String nowdate){
        this.list = list;
        this.cs = cs;
        this.nowdate = nowdate;
    }
    @Override
    public void run() {
            //判断短信的长短
            //判断短信内容是否为空或超出总长度
            //根据移动的SMPP协议短信的总长度不能超过140*255字节，我们这里规定不能超过140*100字节，也就是说不能超过7000个汉字
            List<Integer> idlist = new ArrayList<>();//发送失败不在重新发送的短信(服务器验证错误，短信本身问题)
            List<Integer> tidlist = new ArrayList<>();//发送成功的短信
            List<Integer> zidlist = new ArrayList<>();//发送失败 可以重新发送的短信(服务器连接不通畅导致发送失败)
            logger.info("线程开始执行----信息长度=="+list.size());
            //更新数据列表状态
            try {
                connect.update(list,nowdate);
            } catch (SQLException e) {
                logger.info("connect错误4---短信发送前数据更新出现异常");
                logger.info(e.getMessage(),e);
                System.exit(1);
            }
        for (int i = 0; i < list.size(); i++) {
                    byte[] l = list.get(i).getSendcontent().getBytes();
                    if (l.length <= 124) {
                        int errorId = 0;//发送短短信
                        try {
                            errorId = cs.sendShortMsg(list.get(i).getId(), list.get(i).getSendcontent(), list.get(i).getPhone());
                        } catch (IOException e) {
                            logger.info("connect错误1---发送过程中出现异常--"+e.getMessage());
                            zidlist.add(errorId);//该时间发送失败的ID列表
                            continue;
                        }
                        if (errorId != 0000) {
                            idlist.add(errorId);//该时间发送失败的ID列表
                        }else{
                            tidlist.add(list.get(i).getId());//该时间发送成功的ID列表
                        }
                        //System.out.println("短短信发送成功");
                        //cs.CMPP_DELIVER();//不用这个判断  用submit和submit_resp判断是否成功
                    } else if (l.length > 124 && l.length < 14000) {
                        int errorId = 0;//发送长短信
                        try {
                            errorId = cs.sendLongMsg(list.get(i).getId(), list.get(i).getSendcontent(), list.get(i).getPhone());
                        } catch (IOException e) {
                            logger.info("connect错误1---发送过程中出现异常--"+e.getMessage());
                            zidlist.add(errorId);//该时间发送失败的ID列表
                            continue;
                        }
                        if (errorId != 0000) {
                            idlist.add(errorId);//该时间发送失败的ID列表
                        }
                        else{
                            tidlist.add(list.get(i).getId());//该时间发送成功的ID列表
                        }
                        //System.out.println("长短信发送成功");
                        //cs.CMPP_DELIVER();
                    } else {
                        logger.info("该-"+nowdate+"-时段ID为"+list.get(i).getId()+"的信息长度超过最大限制或信息为空。");
                    }
                    try {
                        sleep(200);//一秒发5条  过快 会报错
                    } catch (InterruptedException e) {
                        logger.info("connect错误3---线程流程错误");
                        logger.info(e.getMessage(),e);
                    }
                }
                //开始标记发送失败的短信
                logger.info("本时段-"+nowdate+"-短信发送完成，共发送短信"+list.size()+"条，发送失败"+ idlist.size() + "条，发送成功"+tidlist.size()+"条");
                try {
                    connect.updateall(idlist);
                    if(zidlist.size()>0){
                        connect.updateto0(zidlist);
                        logger.info("下一时段将重新发送该"+zidlist.size()+"条信息");
                    }
                    //connect.update(tidlist,nowdate);
                } catch (SQLException e) {
                    logger.info("connect错误2---更新数据过程中出现异常，程序将关闭重启");
                    logger.info(e.getMessage(),e);
                    System.exit(1);
                }
                logger.info("本时段数据库信息更新完成");
            }

}