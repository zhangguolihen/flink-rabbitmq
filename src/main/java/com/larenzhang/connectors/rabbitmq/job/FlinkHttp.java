package com.larenzhang.connectors.rabbitmq.job;


import com.google.common.collect.Lists;
import com.larenzhang.connectors.http.source.NtHttpSource;
import com.larenzhang.connectors.rabbitmq.domain.TiZheng;
import com.larenzhang.connectors.rabbitmq.utils.GsonUtil;
import com.larenzhang.connectors.sqlserver.SinkBatchToSqlServer;
import com.larenzhang.connectors.sqlserver.SinkToSqlServer;
import com.larenzhang.connectors.sqlserver.SqlSerializableReturnImpl;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;


/**
 * rabbitMq Test
 */
public class FlinkHttp {


    public  static void testHttp(StreamExecutionEnvironment env)  throws Exception {

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
       // env.getConfig().disableSysoutLogging();

        //获取自定义数据源
        DataStreamSource<String> warningMsgHttp = env.addSource(new NtHttpSource(8067,"/u/p/receive/*")).setParallelism(1);


       //注册成table
        DataStream<TiZheng> warningMsgMqStream = warningMsgHttp.map(string -> GsonUtil.fromJson(string, TiZheng.class));
        Table tiZheng = tEnv.fromDataStream(warningMsgMqStream, "tw, zzd, birth,sex,xl,xy");
        Table result = tEnv.sqlQuery("SELECT * FROM " + tiZheng + " WHERE tw > 39 and zzd='肺炎' and birth<'2016-01-12' ");


        String sql="insert into TIZHENG(tw,zzd,birth,sex,xl,xy) values(?,?,?,?,?,?)";
//        tEnv.toAppendStream(result, TiZheng.class).timeWindowAll(Time.seconds(10)).apply(new AllWindowFunction<Object, List<TiZheng>, TimeWindow>() {
//            public void apply(TimeWindow tw, Iterable<TiZheng> var2, Collector<List<TiZheng>> out){
//            ArrayList<TiZheng> list = Lists.newArrayList(var2);
//            if(list.size()>0){
//                out.collect(list);
//            }
//            }
//        }).addSink(new SinkBatchToSqlServer<>(sql,new SqlSerializableReturnImpl<>()));

        tEnv.toAppendStream(result, TiZheng.class)
                .addSink(new SinkToSqlServer<>(sql,new SqlSerializableReturnImpl<>())).name("tizheng-to-sqlserver");


        //如果想保证 exactly-once 或 at-least-once 需要把 checkpoint 开启
        //env.enableCheckpointing(10000);

        //执行
        env.execute("flink TiZheng Http");


    }
}



