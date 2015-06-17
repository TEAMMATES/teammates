<%@ tag description="instructorFeedbackResults - moderations button" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="java.util.Map"%>
<%@ tag import="java.util.List"%>
<%@ tag import="teammates.common.util.Const"%>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ tag import="teammates.common.datatransfer.FeedbackSessionResponseStatus"%>
<%@ tag import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ tag import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ tag import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%@ tag import="teammates.common.datatransfer.FeedbackQuestionDetails"%>
<%@ tag import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="data" type="teammates.ui.controller.InstructorFeedbackResultsPageData" required="true" %>
<%@ attribute name="possibleGiver" required="true" %>
<%@ attribute name="isGiverTeamGiver"  type="java.lang.Boolean" required="true" %>
<%@ attribute name="questionNumber" required="true" %>

<% 
    boolean isAllowedToModerate = data.instructor.isAllowedForPrivilege(data.bundle.getSectionFromRoster(possibleGiver), 
                                                                    data.feedbackSessionName, 
                                                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
    String disabledAttribute = !isAllowedToModerate ? "disabled=\"disabled\"" : "";
%>
<form class="inline" method="post" action="${data.instructorEditStudentFeedbackLink}" target="_blank"> 
    <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" <%=disabledAttribute%> data-toggle="tooltip" title="<%=Const.Tooltips.FEEDBACK_SESSION_MODERATE_FEEDBACK%>">
    <input type="hidden" name="courseid" value="${data.courseId}">
    <input type="hidden" name="fsname" value="${data.feedbackSessionName}">
    <input type="hidden" name="moderatedquestion" value="${questionNumber}">
    <c:choose>
        <c:when test="${isGiverTeamGiver}">
            <input type="hidden" name="moderatedstudent" value="${fn:replace(possibleGiver, data.teamOfEmailOwner,'')}">
        </c:when>
        <c:otherwise>
            <input type="hidden" name="moderatedstudent" value="${possibleGiver}">
        </c:otherwise>
    </c:choose>
</form>