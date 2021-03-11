package de.gdiservice.cmdserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Properties;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

class CmdServer {
	
	static class Result {
		@JsonProperty
		private int exitCode;
		@JsonProperty
		private String stdout;
		@JsonProperty
		private String stderr;

		Result(int exitCode, String out, String err) {
			this.exitCode = exitCode;
			this.stdout = out;
			this.stderr = err;
		}

		@Override
		public String toString() {
			return "Result [exitCode=" + exitCode + ", out= \"" + stdout + "\", err=\"" + stderr + "\"]";
		}
		
	}
	
	class Consumer extends Thread {
		private BufferedReader in;
		private String result;

		Consumer(InputStream in) {
			// "ISO-8859-1"
			// System.out.println(Charset.defaultCharset());
			this.in = new BufferedReader( new InputStreamReader(in, Charset.defaultCharset()));
		}
		public void run() {
			StringBuilder sb =  new StringBuilder();
			String s;
			try {
				while ((s=in.readLine())!=null) {
					sb.append(s).append("\r\n");
				}
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
			this.result = sb.toString();
			System.out.println(result);
		}
		String getResult( ) {
			return result;
		}
	}

	private final Properties properties;
    private HttpServer server;
	
	CmdServer() throws IOException {
		Properties prop = new Properties();
		prop.load(this.getClass().getClassLoader().getResourceAsStream("cmd.properties"));
		this.properties = prop;
	}
	
	private String[] concat(String s, String[] params) {
		if (params!=null) {
			String[] res = new String[params.length+1];
			res[0] = s;
			System.arraycopy(params, 0, res, 1, params.length);
			return res;
		}
		return new String[] {s};
	}
	
	private Result runProzess(String cmd, String[] params) throws IOException {
		String[] cmds = concat(cmd, params);
		System.err.println("cmds="+Arrays.toString(cmds));
		ProcessBuilder pb = new ProcessBuilder(cmds);
		
		Process process = pb.start();
		Consumer stdout = new Consumer(process.getInputStream());
		stdout.start();
		Consumer stderr = new Consumer(process.getErrorStream());
		stderr.start();
		try {
			int code = process.waitFor();
			return new Result( code, stdout.getResult(), stderr.getResult());
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	private void start() {

		HttpServer server = HttpServer.createSimpleServer();
		server.getServerConfiguration().setDefaultQueryEncoding(Charset.forName("utf8"));
		server.getServerConfiguration().addHttpHandler(
		    new HttpHandler() {
		        public void service(Request request, Response response) throws Exception {
		        	System.err.println("getDecodedRequestURI:\""+request.getDecodedRequestURI()+"\"");
		        	System.err.println("pathInfo:\""+request.getPathInfo()+"\"");
		        	System.err.println("tool:\""+request.getParameter("tool")+"\"");
		        	System.err.println("param:\""+request.getParameter("param")+"\"");
		        	
		        	String tool = request.getParameter("tool");
		        	if (tool!=null) {
			        	String param = request.getParameter("param");
			        	String cmd = properties.getProperty(request.getParameter("tool"));
			        	if (cmd!=null) {
			        		String[] params = (param!=null) ? Tokenizer.getTokens(param) : null;
			        		if (params!=null) {
			        		    for (int i=0; i<params.length; i++) {
			        		        System.out.println(""+i+"\""+params[i]+"\"");
			        		    }
			        		}
				        	Result result = runProzess(cmd, params);		        	
				            response.setContentType("application/json");
				            ObjectMapper objectMapper = new ObjectMapper();
				            objectMapper.writer().writeValue(response.getOutputStream(), result);
			        	}
		        	}
		        }
		    });
		try {
		    server.start();
		    this.server = server;		    
		    Runtime.getRuntime().addShutdownHook(new ShutdownThread());
		    Thread.currentThread().join();
//		    System.out.println("Press any key to stop the server...");
//		    System.in.read();
		} catch (Exception e) {
		    System.err.println(e);
		
		}		
	}
	
	class ShutdownThread extends Thread {
	    public void run() {
	        CmdServer.this.server.shutdown();
	    }
	}

	public static void main(String[] args) {
		try {
			CmdServer server = new CmdServer();
			server.start();
		} 
		catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	

}