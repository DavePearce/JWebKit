package jwebkit.http;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
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

	@Override
	public void get(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		try {
			String uri = request.getRequestLine().getUri();
			String path = new URIBuilder(uri).getPath();
			File file = new File(rootDir, path);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			writeFile(os, file);
			response.setStatusCode(HttpStatus.SC_OK);
			response.setEntity(new ByteArrayEntity(os.toByteArray(), mimeType));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
		}
	}

	/**
	 * Write a given file in chunks to the output stream.
	 * @param out
	 * @param file
	 * @throws IOException
	 */
	private void writeFile(OutputStream out, File file) throws IOException {
		FileInputStream fin = new FileInputStream(file);
		byte[] bytes = new byte[CHUNK_SIZE];
		int nread;
		while ((nread = fin.read(bytes, 0, CHUNK_SIZE)) == CHUNK_SIZE) {
			out.write(bytes, 0, CHUNK_SIZE);
		}
		out.write(bytes, 0, nread);
		fin.close();
	}
}
