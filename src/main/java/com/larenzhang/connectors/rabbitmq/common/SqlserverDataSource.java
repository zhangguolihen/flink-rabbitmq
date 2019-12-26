package com.larenzhang.connectors.rabbitmq.common;

import com.larenzhang.connectors.rabbitmq.contants.StreamConstants;
import com.larenzhang.connectors.rabbitmq.utils.ExecutionEnvUtil;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Description
 * @Author zhangguoli
 * @Date 9:34 2019/9/29
 **/
public  class SqlserverDataSource {
    private  static volatile BasicDataSource basicDataSource;
    private SqlserverDataSource(){ }

    public synchronized  static Connection getConnection() throws SQLException {
                if (null == basicDataSource) {
                    basicDataSource = new BasicDataSource();
                    basicDataSource.setDriverClassName(ExecutionEnvUtil.PARAMETER_TOOL.get(StreamConstants.DATABASE.DRIVECLASS));
                    basicDataSource.setUrl(ExecutionEnvUtil.PARAMETER_TOOL.get(StreamConstants.DATABASE.DBURL));
                    basicDataSource.setUsername(ExecutionEnvUtil.PARAMETER_TOOL.get(StreamConstants.DATABASE.USERNAME));
                    basicDataSource.setPassword(ExecutionEnvUtil.PARAMETER_TOOL.get(StreamConstants.DATABASE.PASSWORD));
                    basicDataSource.setInitialSize(2);
                    basicDataSource.setMaxTotal(10);
                    basicDataSource.setMinIdle(1);
                }
        return  basicDataSource.getConnection();
    }

    public static void  realseConnection(Connection con){
        if(null!=con){
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
    