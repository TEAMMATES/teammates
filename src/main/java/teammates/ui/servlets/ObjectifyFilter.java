package teammates.ui.servlets;

import java.io.Closeable;
import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import com.googlecode.objectify.ObjectifyService;

/**
 * Filter to activate Objectify service.
 *
 * <p>This is adapted from the official ObjectifyFilter class
 * with modification to support jakarta.servlet in place of javax.servlet.
 *
 * @see <a href="https://github.com/objectify/objectify/blob/6.0.7/src/main/java/com/googlecode/objectify/ObjectifyFilter.java">https://github.com/objectify/objectify/blob/6.0.7/src/main/java/com/googlecode/objectify/ObjectifyFilter.java</a>
 */
public class ObjectifyFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try (Closeable ignored = ObjectifyService.begin()) {
            chain.doFilter(request, response);
        }
    }
}
