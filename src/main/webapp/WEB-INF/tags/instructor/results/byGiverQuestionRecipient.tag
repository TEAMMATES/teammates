<%@ tag description="instructorFeedbackResults - by question" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="showAll" type="java.lang.Boolean" required="true" %>
<%@ attribute name="shouldCollapsed" type="java.lang.Boolean" required="true" %>

<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_TOP%>" />
    <br>
    <c:forEach items="${data.sectionPanels}" var="sectionPanel" varStatus="i">
        <results:sectionPanel showAll="${showAll}" sectionPanel="${sectionPanel.value}" shouldCollapsed="${shouldCollapsed}" sectionIndex="${i.index}" courseId="${data.courseId}" feedbackSessionName="${data.feedbackSessionName}"/>
    </c:forEach>
<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BOTTOM%>" />
