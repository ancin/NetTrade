package com.etrade.framework.core.dao.h2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;

/**
 * Spring JDBCĬ��ֻ֧���ڴ�ģʽ��H2Ƕ�����ݿ� @see H2EmbeddedDatabaseConfigurer
 * ��չʵ�ֻ���File�ļ�ģʽ��H2Ƕ��ʽ���ݿ⣬�������̾Ͳ�����Ҫÿ�γ�ʼ�����ݿ�
 * ��ʵ�����ù�����ע����databaseNameָ��H2���ݾ���·���ļ��� ����ʾ��:
        <bean id="dataSource"
            class="org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactoryBean">
            <property name="databaseName" value="${user.dir}\h2\prototype" />
            <property name="databaseConfigurer">
                <bean
                    class="****.core.dao.h2.H2EmbeddedFileDatabaseConfigurer"
                    factory-method="getInstance">
                </bean>
            </property>
        </bean>     
 */

public class H2EmbeddedFileDatabaseConfigurer implements EmbeddedDatabaseConfigurer {

    private static Logger logger = LoggerFactory.getLogger(H2EmbeddedFileDatabaseConfigurer.class);

    @Override
    public void shutdown(DataSource dataSource, String databaseName) {
        try {
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            stmt.execute("SHUTDOWN");
        } catch (SQLException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not shutdown embedded database", ex);
            }
        }
    }

    @Override
    public void configureConnectionProperties(ConnectionProperties properties, String databaseName) {
        properties.setDriverClass(org.h2.Driver.class);
        logger.info("Using H2 EmbeddedFileDatabase: {}", databaseName);
        properties.setUrl(String.format("jdbc:h2:file:%s;DB_CLOSE_DELAY=-1", databaseName));
        properties.setUsername("sa");
        properties.setPassword("");
    }
}