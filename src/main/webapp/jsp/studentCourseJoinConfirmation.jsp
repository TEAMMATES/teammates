<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/studentCourseJoinConfirmation.js"></script>
</c:set>
<ts:studentPage title="Course Join Confirmation" jsIncludes="${jsIncludes}">
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <br>
  <div class="panel panel-primary panel-narrow">
    <div class="panel-heading">
      <h4>Confirm your Google account</h4>
    </div>
    <div class="panel-body">
      <p>
        You are currently logged in as <span><strong>${data.account.googleId}</strong></span>.
        <c:if test="${data.redirectResult}">
          You have been redirected to this page because you navigated to a link for the course <strong>${data.courseId}</strong>, which you have not been registered in.
        </c:if>
        <br>
        <c:choose>
          <c:when test="${data.redirectResult}">
            If you wish to register as <strong>${data.account.googleId}</strong>, please confirm below to complete your registration.
            <br>If you wish to register with another Google ID<c:if test="${data.nextUrlAccessibleWithoutLogin}"> or do not wish to register for this course</c:if>, please
            <a href="${data.logoutUrl}">log out</a>, log in with your desired Google ID<c:if test="${data.nextUrlAccessibleWithoutLogin}"> if necessary</c:if>, and navigate to the link again.
            <br>
          </c:when>
          <c:otherwise>
            If this is not you please <a href="${data.logoutUrl}">log out</a> and re-login using your own Google account.
            <br>If this is you, please confirm below to complete your registration.
            <br>
          </c:otherwise>
        </c:choose>
      </p>
      <div class="align-center">
        <a href="${data.confirmUrl}" class="btn btn-success" id="button_confirm">
          <c:choose>
            <c:when test="${data.redirectResult}">
              Register as <strong>${data.account.googleId}</strong>
            </c:when>
            <c:otherwise>
              Yes, this is my account
            </c:otherwise>
          </c:choose>
        </a>
        <a href="${data.logoutUrl}" class="btn btn-danger" id="button_cancel">
          <c:choose>
            <c:when test="${data.redirectResult}">
              Do not register as <strong>${data.account.googleId}</strong>
            </c:when>
            <c:otherwise>
              No, this is not my account
            </c:otherwise>
          </c:choose>
        </a>
      </div>

    </div>
  </div>
</ts:studentPage>
