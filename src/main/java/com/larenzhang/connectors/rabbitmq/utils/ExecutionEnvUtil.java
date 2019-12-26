package com.larenzhang.connectors.rabbitmq.utils;

import com.larenzhang.connectors.rabbitmq.contants.StreamConstants;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExecutionEnvUtil {

    public static final ParameterTool PARAMETER_TOOL = createParameterTool();

    public static ParameterTool createParameterTool(final String[] args) throws Exception {
        return ParameterTool
                .fromPropertiesFile(com.larenzhang.connectors.rabbitmq.utils.ExecutionEnvUtil.class.getResourceAsStream(StreamConstants.CONFIG.STREAMING_CONFIG))
                .mergeWith(ParameterTool.fromArgs(args))
                .mergeWith(ParameterTool.fromSystemProperties())
                .mergeWith(ParameterTool.fromMap(getenv()));
    }

    private static ParameterTool createParameterTool() {
        try {
            return ParameterTool
                    .fromPropertiesFile(com.larenzhang.connectors.rabbitmq.utils.ExecutionEnvUtil.class.getResourceAsStream(StreamConstants.CONFIG.STREAMING_CONFIG))
                    .mergeWith(ParameterTool.fromSystemProperties())
                    .mergeWith(ParameterTool.fromMap(getenv()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static StreamExecutionEnvironment prepare(ParameterTool parameterTool) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(parameterTool.getInt(StreamConstants.STREAM.STREAM_PARALLELISM, 5));
        env.getConfig().disableSysoutLogging();
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000));
        if (parameterTool.getBoolean(StreamConstants.STREAM.STREAM_CHECKPOINT_ENABLE, true)) {
            env.enableCheckpointing(parameterTool.getInt(StreamConstants.STREAM.STREAM_CHECKPOINT_INTERVAL, 1000)); // create a checkpoint every 5 seconds
        }
        env.getConfig().setGlobalJobParameters(parameterTool); // make parameters available in the web interface
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        return env;
    }

    private static Map<String, String> getenv() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
