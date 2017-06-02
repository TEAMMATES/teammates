<%@ tag description="instructorFeedbackResults - participant panel, within team or section panels" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="participantPanel" type="teammates.ui.template.InstructorFeedbackResultsParticipantPanel" required="true" %>

<%-- TODO use enum --%>
<%@ attribute name="isSecondaryParticipantType" type="java.lang.Boolean" required="true" %>

<c:choose>
    <c:when test="${isSecondaryParticipantType}">
        <results:participantGroupBySecondaryParticipantPanel groupByParticipantPanel="${participantPanel}"/>
    </c:when>
    <c:otherwise>
        <results:participantGroupByQuestionPanel groupByQuestionPanel="${participantPanel}"/>
    </c:otherwise>
</c:choose>
