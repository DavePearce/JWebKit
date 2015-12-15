package jwebkit.http;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

/**
 * Provides a base class for dispatching HTTP requests based on their method
 * kind. This allows easy development of RESTful APIs, and fine-grained control
 * over what request methods are supported by a resource.
 * 
 * @author David J. Pearce
 *
 */
public abstract class HttpMethodDispatchHandler implements HttpRequestHandler {

	public static final int ALLOW_GET = 1;
	
	public static final int ALLOW_POST = 2;
	
	public static final int ALLOW_PUT = 4;
	
	public static final int ALLOW_DELETE = 8;
	
	private final int mask;
	
	public HttpMethodDispatchHandler(int mask) {
		this.mask = mask;
	}
	
	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
		if(method.equals("GET") && (mask & ALLOW_GET) != 0) {
			get(request,response,context);
		} else if(method.equals("POST") && (mask & ALLOW_POST) != 0) {
			post(request,response,context);
		} else if(method.equals("PUT") && (mask & ALLOW_PUT) != 0) {
			put(request,response,context);
		} else if(method.equals("DELETE") && (mask & ALLOW_DELETE) != 0) {
			delete(request,response,context);
		} else {
			response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			response.setEntity(new StringEntity("Unsupported method: " + method));
		}
	}

	protected void get(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		
	}
	
	protected void put(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		
	}
	
	protected void post(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		
	}
	
	protected void delete(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		
	}
}
