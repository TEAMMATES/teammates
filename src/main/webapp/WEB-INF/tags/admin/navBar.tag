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
          <a href="<%=Const.WebPageURIs.ADMIN_HOME_PAGE%>">Create Instructor</a>
        </li>

        <li <c:if test="${fn:contains(data.getClass(), 'AdminSearchPage')}">class="active"</c:if>>
          <a href="<%=Const.WebPageURIs.ADMIN_SEARCH_PAGE%>">Search</a>
        </li>

        <li <c:if test="${fn:contains(data.getClass(), 'AdminSessionsPage')}">class="active"</c:if>>
          <a href="<%=Const.WebPageURIs.ADMIN_SESSIONS_PAGE%>">Sessions</a>
        </li>

        <li <c:if test="${fn:contains(data.getClass(), 'AdminEmail')}">class="active"</c:if>>
          <a href="<%=Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE%>">Email</a>
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
