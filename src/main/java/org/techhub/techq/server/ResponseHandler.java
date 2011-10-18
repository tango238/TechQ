package org.techhub.techq.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.jboss.netty.buffer.ChannelBuffer;
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
import org.techhub.techq.WebAppUtil;
import org.techhub.techq.compile.TechqCompiler;
import org.techhub.techq.compile.TechqCompilerException;

import com.sun.tools.javac.util.JCDiagnostic;

public class ResponseHandler extends SimpleChannelUpstreamHandler {

	private final TechqCompiler<Evaluatable> compiler = new TechqCompiler<Evaluatable>(
			Arrays.asList(new String[] { "-target", "1.6" }));

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		// This app will execute this variable as Java language script. 
		String script = null;
		HttpRequest req = (HttpRequest) e.getMessage();
		// 
		if (HttpMethod.POST.equals(req.getMethod())) {
			ChannelBuffer buffer = req.getContent();
			String paramStr = new String(buffer.array());
			Map<String, String> params = parseParameter(paramStr);
			script = params.get("script");
		}
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.getUri());
		Map<String, List<String>> queries = queryStringDecoder.getParameters();
		
		String result = "";
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
		HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		if (script != null) {
			// TODO Evaluates the script and converts to JSON format.
			String source = "import org.techhub.techq.Evaluatable; public class Main implements Evaluatable { @Override public String eval() { return \""
					+ script + "\"; } }";
			System.out.println(source);
			final DiagnosticCollector<JavaFileObject> errors = new DiagnosticCollector<JavaFileObject>();
			Class<Evaluatable> compiledClass = null;
			try {
				compiledClass = compiler.compile("Main", source, errors,
						new Class<?>[] { Evaluatable.class });
			} catch (TechqCompilerException ex) {
				DiagnosticCollector<JavaFileObject> diagnostics = ex.getDiagnostics();
				for(Object diagnostic : diagnostics.getDiagnostics()){
					JCDiagnostic jcDiagnostic = (JCDiagnostic) diagnostic;
					result = jcDiagnostic.getMessage(Locale.JAPANESE);
				}
			}

			if (compiledClass != null) {
				Evaluatable evaluator = compiledClass.newInstance();
				result = evaluator.eval();
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
		if (uri != null && uri.endsWith(".html")) {
			result = serveHtml(req.getUri());
		} else {
			result = serveHtml(WebAppUtil.WELCOME_PAGE); // welcome page
		}
		HttpHeaders.setContentLength(res, result.length());
		ChannelFuture future = e.getChannel().write(result);
		future.addListener(ChannelFutureListener.CLOSE);
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
	
	protected String serveHtml(String uri) {
		String webappDir = WebAppUtil.getWebAppRoot();
		if (uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		// read file
		FileReader fileReader = null;
		BufferedReader reader = null;
		StringBuilder content = new StringBuilder();
		try {
			fileReader = new FileReader(webappDir + uri);
			reader = new BufferedReader(fileReader);

			String read;
			while ((read = reader.readLine()) != null) {
				content.append(read);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fileReader != null){
				try {
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content.toString();
	}
}
