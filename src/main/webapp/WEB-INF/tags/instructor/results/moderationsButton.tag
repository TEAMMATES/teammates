<%@ tag description="instructorFeedbackResults - moderations button" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="moderationButton" type="teammates.ui.template.ModerationButton" required="true" %>

<form class="inline" method="post" action="${data.instructorEditStudentFeedbackLink}" target="_blank"> 
    <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" <c:if test="${moderationButton.disabled}">disabled="disabled"</c:if> data-toggle="tooltip" title="<%=Const.Tooltips.FEEDBACK_SESSION_MODERATE_FEEDBACK%>">
    <input type="hidden" name="courseid" value="${moderationButton.courseId}">
    <input type="hidden" name="fsname" value="${moderationButton.feedbackSessionName}">
    <input type="hidden" name="moderatedquestion" value="${moderationButton.questionNumber}">
    <input type="hidden" name="moderatedstudent" value="${possibleGiver}">
</form>

