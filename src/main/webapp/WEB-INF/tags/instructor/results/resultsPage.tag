<%@ tag description="Generic InstructorFeedbackResults Page" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ attribute name="pageTitle" required="true" %>
<%@ attribute name="bodyTitle" required="true" %>
<%@ attribute name="jsIncludes" %>
<ti:instructorPage pageTitle="${pageTitle}" bodyTitle="${bodyTitle}">
    <jsp:attribute name="jsIncludes">
        <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY_PRINTTHIS %>"></script>
        <script type="text/javascript" src="/js/instructor.js"></script>
        <script type="text/javascript" src="/js/instructorFeedbackResults.js"></script>
        ${jsIncludes}
    </jsp:attribute>
    <jsp:body>
        <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_TOP%>" />
        <jsp:doBody />
        <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BOTTOM%>" />
    </jsp:body>
</ti:instructorPage>
