<%@ page import="teammates.logic.api.*"%>
<%@ page import="teammates.common.util.Const"%>
<%      String nextUrl = request.getParameter(Const.ParamsNames.NEXT_URL);
        if (nextUrl == null) {
            nextUrl = "/index.html";
        }
        response.sendRedirect(Logic.getLogoutUrl(nextUrl)); 
%>