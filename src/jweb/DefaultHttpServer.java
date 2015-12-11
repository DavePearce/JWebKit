package jweb;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Locale;

import org.apache.http.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import jweb.util.HttpMethodDispatchHandler;
 
/**
 * Responsible for initialising and starting the HTTP server which manages the
 * tool
 * 
 * @author djp
 *
 */
public class DefaultHttpServer {
	
	public static final int HTTP_PORT = 8080;
		
	// =======================================================================
	// Main Entry Point
	// =======================================================================
	public static void main(String[] argc) {
		SocketConfig socketConfig = SocketConfig.custom()
				.setSoTimeout(15000)
				.setTcpNoDelay(true)
				.build();

		HttpServer server = ServerBootstrap.bootstrap()
				.setListenerPort(HTTP_PORT)
				.setServerInfo("Test/1.1")
				.setSocketConfig(socketConfig)
				.setExceptionLogger(new Logger())
				.registerHandler("*", new DefaultHandler())
				.create();

		try {
			server.start();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class DefaultHandler extends HttpMethodDispatchHandler {
		
		private static String data = "NOTHING";
		
		public DefaultHandler() {
			super(HttpMethodDispatchHandler.ALLOW_GET | HttpMethodDispatchHandler.ALLOW_POST);
		}
		
		@Override
		public void get(HttpRequest request, HttpResponse response, HttpContext context)
				throws HttpException, IOException {
			String uri = request.getRequestLine().getUri();
			try {
				List<NameValuePair> parameters = new URIBuilder(uri).getQueryParams();
				for(NameValuePair nvp : parameters) {
					System.err.println(nvp.getName() + " = " + nvp.getValue());
				}
				response.setStatusCode(HttpStatus.SC_OK);
				response.setEntity(new StringEntity("HELLO " + data));
			} catch(URISyntaxException e) {
				throw new HttpException("Invalid URI",e);
			}
		}
		
		public void post(HttpRequest request, HttpResponse response, HttpContext context)
				throws HttpException, IOException {
			if(request instanceof HttpEntityEnclosingRequest) {
				HttpEntityEnclosingRequest r = (HttpEntityEnclosingRequest) request;
				String entity = EntityUtils.toString(r.getEntity());
				data = entity;
			}
		}
	}
	
    private static class Logger implements ExceptionLogger {

        @Override
        public void log(final Exception ex) {
            if (ex instanceof SocketTimeoutException) {
                System.err.println(ex.getMessage());
            } else if (ex instanceof ConnectionClosedException) {
                System.err.println(ex.getMessage());
            } else {
                ex.printStackTrace();
            }
        }
    }
}
