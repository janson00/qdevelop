package cn.qdevelop.service.system;

import java.util.Map;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import cn.qdevelop.common.utils.QProperties;
import cn.qdevelop.core.db.config.SQLConfigLoader;
import cn.qdevelop.service.APIControl;
import cn.qdevelop.service.IOutput;
import cn.qdevelop.service.IService;

@WebServlet(urlPatterns="/svr/sys/reload",
loadOnStartup=1,initParams={  
@WebInitParam(name=IService.INIT_VALID_REQUIRED,value="act")
})
public class SystemReloadServlet extends APIControl{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init(Map<String, String> args) {
		
	}

	@Override
	protected String execute(Map<String, String> args, IOutput output) {
		long s = System.currentTimeMillis();
		String act = args.get("act");
		if(act.equals("properties")){
			QProperties.getInstance().init();
			output.setData("系统配置文件*.properties热加载完毕！");
			output.addAttr("used", (System.currentTimeMillis()-s)+"ms");
		}else if(act.equals("sqlConfig")){
			SQLConfigLoader.getInstance().hotLoadConfig();
			output.setData("数据库配置文件*.sql.xml热加载完毕！");
			output.addAttr("used", (System.currentTimeMillis()-s)+"ms");
		}
		return null;
	}

}
