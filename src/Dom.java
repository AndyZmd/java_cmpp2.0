import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/**
 * Created by zmd on 2018/3/2.
 */
public class Dom {
    public static Document doc = null;
    static {
        try {
            File f = new File("conf.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(f);
        } catch (Exception e) {
            System.out.println("读取配置文件时出错，请检查conf.xml文件");
        }
    }
    public static String getgwip(){
        if (doc.getElementsByTagName("gwip").getLength()==0){
            return "10.219.21.4";
        }
        String gwip = doc.getElementsByTagName("gwip").item(0).getFirstChild().getNodeValue();
        return gwip;
    }
    public static int getmtport(){
        if (doc.getElementsByTagName("mtport").getLength()==0){
            return 7890;
        }
        String gwip = doc.getElementsByTagName("mtport").item(0).getFirstChild().getNodeValue();
        int a = Integer.parseInt(gwip);
        return a;
    }
    public static String getspno(){
        if (doc.getElementsByTagName("spno").getLength()==0){
            return "10658562";
        }
        String gwip = doc.getElementsByTagName("spno").item(0).getFirstChild().getNodeValue();
        return gwip;
    }
    public static String getspid(){
        if (doc.getElementsByTagName("spid").getLength()==0){
            return "905275";
        }
        String gwip = doc.getElementsByTagName("spid").item(0).getFirstChild().getNodeValue();
        return gwip;
    }
    public static String getuserpass(){
        if (doc.getElementsByTagName("userpass").getLength()==0){
            return "WYpt5275#";
        }
        String gwip = doc.getElementsByTagName("userpass").item(0).getFirstChild().getNodeValue();
        return gwip;
    }
    public  static String getstarttime(){
        if (doc.getElementsByTagName("start_time").getLength()==0){
            return "07:30:00";
        }
        String gwip = doc.getElementsByTagName("start_time").item(0).getFirstChild().getNodeValue();
        return gwip;
    }
    public static String getendtime(){
        if (doc.getElementsByTagName("end_time").getLength()==0){
            return "20:00:00";
        }
        String gwip = doc.getElementsByTagName("end_time").item(0).getFirstChild().getNodeValue();
        return gwip;
    }
    public static String getcuttime(){
        if (doc.getElementsByTagName("cut_time").getLength()==0){
            return "08:00:00";
        }
        String gwip = doc.getElementsByTagName("cut_time").item(0).getFirstChild().getNodeValue();
        return gwip;
    }
}
