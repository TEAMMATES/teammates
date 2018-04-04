<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Student/Instructor feedback submission form" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/shared/feedbackSubmissionEdit" prefix="tsfse" %>

<%@ attribute name="moderatedPersonEmail" required="true" %>

<form method="post" name="form_submit_response" action="${data.submitAction}">
  <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${data.bundle.feedbackSession.feedbackSessionName}">
  <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${data.bundle.feedbackSession.courseId}">
  <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN %>" value="${data.sessionToken}">

  <c:choose>
    <c:when test="${not empty data.account.googleId}">
      <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
    </c:when>
    <c:otherwise>
      <input type="hidden" name="<%=Const.ParamsNames.REGKEY %>" value="${data.encryptedRegkey}">
      <input type="hidden" name="<%=Const.ParamsNames.STUDENT_EMAIL %>" value="${data.account.email}">
    </c:otherwise>
  </c:choose>

  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <tsfse:feedbackSessionDetailsPanel feedbackSession="${data.bundle.feedbackSession}"/>

  <c:forEach items="${data.questionsWithResponses}" var="questionWithResponses">
    <tsfse:questionWithResponses isSessionOpenForSubmission="${data.sessionOpenForSubmission}"
        isShowRealQuestionNumber="${data.showRealQuestionNumber}"
        questionWithResponses="${questionWithResponses}"/>
  </c:forEach>

  <div class="bold align-center">
    <c:if test="${data.moderation}">
      <input name="moderatedperson" value="${moderatedPersonEmail}" type="hidden">
    </c:if>

    <c:choose>
      <c:when test="${empty data.bundle.questionResponseBundle}">
        There are no questions for you to answer here!
      </c:when>
      <c:otherwise>
        <input type="checkbox" name="sendsubmissionemail" ${data.isResponsePresent ? "" : "checked"}>
        Send me a confirmation email
        <button type="submit" class="btn btn-primary center-block margin-top-7px"
            id="response_submit_button" data-toggle="tooltip"
            data-placement="top" title="<%= Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE %>"
            <c:if test="${data.preview or (not data.submittable)}">
              disabled style="background: #66727A;"
            </c:if>>
          Submit Feedback
        </button>
      </c:otherwise>
    </c:choose>
  </div>
  <br>
  <br>
</form>
