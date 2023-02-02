package teammates.ui.servlets;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import teammates.common.util.HibernateUtil;
import teammates.common.util.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class HibernateSessionRequestFilter implements Filter {

    private SessionFactory sessionFactory;
    private static final Logger log = Logger.getLogger();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        Session currentSession = sessionFactory.getCurrentSession();

        log.info("Beginning transaction in session: " + currentSession);
        currentSession.beginTransaction();

        chain.doFilter(request, response);

        currentSession.getTransaction().commit();
        log.info("Transaction successfully committed in session: " + currentSession);

        currentSession.close();
    }

    @Override
    public void destroy() {
        // nothing to do
    }
}
