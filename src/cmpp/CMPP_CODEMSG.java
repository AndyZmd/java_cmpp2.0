package cmpp;

/**
 * Created by zmd on 2017/12/5/005.
 */
public class CMPP_CODEMSG {

    String[] codeMsgArr = {"0:系统操作成功。"
            ,"1:没有匹配路由。"
            ,"2:源网关代码错误。"
            ,"3:路由类型错误。"
            ,"4:本节点不支持更新（GNS分节点）。"
            ,"5:路由信息更新失败。"
            ,"6:汇接网关路由信息时间戳比本地路由信息时间戳旧。"
            ,"9:系统繁忙。"
            ,"10:Update_type错误。"
            ,"11:路由编号错误。"
            ,"12:目的网关代码错误。"
            ,"13:目的网关IP错误。"
            ,"14:目的网关Port错误。"
            ,"15:MT路由起始号码段错误。"
            ,"16:MT路由截止号码段错误。"
            ,"17:手机所属省代码错误。"
            ,"18:用户类型错误。"
            ,"19:SP_Id错误。"
            ,"20:SP_Code错误。"
            ,"21:SP_AccessType错误。"
            ,"22:Service_Id错误。"
            ,"23:Start_code错误。"
            ,"24:End_code错误。"
    };
    public String getCodeMsg(String code){
        //遍历 code对应的中文msg信息
        for (int x = 0; x <this.codeMsgArr.length; x++) {
            String str=this.codeMsgArr[x];
            String [] arr = str.split(":");
            String codeX=arr[0];
            if (codeX.equals(code)){
                //找到，则返回对应错误MSG
                return arr[1];
            }
        }
        return "["+code+"]自定义的错误码";
    }
}
