package org.techhub.techq.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class Server {

	private int port;
	
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
	}
	
	public synchronized void init(){
		port = 9999;
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
