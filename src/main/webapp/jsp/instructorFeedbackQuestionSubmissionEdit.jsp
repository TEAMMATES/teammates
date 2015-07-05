<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/feedbackSubmissionsEdit.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
</c:set>

<ti:instructorPage pageTitle="TEAMMATES - Submit Feedback Question" bodyTitle="Submit Feedback Question" jsIncludes="${jsIncludes}">
</ti:instructorPage>

<body>
    <%
        if (!data.isHeaderHidden()) {
    %>
            <jsp:include page="<%= Const.ViewURIs.INSTRUCTOR_HEADER %>" />
    <%
        } else if (data.isPreview()) {
    %>
        <nav class="navbar navbar-default navbar-fixed-top">
            <h3 class="text-center">Previewing Session as Instructor <%= data.getPreviewInstructor().name %> (<%= data.getPreviewInstructor().email %>)</h3>
        </nav>
    <%
        }
    %>

    <div class="container" id="mainContent">
        <div id="topOfPage"></div>
        <h1>Submit Feedback Question</h1>
        <br>

        <form method="post" action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_SAVE %>" name="form_submit_response">

            <jsp:include page="<%= Const.ViewURIs.FEEDBACK_SUBMISSION_EDIT %>" />

            <div class="bold align-center">
                <%
                    if (data.bundle.questionResponseBundle.isEmpty()) {
                %>
                        There are no questions for you to answer here!
                <%
                    } else if (data.isPreview() || !data.isSessionOpenForSubmission()) {
                %>
                        <input disabled="disabled" type="submit" class="btn btn-primary center-block" id="response_submit_button" data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE %>" value="Submit Feedback">
                <%
                    } else {
                %>
                        <input type="submit" class="btn btn-primary center-block" id="response_submit_button" data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE %>" value="Submit Feedback">
                <%
                    }
                %>
            </div>
            <br><br>
        </form>
    </div>

    <div id="frameBottom">
     
    </div>
</body>
