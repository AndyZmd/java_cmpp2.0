import cmpp.CMPP_DATABASE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by zmd on 2018/2/11.
 *   总调度
 */
public class MyScheduled {
    private static final Logger logger = LogManager.getLogger();
    public static ExecutorService executors = Executors.newFixedThreadPool(5);
    public static ThreadPoolExecutor pool = (ThreadPoolExecutor) executors;
    public static CmppSocket cs = new CmppSocket();
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
    public static SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:00");
    public static int i = 0;
    public static Dom dom = new Dom();
    public static void main(String[] args) throws ParseException {
        String start = dom.getstarttime();//dom.getstarttime();
        String end = dom.getendtime();//dom.getendtime();
        String cut = dom.getcuttime();//dom.getcuttime();
        //创建一个可重用固定线程数的线程池
        GetListByTime connect = new GetListByTime();
        Date starttime = df2.parse(start);//开始取数据时间
        Date endtime = df2.parse(end);//结束取数据时间
        Date cuttime = df2.parse(cut);//早上处理昨夜数据的分割时间

        Date p1 = df.parse("2018-03-02 " + end);
        Date p2 = df.parse("2018-03-03 " + start);
        int l = (int)Math.ceil(((p2.getTime() - p1.getTime()) / (60.00 * 1000.00)) / 30.00);
        while (true) {
            Calendar c = new GregorianCalendar();
            Date date = new Date();
            logger.info("本次任务开始--"+df3.format(date)+"---线程池大小---"+pool.getPoolSize()+"---活动的线程---"+pool.getActiveCount());
            c.setTime(date);//设置参数时间
            c.add(Calendar.SECOND,-60);//把日期往后增加SECOND 秒.整数往后推,负数往前移动
            Date date1=c.getTime(); //这个时间就是日期往后推一天的结果
            String str = df.format(date1);//获得完整时间
            Date nowdate = df2.parse(df2.format(date1));//仅仅获得小时j
            try {
                //如果是当前时间>21点小于第二天7点
                //如果时间=7:31 取出 8-8:30  7:31取出8:30-9
                //如果时间=20:01 停止去数据库
                if(nowdate.getTime()<=endtime.getTime() && nowdate.getTime()>=cuttime.getTime()){
                    List<CMPP_DATABASE> list = connect.getPool(str);//获取当前时段数据列表
                    if(list.size()!=0){
                        String s = cs.connectISMG();//连接cmpp
                        if (s.equals("error")) {
                            //System.out.print("连接cmpp失败，本时段短信无法发送");
                            logger.error("连接cmpp失败，本时段-"+str+"-短信无法发送");
                            //connect.updateforerror(str);
                            Thread.sleep(55000);//50秒 50000
                            continue;
                        }
                        pool.execute(new Connect(list,cs,str));
                        //System.out.println(pool.getTaskCount()+"，本时间段任务完成，执行下一个。线程池大小="+pool.getPoolSize()+"，运行的线程数="+pool.getActiveCount());
                    }
                    else{
                        logger.info("本时段-"+str+"-没有新数据");
                    }
                }else if(nowdate.getTime()<cuttime.getTime() && nowdate.getTime()>=starttime.getTime()){  //给23分钟处理 昨夜(690分钟)的短信数据
                    List<CMPP_DATABASE> list = new ArrayList<CMPP_DATABASE>();
                    if(i<l){
                        Calendar c4 = new GregorianCalendar();
                        c4.setTime(date);
                        c4.add(Calendar.DATE, -1);
                        String format2 = new SimpleDateFormat("yyyy-MM-dd " + end).format(c4.getTime());

                        //获得前一晚的数据时间区间的开始
                        Calendar c2 = new GregorianCalendar();
                        c2.setTime(df.parse(format2));//设置参数时间
                        c2.add(Calendar.MINUTE,30*i);//把日期往后增加MINUTE 分钟.整数往后推,负数往前移动
                        Date date2=c2.getTime();
                        String str2 = df.format(date2);//获得完整时间

                        //获得前一晚的数据时间区间的结束
                        Calendar c3 = new GregorianCalendar();
                        c3.setTime(df.parse(format2));//设置参数时间
                        c3.add(Calendar.MINUTE,30*(i+1));//把日期往后增加MINUTE 分钟.整数往后推,负数往前移动
                        Date date3=c3.getTime();
                        String str3 = df.format(date3);//获得完整时间
                        logger.info("开始处理昨夜数据时间段为:"+str2+"---"+str3);
                        list = connect.getPoolByDay(str,str2,str3);//获取当前时段数据列表
                        i++;
                    }
                    else {
                        list = connect.getPool(str);//获取当前时段数据列表
                    }
                    if(list.size()!=0){
                        String s = cs.connectISMG();//连接cmpp
                        if (s.equals("error")) {
                            //System.out.print("连接cmpp失败，本时段短信无法发送");
                            logger.error("连接cmpp失败，本时段-"+str+"-短信无法发送");
                            //connect.updateforerror(str);
                            Thread.sleep(55000);//50秒 50000
                            continue;
                        }

                        pool.execute(new Connect(list,cs,str));
                        //System.out.println(pool.getTaskCount()+"，本时间段任务完成，执行下一个。线程池大小="+pool.getPoolSize()+"，运行的线程数="+pool.getActiveCount());
                    }
                    else{
                        logger.info("本时段-"+str+"-没有新数据");
                    }
                }else {
                        logger.info("本时段-"+str+"-程序不发送数据");
                        i = 0;
                }
                Thread.sleep(55000);//50秒 50000
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("程序因错误关闭");
                System.exit(1);
            }
        }
        //关闭线程池
        //pool.shutdown();
    }
}
