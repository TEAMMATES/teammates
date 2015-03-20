<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%
    PageData data = (PageData) request.getAttribute("data");
%>

<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle"
                data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span> 
                <span class="icon-bar"></span> 
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/index.html">TEAMMATES</a>
        </div>

        <div class="collapse navbar-collapse" id="contentLinks">

            <ul class="nav navbar-nav">
                <li
                    class="<%=data.getClass().toString()
                    .contains("AdminHomePage") ? "active" : ""%>">
                    <a href="<%=Const.ActionURIs.ADMIN_HOME_PAGE%>">Create
                        Instructor</a>
                </li>
                
                <li
                    class="<%=data.getClass().toString()
                    .contains("AdminAccountManagementPage") ? "active" : ""%>">
                    <a href="<%=Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE%>">Account
                        Management</a>
                </li>
                
                <li
                    class="<%=data.getClass().toString().contains("AdminSearchPage") ? "active"
                    : ""%>">
                    <a href="<%=Const.ActionURIs.ADMIN_SEARCH_PAGE%>">Search</a>
                </li>
                
                <li
                    class="<%=data.getClass().toString()
                    .contains("AdminActivityLogPage") ? "active" : ""%>">
                    <a href="<%=Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE%>">Activity
                        Log</a>
                </li>
                
                <li
                    class="<%=data.getClass().toString()
                    .contains("AdminSessionsPage") ? "active" : ""%>">
                    <a href="<%=Const.ActionURIs.ADMIN_SESSIONS_PAGE%>">Sessions</a>
                </li>

                <li
                    class="<%=data.getClass().toString()
                    .contains("AdminEmail") ? "active dropdown" : "dropdown"%>">

                    <a href="#" class="dropdown-toggle"
                    data-toggle="dropdown" role="button"
                    aria-expanded="false">Email <span
                        class="caret"></span></a> 
                    <ul class="dropdown-menu" role="menu">
                        <li>
                             <a href="<%=Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE%>">Email</a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a href="<%=Const.ActionURIs.ADMIN_EMAIL_LOG_PAGE%>">Email Log</a>
                        </li>
                    </ul>
                </li>
            </ul>

            <ul class="nav navbar-nav pull-right">
                <li>                
                    <a class="nav logout"
                        href="<%=Const.ViewURIs.LOGOUT%>">
                        <span class="glyphicon glyphicon-user"></span>
                        Logout <%
                        if (data.account.googleId.length() > Const.SystemParams.USER_ID_MAX_DISPLAY_LENGTH) {
                    %> (<span class="text-info" data-toggle="tooltip"
                            data-placement="bottom"
                            title="<%=data.account.googleId%>"> <%=PageData.truncate(data.account.googleId,
                            Const.SystemParams.USER_ID_MAX_DISPLAY_LENGTH)%>
                        </span>) <%
                            } else {
                        %> (<span class="text-info" data-toggle="tooltip"
                            data-placement="bottom"
                            title="<%=data.account.googleId%>"> <%=PageData.truncate(data.account.googleId,
                            Const.SystemParams.USER_ID_MAX_DISPLAY_LENGTH)%>
                        </span>) <%
                            }
                        %>
                    </a>  
                </li>
            </ul>

        </div>
    </div>
</div>