package org.techhub.techq.server;

import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.techhub.techq.EvaluationContainer;
import org.techhub.techq.Lang;
import org.techhub.techq.java.JavaEvaluationContainer;
import org.techhub.techq.javascript.JavaScriptEvaluationContainer;
import org.techhub.techq.json.JsonParser;
import org.techhub.techq.json.Question;
import org.techhub.techq.ruby.RubyEvaluationContainer;
import org.techhub.techq.util.FileUtil;
import org.techhub.techq.util.WebAppUtil;

/**
 * 
 * @author tango
 * 
 */
public class ResponseHandler extends SimpleChannelUpstreamHandler {
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		// This app will execute this variable as script. 
		String script = null;
		String lang = null;
		
		HttpRequest req = (HttpRequest) e.getMessage();
//		System.out.println(req);
		if (is100ContinueExpected(req)) {
			 send100Continue(e);
		}
		
		if (HttpMethod.POST.equals(req.getMethod())) {
			ChannelBuffer buffer = req.getContent();
			String json = new String(buffer.array());
			JsonParser parser = new JsonParser();
			Question question = parser.parse(json);
			script = question.inputString;
			lang = question.languageName;
		}
		
		// TODO Erase the code below later, and will add implementation of "deny" when the server receive GET parameters.
		// From here
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.getUri());
		Map<String, List<String>> queries = queryStringDecoder.getParameters();		
		if (!queries.isEmpty()) {
			for (Entry<String, List<String>> p : queries.entrySet()) {
				String key = p.getKey();
				if ("script".equals(key)) {
					List<String> values = p.getValue();
					if (values != null && values.size() > 0) {
						script = values.get(0);
					}
				}
			}
		}
		// End.
		
		HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		res.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
		String result = "";
		
		if (script != null) {
			// TODO Evaluates the script and converts to JSON format.
			
			if(Lang.LANG_JAVA.equals(lang)){
				// For Java evaluation
				EvaluationContainer container = new JavaEvaluationContainer();
				result = container.runScript(script);
			}else if(Lang.LANG_RUBY.equals(lang)){
				// For Ruby evaluation
				EvaluationContainer container = new RubyEvaluationContainer();
				result = container.runScript(script);
			}else if(Lang.LANG_JS.equals(lang)){
				// For JavaScript evaluation
				JavaScriptEvaluationContainer container = new JavaScriptEvaluationContainer();
				result = container.runScript(script);
			}
			// スクリプトの実行結果もしくはエラー情報があればここでレスポンスをクライアントに返す
			if(result.length() > 0){
				HttpHeaders.setContentLength(res, result.length());
				ChannelFuture future = e.getChannel().write(result);
				future.addListener(ChannelFutureListener.CLOSE);
				return;
			}
		}
		
		String uri = req.getUri();
		if (uri != null && (uri.endsWith(".html") || uri.endsWith(".css") || uri.endsWith(".js"))) {
			result = serveStaticFile(req.getUri());
		} else {
			result = serveStaticFile(WebAppUtil.getDefaultDocument()); // welcome page
		}
		HttpHeaders.setContentLength(res, result.length());
		ChannelFuture future = e.getChannel().write(result);
		future.addListener(ChannelFutureListener.CLOSE);
	}

	private void send100Continue(MessageEvent e) {
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
		e.getChannel().write(response);
	}
	/**
	 * Parses POST parameter.
	 * @param parameter
	 * @return Map<String, String>
	 */
	protected Map<String, String> parseParameter(String paramStr){
		HashMap<String, String> map = new HashMap<String, String>();
		String[] params = paramStr.split("&");
		for(String param : params){
			String[] p = param.split("=");
			if(p != null && p.length == 2){
				try {
					map.put(p[0], URLDecoder.decode(p[1], "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	
	protected String serveStaticFile(String uri) {
		StringBuilder webappDir = new StringBuilder(WebAppUtil.getWebAppRoot());
		if(!webappDir.toString().endsWith("/")){
			webappDir.append("/");
		}
		return FileUtil.read(webappDir.toString() + uri);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}

}
