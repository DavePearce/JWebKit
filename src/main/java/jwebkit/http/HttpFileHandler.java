package jwebkit.http;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.protocol.HttpContext;

/**
 * Responsible for serving static files from the file system.
 *
 * @author David J. Pearce
 *
 */
public class HttpFileHandler extends HttpMethodDispatchHandler {
	/**
	 * The mime type for files handled by this server.
	 */
	private final ContentType mimeType;

	/**
	 * The root directory for the file store this server serves from.
	 */
	private final File rootDir;

	private static int CHUNK_SIZE = 1024;

	public HttpFileHandler(File rootDir, ContentType mimeType) {
		super(HttpMethodDispatchHandler.ALLOW_GET);
		this.rootDir = rootDir;
		this.mimeType = mimeType;
	}

	public HttpFileHandler(int mask, File rootDir, ContentType mimeType) {
		super(mask);
		this.rootDir = rootDir;
		this.mimeType = mimeType;
	}

	@Override
	public void get(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		try {
			String uri = request.getRequestLine().getUri();
			String path = new URIBuilder(uri).getPath();
			File file = new File(rootDir, path);
			response.setStatusCode(HttpStatus.SC_OK);
			response.setEntity(new FileEntity(file, mimeType));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void post(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		if (request instanceof HttpEntityEnclosingRequest) {
			post((HttpEntityEnclosingRequest) request, response, context);
		} else {
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
		}
	}

	protected void post(HttpEntityEnclosingRequest request, HttpResponse response, HttpContext context) {
		try {
			String uri = request.getRequestLine().getUri();
			String path = new URIBuilder(uri).getPath();
			HttpEntity entity = request.getEntity();
			// Ensure matching mime types. Whilst not really a strong safeguard, it's
			// nevertheless a good idea.
			if (mimeType.getMimeType().equals(entity.getContentType().getValue())) {
				File file = new File(rootDir, path);
				writeFile(entity.getContent(), file);
				response.setStatusCode(HttpStatus.SC_OK);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
	}

	/**
	 * Write a given file in chunks to the output stream.
	 *
	 * @param out
	 * @param file
	 * @throws IOException
	 */
	private void writeFile(InputStream in, File file) throws IOException {
		FileOutputStream fout = new FileOutputStream(file);
		byte[] bytes = new byte[CHUNK_SIZE];
		int nread;
		while ((nread = in.read(bytes, 0, CHUNK_SIZE)) == CHUNK_SIZE) {
			fout.write(bytes, 0, CHUNK_SIZE);
		}
		fout.write(bytes, 0, nread);
		fout.close();
		in.close();
	}

}
