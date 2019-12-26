/**
 * 
 */
package com.larenzhang.connectors.rabbitmq.contants;

/**
 * @author larenzhang
 *
 */
public class StreamConstants {

	public static class CONFIG {
		public static final String STREAMING_CONFIG = "/flinkStream.properties";
	}

	public static class RABBITMQ {
		public static final String DEFALUT = "";
		public static final String RMQ_HOST = "rmq.host";
		public static final String RMQ_PORT = "rmq.port";
		public static final String RMQ_USER = "rmq.user";
		public static final String RMQ_PASSWORD = "rmq.password";
	}
	public static class STREAM {
		public static final String STREAM_PARALLELISM = "stream.parallelism";
		public static final String STREAM_SINK_PARALLELISM = "stream.sink.parallelism";
		public static final String STREAM_DEFAULT_PARALLELISM = "stream.default.parallelism";
		public static final String STREAM_CHECKPOINT_ENABLE = "stream.checkpoint.enable";
		public static final String STREAM_CHECKPOINT_INTERVAL = "stream.checkpoint.interval";
	}
	public  static  class DATABASE{
		public static  final String DRIVECLASS="datasource.driver.classname";
		public static  final String USERNAME="datasource.username";
		public static  final String PASSWORD="datasource.password";
		public static  final String DBURL="datasource.url";
	}

}
