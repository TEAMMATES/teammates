package teammates.storage.datastore;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

//TODO: Do we really need this filter? To be reconsidered.
/**
 * Servlet Filter to ensure that the datastore is initialized before a request
 * is processed.
 */
public final class DatastoreFilter implements javax.servlet.Filter {

    @Override
    public void init(FilterConfig config) {
        Datastore.initialize();
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        //There was a Datastore.finishRequest() here inside a finally clause.
        //  It was removed at 4.19 because some requests span multiple entity
        //  groups. We are not allowed to apply transactions to such requests.
        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {
    }
}