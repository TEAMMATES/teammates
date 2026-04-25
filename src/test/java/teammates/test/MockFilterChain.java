package teammates.test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * Mocks {@link FilterChain} for testing purpose.
 *
 * <p>Only important methods are modified here; everything else are auto-generated.
 */
public class MockFilterChain implements FilterChain {

    private boolean invoked;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) {
        invoked = true;
    }

    /**
     * Returns whether the filter chain was invoked.
     */
    public boolean wasInvoked() {
        return invoked;
    }

}
