package teammates.jsp;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.APIServlet;

public class LoginFilter implements Filter {
	private ArrayList<String> exclude;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		exclude = new ArrayList<String>();
		String[] excludedFiles = filterConfig.getInitParameter("ExcludedFiles")
				.split("|");
		for(int i=0; i<excludedFiles.length; i++){
			exclude.add(excludedFiles[i].trim());
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		if(exclude.contains(req.getRequestURI())) return;
		if(!APIServlet.isUserLoggedIn()){
			String link = req.getRequestURI();
			String query = req.getQueryString();
			if(query!=null) link+="?"+query;
			resp.sendRedirect(APIServlet.getLoginUrl(link));
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {}

}
