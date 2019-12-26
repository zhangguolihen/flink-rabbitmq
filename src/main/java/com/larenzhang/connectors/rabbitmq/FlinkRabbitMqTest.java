package com.larenzhang.connectors.rabbitmq;

import com.larenzhang.connectors.rabbitmq.job.FlinkRabbitMq;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 提交到集群环境
 */
public class FlinkRabbitMqTest {

    public static void main(String[] args) throws Exception {
         ExecutorService startService = Executors.newSingleThreadExecutor();
//         final  StreamExecutionEnvironment env = StreamExecutionEnvironment
//                .createRemoteEnvironment("hf-hdw-gpcenter-03", 8081, "D:\\workspace\\flink-rabbitmq\\classes\\artifacts\\flink_connectors_rabbitmq_jar\\flink-connectors-rabbitmq.jar");


       final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        try {
            startService.execute(() -> {
                try {
                    FlinkRabbitMq.testRabbitMq(env);
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
