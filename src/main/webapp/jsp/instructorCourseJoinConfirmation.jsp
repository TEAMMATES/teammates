<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<c:set var="logoutUrl" value="<%= Const.ActionURIs.LOGOUT %>" />
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/instructorCourseJoinConfirmation.js"></script>
</c:set>
<ti:instructorPage title="Course Join Confirmation" jsIncludes="${jsIncludes}">
  <br>
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <br>
  <div class="panel panel-primary panel-narrow">
    <div class="panel-heading">
      <h3>Confirm your Google account</h3>
    </div>
    <div class="panel-body">
      <p class="lead">
        You are currently logged in as <span><strong>${data.account.googleId}</strong></span>.
        <br>If this is not you please <a href="${logoutUrl}">log out</a> and re-login using your own Google account.
        <br>If this is you, please confirm below to complete your registration. <br>
        <div class="align-center">
          <a href="${data.confirmationLink}" id="button_confirm" class="btn btn-success">Yes, this is my account</a>
          <a href="${logoutUrl}" id="button_cancel" class="btn btn-danger">No, this is not my account</a>
        </div>
      </p>
    </div>
  </div>
</ti:instructorPage>
