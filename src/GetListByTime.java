import cmpp.CMPP_DATABASE;
import com.alibaba.druid.pool.DruidPooledConnection;
import jdbc.DbPoolConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zmd on 2018/2/11.
 * 连接数据库连接池 查询数据 更新数据mycmpp.dbo.cmpp
 * select * from mycmpp.dbo.cmpp where  istatus = '0'and  DATEDIFF(MI,gentime,'"+str+"')=0
 * mycmpp.dbo.cmpp
 */
public class GetListByTime{
    private static final Logger logger = LogManager.getLogger();
    public static DbPoolConnection dbp = DbPoolConnection.getInstance();
    public DruidPooledConnection con = null;

    public List<CMPP_DATABASE> getPool(String str) {
        List<CMPP_DATABASE> list = new ArrayList<CMPP_DATABASE>();
        try {
            con = dbp.getConnection();
            //取出前5分钟的数据
            PreparedStatement ps = con.prepareStatement("select * from NMConDB.dbo.SmsSender where  istatus = '0'and  DATEDIFF(MI,gentime,'"+str+"')<=5 and DATEDIFF(MI,gentime,'"+str+"')>=0");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new CMPP_DATABASE(rs.getInt(1), rs.getString(3), rs.getString(4)));
            }
            //JdbcUtils.printResultSet(resultSet);
            ps.close();
            con.close();
        } catch (SQLException e) {
            logger.error("数据库连接失败请检查用户名密码");
            System.exit(1);
        }
        return  list;
    }
    /*
    取昨夜的数据
     */
    public List<CMPP_DATABASE> getPoolByDay(String str,String starttime,String endtime) {
        List<CMPP_DATABASE> list = new ArrayList<CMPP_DATABASE>();
        try {
            con = dbp.getConnection();
            //取出前5分钟的数据
            PreparedStatement ps1 = con.prepareStatement("select * from NMConDB.dbo.SmsSender where  istatus = '0'and  DATEDIFF(MI,gentime,'"+str+"')<=5 and DATEDIFF(MI,gentime,'"+str+"')>=0");
            ResultSet rs1 = ps1.executeQuery();
            while (rs1.next()) {
                list.add(new CMPP_DATABASE(rs1.getInt(1), rs1.getString(3), rs1.getString(4)));
            }

            PreparedStatement ps = con.prepareStatement("select * from NMConDB.dbo.SmsSender where istatus = '0'and gentime >=  "+"'"+starttime+"' and gentime < '"+endtime+"'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new CMPP_DATABASE(rs.getInt(1), rs.getString(3), rs.getString(4)));
            }
            //JdbcUtils.printResultSet(resultSet);
            ps.close();
            con.close();
        } catch (SQLException e) {
            logger.error("数据库连接失败请检查用户名密码");
            System.exit(1);
        }
        return  list;
    }
    //标记成功的信息
    public void update(List<CMPP_DATABASE> idlist,String nowdate) throws SQLException {
        con = dbp.getConnection();
        // 获取Statement对象
        Statement state = con.createStatement();
        for (int i=0;i<idlist.size();i++){
            state.addBatch("UPDATE NMConDB.dbo.SmsSender SET istatus = '1' , sendtime = "+"'"+nowdate+"'"+" WHERE id = "+idlist.get(i).getId());
        }
        // 执行sql
        state.executeBatch();
        // 清空批量
        state.clearBatch();
        // 关闭statement对象
        state.close();
        con.close();
        return;
    }
    //标记错误的信息
    public void updateall(List<Integer> idlist) throws SQLException {
        con = dbp.getConnection();
        // 获取Statement对象
        Statement state = con.createStatement();
        for (int i=0;i<idlist.size();i++){
            state.addBatch("UPDATE NMConDB.dbo.SmsSender SET istatus = '2' WHERE id = "+idlist.get(i));
        }
        // 执行sql
        state.executeBatch();
        // 清空批量
        state.clearBatch();
        // 关闭statement对象
        state.close();
        con.close();
        return;
    }
    //标记错误的信息
    public void updateto0(List<Integer> idlist) throws SQLException {
        con = dbp.getConnection();
        // 获取Statement对象
        Statement state = con.createStatement();
        for (int i=0;i<idlist.size();i++){
            state.addBatch("UPDATE NMConDB.dbo.SmsSender SET istatus = '0' WHERE id = "+idlist.get(i));
        }
        // 执行sql
        state.executeBatch();
        // 清空批量
        state.clearBatch();
        // 关闭statement对象
        state.close();
        con.close();
        return;
    }
}

