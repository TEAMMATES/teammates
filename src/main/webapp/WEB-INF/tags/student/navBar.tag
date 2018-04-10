<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Student Navigation Bar" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<c:set var="isUnregistered" value="${data.unregisteredStudent}" />
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#contentLinks">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <t:teammatesLogo/>
    </div>
    <div class="collapse navbar-collapse" id="contentLinks">
      <ul class="nav navbar-nav">
        <li<c:if test="${fn:contains(data.getClass(), 'StudentHome')}"> class="active"</c:if>>
          <a class="navLinks" id="studentHomeNavLink" href="${data.studentHomeLink}"
              <c:if test="${isUnregistered}">data-unreg="true"</c:if>>
            Home
          </a>
        </li>
        <li<c:if test="${fn:contains(data.getClass(), 'StudentProfilePage')}"> class="active"</c:if>>
          <a class="navLinks" id="studentProfileNavLink" href="${data.studentProfileLink}"
              <c:if test="${isUnregistered}">data-unreg="true"</c:if>>
            Profile
          </a>
        </li>
        <li<c:if test="${fn:contains(data.getClass(), 'StudentHelp')}"> class="active"</c:if>>
          <a id="studentHelpLink" class="nav" href="/studentHelp.jsp" target="_blank" rel="noopener noreferrer">Help</a>
        </li>
      </ul>
      <c:if test="${not empty data.account && not empty data.account.googleId}">
        <ul class="nav navbar-nav pull-right">
          <li>
            <a id="btnLogout" class="nav logout" href="<%= Const.ActionURIs.LOGOUT %>">
              Logout (
              <span class="text-info" data-toggle="tooltip" title="${data.account.googleId}" data-placement="bottom">
                ${data.account.truncatedGoogleId}
              </span>
              )
            </a>
          </li>
        </ul>
      </c:if>
    </div>
  </div>
</div>
