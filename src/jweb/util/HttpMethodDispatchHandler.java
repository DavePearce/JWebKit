package jweb.util;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

public abstract class HttpMethodDispatchHandler implements HttpRequestHandler {

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
		if(method.equals("GET")) {
			get(request,response,context);
		} else if(method.equals("POST")) {
			post(request,response,context);
		} else if(method.equals("PUT")) {
			put(request,response,context);
		} else if(method.equals("DELETE")) {
			delete(request,response,context);
		} else {
			throw new HttpException("Unknown HTTP method: " + method);
		}
	}

	protected abstract void get(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException;
	
	protected abstract void put(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException;
	
	protected abstract void post(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException;
	
	protected abstract void delete(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException;
}
