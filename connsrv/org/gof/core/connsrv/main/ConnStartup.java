package org.gof.core.connsrv.main;

import org.gof.core.Node;
import org.gof.core.connsrv.ConnPort;
import org.gof.core.connsrv.ConnService;
import org.gof.core.connsrv.netty.Server;
import org.gof.core.support.Distr;
import org.gof.core.support.log.LogCore;

public class ConnStartup {
	public static Node CONN_NODE;
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		//设置个默认值 便于系统调试
		if(args.length == 0) {
			args = new String[] {"0"};
		}
		
		//验证参数数量
		if(args.length < 1) {
			System.out.println("useage: servId");
			return ;
		}
		
		//Log4j日志文件名称
		System.setProperty("logFileName", "conn" + args[0]);

		String serverId = args[0];
		String nodeId = Distr.NODE_CONNECT_PREFIX + serverId;
		String nodeAddr = Distr.getNodeAddr(nodeId);

		//创建Node
		Node node = new Node(nodeId, nodeAddr);
		CONN_NODE = node;

		ConnPort port = new ConnPort(serverId);
		port.startup(node);
		
		//开启一个默认服务
		ConnService connService = new ConnService(port);
		connService.startup();
		port.addService(connService);
		
		node.startup();
		
		//启动socket监听
		new Server().start();
		
		//启动日志信息
		LogCore.core.info("================================================");
		LogCore.core.info(nodeId + " started.");
		LogCore.core.info("Listen:" + nodeAddr);
		LogCore.core.info("ServerId:" + serverId);
		LogCore.core.info("================================================");
		
		//系统关闭时进行清理
		Runtime.getRuntime().addShutdownHook(new Thread() { 
			public void run() { 
				
			} 
		});
	}

}
