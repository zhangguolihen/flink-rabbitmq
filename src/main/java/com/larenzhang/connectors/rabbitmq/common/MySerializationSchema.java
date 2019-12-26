package com.larenzhang.connectors.rabbitmq.common;

import com.larenzhang.connectors.rabbitmq.domain.TiZheng;
import com.larenzhang.connectors.rabbitmq.utils.GsonUtil;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.nio.charset.Charset;

public class MySerializationSchema implements DeserializationSchema<TiZheng>,SerializationSchema<TiZheng> {
    private static final long serialVersionUID = 1L;

    public byte[] serialize(TiZheng element) {
        return GsonUtil.toJson(element).getBytes(Charset.forName("UTF-8"));
    }

    public TiZheng deserialize(byte[] bytes) throws IOException {
        return GsonUtil.fromJson(new String(bytes), TiZheng.class);
    }


    public boolean isEndOfStream(TiZheng tiZheng) {
        return false;
    }

    public TypeInformation<TiZheng> getProducedType() {
         return TypeInformation.of(TiZheng.class);
    }
}