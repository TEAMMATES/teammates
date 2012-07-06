package teammates.storage.datastore;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Servlet Filter to support Datastore class
 */
public final class DatastoreFilter implements javax.servlet.Filter {

	@Override
	public void init(FilterConfig config) {
		Datastore.initialize();
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} finally {
			Datastore.finishRequest();
		}
	}

	@Override
	public void destroy() {
	}
}