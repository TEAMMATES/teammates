<%--
    - @(#)
    - Description: This jsp file defines the black bar at the top of all 
    -                 student pages
 --%>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.ui.controller.PageData" %>
<%
    PageData data = (PageData)request.getAttribute("data");
    boolean isUnregistered = data.account.googleId == null 
            || (data.student != null && !data.student.isRegistered());
%>
        <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#contentLinks">
                         <span class="sr-only">Toggle navigation</span>
                         <span class="icon-bar"></span>
                         <span class="icon-bar"></span>
                         <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="/index.html">TEAMMATES</a>
                </div>
                <div class="collapse navbar-collapse" id="contentLinks">
                    <ul class="nav navbar-nav">
                        <li class="<%=data.getClass().toString().contains("StudentHome") ? "active":"" %>">
                            <a class="navLinks" id="studentHomeNavLink" href="<%=data.getStudentHomeLink(isUnregistered)%>"
                               <%=isUnregistered ? "data-unreg='true'" : "" %>>
                                Home
                            </a>
                        </li>
                        <li class="<%=data.getClass().toString().contains("StudentProfilePage") ? "active":""%>">
                            <a class="navLinks" id="studentProfileNavLink" 
                               href="<%=data.getStudentProfileLink(isUnregistered)%>"
                               <%=isUnregistered ? "data-unreg='true'" : "" %>>
                                Profile
                            </a>
                        </li>
                        <li class="<%=data.getClass().toString().contains("StudentComments") ? "active":""%>">
                            <a class="navLinks" id="studentCommentsNavLink" 
                               href="<%=data.getStudentCommentsLink(isUnregistered)%>"
                               <%=isUnregistered ? "data-unreg='true'" : "" %>>
                                Comments
                            </a>
                        </li>
                        <li class="<%=data.getClass().toString().contains("StudentHelp") ? "active":""%>">
                            <a id="studentHelpLink" class='nav' href="/studentHelp.html" target="_blank">Help</a>
                        </li>
                    </ul>
                    <% if (data.account != null && data.account.googleId != null) { %>
                    <ul class="nav navbar-nav pull-right">
                        <li>
                            <a class='nav logout' href="<%=Const.ViewURIs.LOGOUT%>">Logout
                                (<span class="text-info" data-toggle="tooltip" data-placement="bottom" 
                                        title="<%=data.account.googleId%>">
                                        <%=PageData.truncate(data.account.googleId,Const.SystemParams.USER_ID_MAX_DISPLAY_LENGTH)%>
                                 </span>)
                            </a>
                        </li>
                    </ul>
                    <% } %>
                </div>
            </div>
        </div>