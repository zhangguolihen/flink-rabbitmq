package com.larenzhang.connectors.rabbitmq.job;


import com.larenzhang.connectors.rabbitmq.contants.StreamConstants;
import com.larenzhang.connectors.rabbitmq.domain.TiZheng;
import com.larenzhang.connectors.rabbitmq.common.MySerializationSchema;
import com.larenzhang.connectors.rabbitmq.utils.ExecutionEnvUtil;
import com.larenzhang.connectors.rabbitmq.utils.GsonUtil;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;


/**
 * rabbitMq Test
 */
public class FlinkRabbitMq {


    public  static void testRabbitMq(StreamExecutionEnvironment env)  throws Exception {

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        env.getConfig().disableSysoutLogging();
        //RabbitMq配置参数
        final RMQConnectionConfig connectionConfig = new RMQConnectionConfig
                .Builder().setHost( ExecutionEnvUtil.PARAMETER_TOOL.get(StreamConstants.RABBITMQ.RMQ_HOST,"127.0.0.1")).setVirtualHost("/")
                .setPort(ExecutionEnvUtil.PARAMETER_TOOL.getInt(StreamConstants.RABBITMQ.RMQ_PORT,5672)).setUserName( ExecutionEnvUtil.PARAMETER_TOOL.get(StreamConstants.RABBITMQ.RMQ_USER,"admin"))
                .setPassword( ExecutionEnvUtil.PARAMETER_TOOL.get(StreamConstants.RABBITMQ.RMQ_PASSWORD,""))
                .build();
        //获取自定义数据源
       // DataStreamSource<String> warningMsgMq = env.addSource(new MyParalleSource()).setParallelism(1);
       //获取rabbitMq数据源
        DataStreamSource<String> warningMsgMq = env.addSource(new RMQSource<>(connectionConfig,
                "test.warningMsg",
                true,
                new SimpleStringSchema()))
                .setParallelism(1);

        //  创建一个 sink,将数据直接输出
        warningMsgMq.addSink(new RMQSink<>(connectionConfig, "test.warningMsgSinkNormal", new SimpleStringSchema()));


       //注册成table
        DataStream<TiZheng> warningMsgMqStream = warningMsgMq.map(string -> GsonUtil.fromJson(string, TiZheng.class));
        Table tiZheng = tEnv.fromDataStream(warningMsgMqStream, "tw, zzd, birth,sex,xl,xy");
        Table result = tEnv.sqlQuery("SELECT * FROM " + tiZheng + " WHERE tw > 39 and zzd='肺炎' and birth<'2016-01-12' ");
        tEnv.toAppendStream(result, TiZheng.class).print();


        Table result2 = tEnv.sqlQuery("SELECT * FROM " + tiZheng + " WHERE tw > 41 and sex=0 ");
        //输出到新的queue
        tEnv.toAppendStream(result2, TiZheng.class).addSink(new RMQSink<>(connectionConfig, "test.warningMsgSink",new MySerializationSchema()));

        //如果想保证 exactly-once 或 at-least-once 需要把 checkpoint 开启
        //env.enableCheckpointing(10000);

        //执行
        env.execute("flink TiZheng rabbitmq");


    }
}



