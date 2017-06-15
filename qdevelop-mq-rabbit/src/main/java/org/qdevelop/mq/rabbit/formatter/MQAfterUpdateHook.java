package org.qdevelop.mq.rabbit.formatter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;
import org.qdevelop.mq.rabbit.MQProvider;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.db.bean.UpdateBean;
import cn.qdevelop.core.formatter.AbstractUpdateHook;
import cn.qdevelop.core.standard.IDBUpdate;

public class MQAfterUpdateHook  extends AbstractUpdateHook {
	String queueName;
	@Override
	public void initHook(Element conf) throws QDevelopException {
		queueName = conf.attributeValue("queue-name");
	}

	@Override
	public void init(Connection conn, Map<String, ?> query) throws QDevelopException {
		
	}

	@Override
	public void execHook(Connection conn, UpdateBean ub, int fetchSize, int lastInsertId) throws SQLException {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void flush(Connection conn, Map<String, ?> query, IDBUpdate dbUpdate) throws QDevelopException {
		MQProvider.getInstance().publish(queueName, (HashMap<String,Object>)query);
	}

}
