<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ page import="teammates.logic.api.*"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.StringHelper"%>
<%      String nextUrl = request.getParameter(Const.ParamsNames.NEXT_URL);
        String expectedId = request.getParameter(Const.ParamsNames.HINT);
        String actualId = request.getParameter(Const.ParamsNames.USER_ID);
        if (nextUrl == null) {
            nextUrl = "/index.html";
        } 
        if (expectedId == null || actualId == null) {
            response.sendRedirect(Logic.getLogoutUrl(nextUrl));
        } else {
            try {
                expectedId = StringHelper.decrypt(expectedId);
                actualId = StringHelper.decrypt(actualId);
            } catch (Exception e) {
            	response.sendRedirect(Logic.getLogoutUrl(nextUrl));
            }
            pageContext.setAttribute("expectedId", expectedId);
            pageContext.setAttribute("actualId", actualId);
            pageContext.setAttribute("logoutUrl", Logic.getLogoutUrl(nextUrl));
            pageContext.setAttribute("homePage", Const.ActionURIs.STUDENT_HOME_PAGE);
%>
            <t:errorPage>
                <div class="panel panel-primary panel-narrow">
                    <div class="panel-heading">
                        <h4>
                            Google Account Hint 
                        </h4>
                    </div>
                    <div class="panel-body">
                        <p>
                            The link you provided belongs to a user with Google ID (partially obscured for security) <strong>"${expectedId}"</strong> 
                            while you are currently logged in as <strong>"${actualId}"</strong>.
                        </p>
                        <ul class="small narrow-slight">
                            <li>
                                If the Google ID <strong>"${expectedId}"</strong> 
                                belongs to you, please proceed to the login page.
                            </li>
                            <li>
                                If that Google ID does not belong to you, please inform us at 
                                <a class="link">teammates@comp.nus.edu.sg</a>. Please also forward us the original email containing the 
                                link you clicked, to help us with the troubleshooting.
                            </li>
                        </ul>
                    </div>
                    <div class="panel-footer center-block align-center container-fluid">
                        <a class="btn btn-primary" href="${logoutUrl}">
                            Proceed to Login Page
                        </a>
                        <a class="btn btn-default" href="${homePage}">
                            Go to Home Page
                        </a>
                    </div>
                </div>
            </t:errorPage>
<% } %>