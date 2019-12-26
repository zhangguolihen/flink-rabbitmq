package com.larenzhang.connectors.rabbitmq;


import com.larenzhang.connectors.rabbitmq.domain.TiZheng;
import com.larenzhang.connectors.rabbitmq.proucer.Producer;
import com.larenzhang.connectors.rabbitmq.utils.GsonUtil;

/**
 * 发送rabbitmq测试数据
 */
public class RabbitMqProducerTest {
    public final static String QUEUE_NAME = "test.warningMsg";

    public static void main(String[] args)   {
        Producer producer = null;
      try {
          producer = new Producer(QUEUE_NAME);

        for (int i = 0; i < 100000; i++) {

            TiZheng tiZheng = new TiZheng();
            tiZheng.zzd="肺炎";
            tiZheng.tw=34+(int) (Math.random() * 10);
            tiZheng.birth="2010-01-3";
            tiZheng.sex=(int) (Math.random() * 2);
            tiZheng.xl=120f;
            tiZheng.xy=120f;
            producer.sendMessage( GsonUtil.toJson(tiZheng));

            System.out.println("Producer Send +'" + GsonUtil.toJson(tiZheng));
            Thread.sleep(10);
        }
      }catch (Exception e){
          e.printStackTrace();
      }finally {
          try {
              producer.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
    }
}
