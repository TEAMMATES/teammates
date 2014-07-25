package teammates.ui.controller;

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

import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.Logic;

public class LoginFilter implements Filter {
    private ArrayList<String> exclude;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String param = filterConfig.getInitParameter("ExcludedFiles");
        if(param==null) return;
        String[] excludedFiles = param.split("[|]");
        exclude = new ArrayList<String>();
        for(int i=0; i<excludedFiles.length; i++){
            exclude.add(excludedFiles[i].trim());
        }
        for (int i = 0 ; i < Const.SystemParams.PAGES_ACCESSIBLE_WITHOUT_GOOGLE_LOGIN.size() ; i ++) {
            exclude.add(Const.SystemParams.PAGES_ACCESSIBLE_WITHOUT_GOOGLE_LOGIN.get(i));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        if(exclude.contains(req.getRequestURI())){
            chain.doFilter(request, response);
            return;
        }
        if(!Logic.isUserLoggedIn()){
            Utils.getLogger().info("User is not logged in");
            String link = req.getRequestURI();
            String query = req.getQueryString();
            if(query!=null) link+="?"+query;
            resp.sendRedirect(Logic.getLoginUrl(link));
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {}

}
