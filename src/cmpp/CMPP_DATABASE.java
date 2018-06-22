package cmpp;

/**
 * Created by zmd on 2018/2/9.
 */
public class CMPP_DATABASE {
    public int id;
    public String type;
    public String phone;
    public String sendcontent;
    public String istatus;
    public String gentime;
    public String sendtime;
    public String state;

    public CMPP_DATABASE() {
    }
    public CMPP_DATABASE(int id,String phone, String sendcontent) {
        this.id = id;
        this.phone = phone;
        this.sendcontent = sendcontent;
    }

    public CMPP_DATABASE(int id,String type, String phone, String sendcontent, String istatus, String gentime, String sendtime, String state) {
        this.id = id;
        this.type = type;
        this.phone = phone;
        this.sendcontent = sendcontent;
        this.istatus = istatus;
        this.gentime = gentime;
        this.sendtime = sendtime;
        this.state = state;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getSendcontent() {
        return sendcontent;
    }
    public void setSendcontent(String sendcontent) {
        this.sendcontent = sendcontent;
    }
    public String getIstatus() {
        return istatus;
    }
    public void setIstatus(String istatus) {
        this.istatus = istatus;
    }
    public String getGentime() {
        return gentime;
    }
    public void setGentime(String gentime) {
        this.gentime = gentime;
    }
    public String getSendtime() {
        return sendtime;
    }
    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
}
