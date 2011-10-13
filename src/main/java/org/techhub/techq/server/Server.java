package org.techhub.techq.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server server = new Server();
		server.init();
		try{
			server.start();
		}catch(Throwable t){
			t.printStackTrace();
		}
		System.out.println("Server started. Browse http://localhost:9999/?script=hello%20world");
	}
	
	public void init(){
	}
	
	public void start(){
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ServerPipelineFactory());
		bootstrap.bind(new InetSocketAddress(9999));
	}
	
}