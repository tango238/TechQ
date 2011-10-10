package org.techhub.techq.server;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

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
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.techhub.techq.Evaluatable;
import org.techhub.techq.compile.TechqCompiler;
import org.techhub.techq.compile.TechqCompilerException;

public class ResponseHandler extends SimpleChannelUpstreamHandler {

	private final TechqCompiler<Evaluatable> compiler = new TechqCompiler<Evaluatable>(Arrays.asList(new String[] { "-target", "1.6" }));
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		HttpRequest req = (HttpRequest) e.getMessage();
		// TODO GETは許可しない
		if(!HttpMethod.POST.equals(req.getMethod())){
			// Error Response
			//return;
		}
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.getUri());
		Map<String, List<String>> params = queryStringDecoder.getParameters();
		String script = "";
		if (!params.isEmpty()) {
			for (Entry<String, List<String>> p: params.entrySet()) {
				String key = p.getKey();
				if("script".equals(key)){
					List<String> values = p.getValue();
					if(values != null && values.size() > 0){
						script = values.get(0);
					}
				}
			}
		}
		// TODO Evaluates the script and converts to JSON format. 
		String source = "import org.techhub.techq.Evaluatable; public class Main implements Evaluatable { @Override public String eval() { return \"" + script + "\"; } }";
		final DiagnosticCollector<JavaFileObject> errors = new DiagnosticCollector<JavaFileObject>();
		Class<Evaluatable> compiledClass = null;
		try{
			compiledClass = compiler.compile("Main", source, errors, new Class<?>[] { Evaluatable.class });
		}catch(TechqCompilerException ex){
			DiagnosticCollector<JavaFileObject> diagnostics = ex.getDiagnostics();
			System.out.println(diagnostics);
		}
		
		String result = "no result";
        if(compiledClass != null){
			Evaluatable evaluator = compiledClass.newInstance();
			result = evaluator.eval();
        }
		
		HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		HttpHeaders.setContentLength(res, result.length());
		ChannelFuture future = e.getChannel().write(result);
		future.addListener(ChannelFutureListener.CLOSE);
	}
}
