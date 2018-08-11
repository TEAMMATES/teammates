<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbackEdit" prefix="feedbackEdit" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="feedbacks" %>

<c:set var="jsIncludes">
  <script type="text/javascript" src="<%= FrontEndLibrary.TINYMCE %>"></script>
  <script type="text/javascript" src="<%= FrontEndLibrary.MOMENT %>"></script>
  <script type="text/javascript" src="/data/moment-timezone-with-data-2013-2023.min.js"></script>
  <script type="text/javascript" src="/js/instructorFeedbackEdit.js"></script>
</c:set>

<c:set var="EMPTY_FEEDBACK_SESSION_MESSAGE">
  <%= Const.StatusMessages.FEEDBACK_QUESTION_EMPTY %>
</c:set>
<ti:instructorPage title="Edit Feedback Session" jsIncludes="${jsIncludes}">

  <feedbacks:feedbackSessionsForm fsForm="${data.fsForm}"
      courseName="${fn:escapeXml(data.courseDetails.course.getName())}"
      fsEnableEdit="${data.shouldLoadInEditMode}" fsAttributes="${data.feedbackSession}"/>

  <br>
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <ti:copyModal editCopyActionLink="${data.editCopyActionLink}" />

  <c:if test="${empty data.qnForms}">
    <br>
    <div class="align-center bold" id="empty_message">${EMPTY_FEEDBACK_SESSION_MESSAGE}</div>
    <br>
  </c:if>
  <br>
  <input type="hidden" id="num-questions" value="${fn:length(data.qnForms)}">
  <input type="hidden" id="num-students" value="${data.courseDetails.stats.studentsTotal}">
  <input type="hidden" id="num-teams" value="${data.courseDetails.stats.teamsTotal}">
  <input type="hidden" id="num-instructors" value="${data.numOfInstructors}">
  <c:forEach items="${data.qnForms}" var="question">
    <feedbackEdit:questionEditForm fqForm="${question}" />
  </c:forEach>

  <feedbackEdit:newQuestionForm fqForm="${data.newQnForm}" nextQnNum="${fn:length(data.qnForms) + 1}"/>
  <feedbackEdit:addTemplateQuestionModal feedbackSessionName="${data.fsForm.fsName}" courseId="${data.fsForm.courseId}"/>
  <feedbackEdit:copyQuestionModal feedbackSessionName="${data.fsForm.fsName}" courseId="${data.fsForm.courseId}"/>

  <br>
  <br>
  <feedbackEdit:previewSessionForm previewForm="${data.previewForm}" />

  <br>
  <br>
</ti:instructorPage>
