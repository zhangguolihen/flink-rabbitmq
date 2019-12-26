package com.larenzhang.connectors.http;


import com.larenzhang.connectors.rabbitmq.domain.TiZheng;
import com.larenzhang.connectors.rabbitmq.utils.GsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * 发送rabbitmq测试数据
 */
public class HttpTest {


    public static void main(String[] args)   {
      try {


        for (int i = 0; i < 100; i++) {

            TiZheng tiZheng = new TiZheng();
            tiZheng.zzd="肺炎";
            tiZheng.tw=34+(int) (Math.random() * 10);
            tiZheng.birth="2010-01-3";
            tiZheng.sex=(int) (Math.random() * 2);
            tiZheng.xl=120f;
            tiZheng.xy=120f;

            PostMsg(tiZheng);
            System.out.println("Producer Send +'" + GsonUtil.toJson(tiZheng));
            Thread.sleep(1000);
        }



      }catch (Exception e){
          e.printStackTrace();
      }finally {
          try {
              //producer.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
    }
    private static void PostMsg( TiZheng tiZheng) throws Exception {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        String url = "http://192.168.56.8:8068/u/p/receive/tizheng";
        //初始化http请求
        HttpPost post = new HttpPost(url);
        //设置请求头
        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        try {

            String paramJson=GsonUtil.toJson(tiZheng);
            // 提交参数发送请求
            StringEntity requestEntity = new StringEntity(paramJson,"UTF-8");

            post.setEntity(requestEntity);
            CloseableHttpResponse response=httpclient.execute(post);

            HttpEntity entity=response.getEntity();
            String entityStr = EntityUtils.toString(entity,"UTF-8");
            System.out.println(entityStr);
            //
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpclient.close();
        }
    }

}
