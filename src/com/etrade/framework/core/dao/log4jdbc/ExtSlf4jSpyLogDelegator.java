package com.etrade.framework.core.dao.log4jdbc;

import net.sf.log4jdbc.ResultSetCollector;
import net.sf.log4jdbc.Slf4jSpyLogDelegator;
import net.sf.log4jdbc.Spy;
import net.sf.log4jdbc.SpyLogFactory;

import org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ��ǿlog4jdbc��־����
 */
public class ExtSlf4jSpyLogDelegator extends Slf4jSpyLogDelegator {

	private static final Logger jdbcLogger = LoggerFactory.getLogger("jdbc.audit");

	private final Logger resultSetTableLogger = LoggerFactory.getLogger("jdbc.resultsettable");

	static {
		jdbcLogger.info("Static init ExtSlf4jSpyLogDelegator...");
		SpyLogFactory.setSpyLogDelegator(new ExtSlf4jSpyLogDelegator());
	}

	    /**
     * ����Hibernate�ڹر�Statement�����getMaxRows�����쳣
     * @see JdbcCoordinatorImpl.close
     */
	@Override
	public void exceptionOccured(Spy spy, String methodCall, Exception e, String sql, long execTime) {
		if ("getMaxRows()".equals(methodCall)) {
			if (jdbcLogger.isDebugEnabled()) {
				String classType = spy.getClassType();
				Integer spyNo = spy.getConnectionNumber();
				String header = spyNo + ". " + classType + "." + methodCall;
				jdbcLogger.error(header, e);
			}
			return;
		}
		super.exceptionOccured(spy, methodCall, e, sql, execTime);
	}

	@Override
    public void sqlTimingOccured(Spy spy, long execTime, String methodCall, String sql) {
        //����Ƶ��ִ�е�Quartz SQL���
		if (sql.indexOf("QRTZ_TRIGGERS") > -1 || sql.indexOf("QRTZ_SCHEDULER_STATE") > -1
				|| sql.indexOf("QRTZ2_TRIGGERS") > -1 || sql.indexOf("QRTZ2_SCHEDULER_STATE") > -1) {
			return;
		}
		super.sqlTimingOccured(spy, execTime, methodCall, sql);
	}

	@Override
    public String sqlOccured(Spy spy, String methodCall, String sql) {
        //����Ƶ��ִ�е�Quartz SQL���
		if (sql.indexOf("QRTZ_TRIGGERS") > -1 || sql.indexOf("QRTZ_SCHEDULER_STATE") > -1
				|| sql.indexOf("QRTZ2_TRIGGERS") > -1 || sql.indexOf("QRTZ2_SCHEDULER_STATE") > -1) {
			return "";
		}
		return super.sqlOccured(spy, methodCall, sql);
	}

	    /**
     * �Ż��������ӡ��������̫���Ĵ��ı��ֶν�ȡֻ��ʾ����
     */
	@Override
	public void resultSetCollected(ResultSetCollector resultSetCollector) {
		new ExtResultSetCollectorPrinter(resultSetTableLogger).printResultSet(resultSetCollector);
	}
}
