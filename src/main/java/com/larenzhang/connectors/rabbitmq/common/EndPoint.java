package com.larenzhang.connectors.rabbitmq.common;

import com.larenzhang.connectors.rabbitmq.contants.StreamConstants;
import com.larenzhang.connectors.rabbitmq.utils.ExecutionEnvUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

/**
 *
 */
public class EndPoint {
    protected Channel channel;
    protected Connection connection;
    protected String endPointName;

    public EndPoint(String endpointName) throws Exception {
        this.endPointName = endpointName;

        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost( ExecutionEnvUtil.PARAMETER_TOOL.get(StreamConstants.RABBITMQ.RMQ_HOST,"127.0.0.1"));
        factory.setUsername( ExecutionEnvUtil.PARAMETER_TOOL.get(StreamConstants.RABBITMQ.RMQ_USER,"admin"));
        factory.setPassword( ExecutionEnvUtil.PARAMETER_TOOL.get(StreamConstants.RABBITMQ.RMQ_PASSWORD,""));
        factory.setPort(ExecutionEnvUtil.PARAMETER_TOOL.getInt(StreamConstants.RABBITMQ.RMQ_PORT,5672));

        connection = factory.newConnection();

        channel = connection.createChannel();

        channel.queueDeclare(endpointName, true, false, false, null);
    }

    /**
     * 关闭channel和connection。并非必须，因为隐含是自动调用的
     *
     * @throws IOException
     */
    public void close() throws Exception {
        this.channel.close();
        this.connection.close();
    }
}
