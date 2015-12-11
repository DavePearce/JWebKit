package jweb;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Locale;

import org.apache.http.ConnectionClosedException;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
 
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
	
	private static class DefaultHandler implements HttpRequestHandler {

		@Override
		public void handle(HttpRequest request, HttpResponse response, HttpContext context)
				throws HttpException, IOException {
			String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
			System.out.println("GOT: " + method);
			response.setStatusCode(HttpStatus.SC_OK);
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
