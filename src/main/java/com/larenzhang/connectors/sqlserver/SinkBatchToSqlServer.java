package com.larenzhang.connectors.sqlserver;

import com.larenzhang.connectors.rabbitmq.common.SqlserverDataSource;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * @Description 批量处理
 * @Author zhangguoli
 * @Date 21:16 2019/9/28
 **/
public class SinkBatchToSqlServer<T> extends RichSinkFunction<List<T>> {

    private Connection connection;
    private PreparedStatement pst;
    private String sql;
    private  SqlSerializableReturn returnListener;

    public SinkBatchToSqlServer(String sql,SqlSerializableReturn returnListener){
        this.sql=sql;
        this.returnListener=returnListener;
    }
    public void open(Configuration parameters) throws Exception {
        connection=  SqlserverDataSource.getConnection();
        pst=connection.prepareStatement(sql);
        super.open(parameters);
    }

    public void close() throws Exception {
        if(pst!=null){
            pst.close();
        }
        if(connection!=null){
            connection.close();
        }
        super.close();
    }

    public void invoke(List<T> objects, SinkFunction.Context context) throws Exception {
          for (Object o:objects){
              returnListener.prepare(pst,o);
              pst.addBatch();
          }
          try {
              int[] count =pst.executeBatch();
              System.out.println("插入"+count.length);
          }catch (Exception e){
              e.printStackTrace();
          }
    }
}
    