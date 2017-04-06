package cn.qdevelop.service.common.api;

import java.util.Map;

import javax.servlet.annotation.WebServlet;

import com.alibaba.fastjson.JSON;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.standard.IDBResult;
import cn.qdevelop.service.AbstractService;

@WebServlet("/svr/common/ajax/dataTables")
public class DataTablesQuery extends AbstractService{
	/**
	 * 
	 */
	private static final long serialVersionUID = 435444758591154555L;

	@Override
	protected String execute(Map<String, String> query,StringBuffer result) {
		result.append("{");
		try {
			String draw = query.get("draw") == null ? "1" : String.valueOf(query.get("draw"));
			int page = query.get("page") == null ? 1 : Integer.parseInt(String.valueOf(query.get("page")));
			int pageSize = query.get("page_size") == null ? 10 : Integer.parseInt(String.valueOf(query.get("page_size")));

			IDBResult rb = DatabaseFactory.getInstance().queryDatabase(query);
			int recordsFiltered = rb.getSize() == pageSize ? page*pageSize+1 : (page-1)*pageSize + rb.getSize();

			String json = JSON.toJSONString(rb);
			result.append("\"tag\":true,");
			result.append("\"data\":").append(json).append(",");
			result.append("\"draw\":").append(draw).append(",");
			result.append("\"recordsTotal\":").append(rb.getSize()).append(",");
			result.append("\"recordsFiltered\":").append(recordsFiltered);
		} catch (QDevelopException e) {
			result.append("\"tag\":false,");
			result.append("\"error\":\"").append(e.getMessage().replaceAll("\"", "\\\\\"")).append("\"");
			e.printStackTrace();
		}
		result.append("}");
		return this.OUT_TYPE_JSON;
	}

}
