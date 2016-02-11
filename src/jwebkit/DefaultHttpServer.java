package jwebkit;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

import jwebkit.http.HttpMethodDispatchHandler;
import jwebkit.sql.*;
 
/**
 * Responsible for initialising and starting the HTTP server which manages the
 * tool
 * 
 * @author David J. Pearce
 *
 */
public class DefaultHttpServer {
	
	public static final int HTTP_PORT = 8080;
		
	// =======================================================================
	// Main Entry Point
	// =======================================================================
	public static void main(String[] argc) throws SQLException {
		Connection connection = getDatabaseConnection();
		SqlDatabase db = new SqlDatabase(connection);
		db.bindTable("users", new SqlTable.Column("name", SqlType.VARCHAR(10)));
		
		SocketConfig socketConfig = SocketConfig.custom()
				.setSoTimeout(15000)
				.setTcpNoDelay(true)
				.build();

		HttpServer server = ServerBootstrap.bootstrap()
				.setListenerPort(HTTP_PORT)
				.setServerInfo("Test/1.1")
				.setSocketConfig(socketConfig)
				.setExceptionLogger(new Logger())
				.registerHandler("*", new DefaultHandler(db))
				.create();

		try {
			server.start();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class DefaultHandler extends HttpMethodDispatchHandler {
		private final SqlDatabase db;
		
		public DefaultHandler(SqlDatabase db) {
			super(HttpMethodDispatchHandler.ALLOW_GET | HttpMethodDispatchHandler.ALLOW_POST);
			this.db = db;
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
				response.setEntity(new StringEntity("HELLO"));
			} catch(URISyntaxException e) {
				throw new HttpException("Invalid URI",e);
			}
		}
		
		public void post(HttpRequest request, HttpResponse response, HttpContext context)
				throws HttpException, IOException {
			if(request instanceof HttpEntityEnclosingRequest) {
				HttpEntityEnclosingRequest r = (HttpEntityEnclosingRequest) request;
				String entity = EntityUtils.toString(r.getEntity());				
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
    
    private static Connection getDatabaseConnection() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
		return conn;
	}
}
