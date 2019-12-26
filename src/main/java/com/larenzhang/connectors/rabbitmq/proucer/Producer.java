package com.larenzhang.connectors.rabbitmq.proucer;

import java.io.IOException;

import com.larenzhang.connectors.rabbitmq.common.EndPoint;

public class Producer extends EndPoint {

    public Producer(String endPointName) throws Exception {
        super(endPointName);
    }

    public void sendMessage(String jsonStr) throws IOException {
        channel.basicPublish("",endPointName, null, jsonStr.getBytes("UTF-8"));
    }
}