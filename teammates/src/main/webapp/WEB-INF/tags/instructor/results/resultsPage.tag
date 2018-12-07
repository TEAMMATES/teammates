<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Generic InstructorFeedbackResults Page" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="jsIncludes" %>
<ti:instructorPage title="${title}">
  <jsp:attribute name="jsIncludes">
    <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY_PRINTTHIS %>"></script>
    ${jsIncludes}
  </jsp:attribute>
  <jsp:body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_TOP%>" />
    <jsp:doBody />
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BOTTOM%>" />
  </jsp:body>
</ti:instructorPage>
