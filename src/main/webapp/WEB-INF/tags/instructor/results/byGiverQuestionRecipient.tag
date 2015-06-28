<%@ tag description="instructorFeedbackResults - by question" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="data" type="teammates.ui.controller.InstructorFeedbackResultsPageData" required="true" %>
<%@ attribute name="showAll" type="java.lang.Boolean" required="true" %>
<%@ attribute name="shouldCollapsed" type="java.lang.Boolean" required="true" %>

<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_TOP%>" />
<br>


<c:forEach items="${data.questionPanels}" var="questionPanel" varStatus="i">
    <results:questionPanel questionIndex="${i.index}" showAll="${showAll}" questionPanel="${questionPanel}" shouldCollapsed="${shouldCollapsed}"/>
</c:forEach>

<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BOTTOM%>" />
