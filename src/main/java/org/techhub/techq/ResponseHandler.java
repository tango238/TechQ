package org.techhub.techq;


import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class ResponseHandler extends SimpleChannelUpstreamHandler {

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		HttpRequest req = (HttpRequest) e.getMessage();
		// TODO GETは許可しない
		if(!HttpMethod.POST.equals(req.getMethod())){
			// Error Response
			//return;
		}
		// TODO Evaluates the script and converts to JSON format. 
		String message = "hoge";
		
		HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		HttpHeaders.setContentLength(res, message.length());
		ChannelFuture future = e.getChannel().write(message);
		future.addListener(ChannelFutureListener.CLOSE);
	}
}
