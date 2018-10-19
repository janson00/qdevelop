package cn.qdevelop.auth.common;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.qdevelop.auth.bean.LoginInfo;
import cn.qdevelop.auth.utils.AuthUtils;
import cn.qdevelop.auth.utils.XMemcached;
import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.bean.DBArgs;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.interfacer.IOutput;
import cn.qdevelop.service.interfacer.IService;
import cn.qdevelop.service.utils.QServiceUitls;

@WebServlet(urlPatterns={"/sys/auth/login","/sys/auth/login.jsonp"},
loadOnStartup=1,initParams={
		//value内填写需要验证必须存在的参数，其他参数将自动按数据库内的格式自动校验,可将绝大部分数据验证自动处理了
		@WebInitParam(name=IService.INIT_VALID_REQUIRED,value="username,passwd")
})
public class AuthLogin extends APIControl{

	private static final long serialVersionUID = 1L;

	@Override
	public void init(Map<String, String> args) {

	}



	@Override
	protected String execute(Map<String, String> args, IOutput output) {
		Connection conn = null;
		try {
			conn = DatabaseFactory.getInstance().getConnectByQuery("checkLoginAction");
			//验证登陆信息
			IDBResult result = DatabaseFactory.getInstance().queryDatabase(new DBArgs("checkLoginAction")
					.put("login_name", args.get("username"))
					.put("login_passwd", args.get("passwd"))
					,conn);
			if(result.getSize()==0){
				output.setErrMsg("登陆失败，请检测账号或密码！");
				return null;
			}

			//设置登陆成功后信息
			Map<String,Object> data = result.getResult(0);
			LoginInfo li = new LoginInfo();
			li.setLoginName(result.getString(0, "login_name"));
			li.setSysName("admin");
			li.setUserName(result.getString(0, "user_name"));
			li.setIp(QServiceUitls.getUserIP(getRequest()));
			li.setUid(result.getInteger(0, "uid"));
			
			String  extra = result.getString(0, "extra_info");
			if(extra!=null && extra.length()>2){
				HashMap<String,String> extraAttr = new HashMap<String,String>();
				JSONObject tmp = JSON.parseObject(extra);
				Iterator<String> itor = tmp.keySet().iterator();
				while(itor.hasNext()){
					String key = itor.next();
					extraAttr.put(key, tmp.getString(key));
				}
				tmp.clear();
				li.setExtra(extraAttr);
			}

			StringBuffer sb = new StringBuffer();
			if(data.get("child")!=null){
				@SuppressWarnings("unchecked")
				List<Map<String,Object>> tmp = (List<Map<String,Object>>)data.get("child");
				for(int i=0;i<tmp.size();i++){
					sb.append("|").append(tmp.get(i).get("permit_id"));
				}
			}
			li.setPermitId(sb.length()==0?"0":sb.substring(1).toString());

			//获取用户可用访问url
			result = DatabaseFactory.getInstance().queryDatabase(new DBArgs("loginWithMenuPerimit")
					.put("permit_id", li.getPermitId() )
					.put("uid", li.getUid())
					,conn);

			for(int i=0;i<result.getSize();i++){
				String url = result.getString(i, "permit_link");
				if(url!=null && url.length()==0){
					url = result.getString(i, "link");
				}
				if(url!=null && url.length() > 0){
					li.addMenuPermit(AuthUtils.relaceDynamicValue(url), result.getLong(i, "permit"));
				}
			}

			//存入memcached做验证使用
			String sid = QServiceUitls.getCookie("sid", this.getRequest());

			if(sid!=null){
				XMemcached.getInstance().add(sid, li, XMemcached.EXP_30Min);
				DBArgs query = new DBArgs("loginLoggers")
						.putMap(data, new String[]{"permit_id","uid","login_name","user_name"})
						.put("sid", sid)
						.put("ip", li.getIp())
						.put("sys_name", li.getSysName());
				DatabaseFactory.getInstance().updateDatabase(query);
			}
			data.clear();
			output.setData("登陆成功");
		} catch (QDevelopException e) {
			e.printStackTrace();
		}
		DatabaseFactory.closeConnection(conn);
		return null;
	}



}
