package com.larenzhang.connectors.rabbitmq.source;

import com.larenzhang.connectors.rabbitmq.domain.TiZheng;
import com.larenzhang.connectors.rabbitmq.utils.GsonUtil;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

/**
 * 自定义实现一个支持并行度的source
 *
 */
public class MyParalleSource implements ParallelSourceFunction<String> {

    private long count = 1L;

    private boolean isRunning = true;

    /**
     * 主要的方法
     * 启动一个source
     * 大部分情况下，都需要在这个run方法中实现一个循环，这样就可以循环产生数据了
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        while(isRunning){
            TiZheng tiZheng = new TiZheng();
            tiZheng.zzd="肺炎";
            tiZheng.tw=34+(int) (Math.random() * 10);
            tiZheng.birth="2010-01-3";
            tiZheng.sex=(int) (Math.random() * 2);
            tiZheng.xl=120f;
            tiZheng.xy=120f;
            ctx.collect(GsonUtil.toJson(tiZheng));
            System.out.println("source"+GsonUtil.toJson(tiZheng));
            count++;
            //每秒产生一条数据
            Thread.sleep(1000);
        }
    }

    /**
     * 取消一个cancel的时候会调用的方法
     *
     */
    @Override
    public void cancel() {
        isRunning = false;
    }
}
