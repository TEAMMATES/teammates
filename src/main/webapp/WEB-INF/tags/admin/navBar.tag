<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Admin Navigation Bar" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <t:teammatesLogo/>
    </div>

    <div class="collapse navbar-collapse" id="contentLinks">

      <ul class="nav navbar-nav">
        <li <c:if test="${fn:contains(data.getClass(), 'AdminHomePage')}">class="active"</c:if>>
          <a href="<%=Const.ActionURIs.ADMIN_HOME_PAGE%>">Create Instructor</a>
        </li>

        <!-- The link to Account Management Page will be hidden until it's scalable.
        <li <c:if test="${fn:contains(data.getClass(), 'AdminAccountManagementPage')}">class="active"</c:if>>
          <a href="<%=Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE%>">Account Management</a>
        </li>-->

        <li <c:if test="${fn:contains(data.getClass(), 'AdminSearchPage')}">class="active"</c:if>>
          <a href="<%=Const.ActionURIs.ADMIN_SEARCH_PAGE%>">Search</a>
        </li>

        <li <c:if test="${fn:contains(data.getClass(), 'AdminActivityLogPage')}">class="active"</c:if>>
          <a href="<%=Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE%>">Activity Log</a>
        </li>

        <li <c:if test="${fn:contains(data.getClass(), 'AdminSessionsPage')}">class="active"</c:if>>
          <a href="<%=Const.ActionURIs.ADMIN_SESSIONS_PAGE%>">Sessions</a>
        </li>

        <li <c:if test="${fn:contains(data.getClass(), 'AdminEmail')}">class="active dropdown"</c:if>
            <c:if test="${not fn:contains(data.getClass(), 'AdminEmail')}">class="dropdown"</c:if>>
          <a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
            Email <span class="caret"></span>
          </a>
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
          <a id="btnLogout" class="nav logout" href="<%= Const.ActionURIs.LOGOUT %>">
            <span class="glyphicon glyphicon-user"></span> Logout

            (<span class="text-info" data-toggle="tooltip" title="${data.account.googleId}" data-placement="bottom">
              ${data.account.truncatedGoogleId}
            </span>)
          </a>
        </li>
      </ul>
    </div>
  </div>
</div>
