package org.techhub.techq.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.techhub.techq.util.WebAppUtil;

/**
 * 
 * @author tango
 * 
 */
public class Server {

	private int port = 9999;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Server server = new Server();
		
		if(args.length == 1){
			server.port = Integer.parseInt(args[0]);
		}
		
		server.init();
		try{
			server.start();
		}catch(Throwable t){
			t.printStackTrace();
		}
	}
	
	protected void init() {
		WebAppUtil.validate();
	}
	
	public void start(){
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ServerPipelineFactory());
		bootstrap.bind(new InetSocketAddress(port));
		
		System.out.println("Server started. Browse http://localhost:" + port + "/");
	}
	
}
