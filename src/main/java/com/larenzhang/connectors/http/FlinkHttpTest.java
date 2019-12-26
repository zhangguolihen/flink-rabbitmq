package com.larenzhang.connectors.http;

import com.larenzhang.connectors.rabbitmq.job.FlinkHttp;
import com.larenzhang.connectors.rabbitmq.job.FlinkRabbitMq;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 提交到集群环境
 */
public class FlinkHttpTest {

    public static void main(String[] args) throws Exception {
         ExecutorService startService = Executors.newSingleThreadExecutor();
//         final  StreamExecutionEnvironment env = StreamExecutionEnvironment
//                .createRemoteEnvironment("mdw", 8081, "D:\\workspace\\flink-rabbitmq\\classes\\artifacts\\flink_connectors_http_jar\\flink-connectors-rabbitmq.jar");


      final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        try {
            startService.execute(() -> {
                try {
                    FlinkHttp.testHttp(env);
                } catch (Exception e) {
                    e.printStackTrace();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
}
