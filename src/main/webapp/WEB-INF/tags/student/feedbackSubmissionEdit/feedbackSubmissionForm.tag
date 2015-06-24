<%@ tag description="studentFeedbackSubmissionEdit.jsp - Display student feedback submission form" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackSubmissionEdit" prefix="feedbackSubmissionEdit" %>

<%@ attribute name="feedbackSubmissionForm" type="teammates.ui.controller.FeedbackSubmissionEditPageData" required="true" %>

<form method="post" name="form_student_submit_response" action="${feedbackSubmissionForm.submitAction}">
    <jsp:include page="<%=Const.ViewURIs.FEEDBACK_SUBMISSION_EDIT%>" />
    
    <div class="bold align-center"> 
        <c:if test="${feedbackSubmissionForm.moderation}">       
            <input name="moderatedstudent" value="${feedbackSubmissionForm.studentToViewPageAs.email}" type="hidden">
        </c:if>

        <c:choose>
            <c:when test="${empty feedbackSubmissionForm.bundle.questionResponseBundle}">
                    There are no questions for you to answer here!
            </c:when>
            <c:otherwise>
                <feedbackSubmissionEdit:submitButton
                    preview="${feedbackSubmissionForm.preview}" submittable="${feedbackSubmissionForm.submittable}" />
            </c:otherwise>
        </c:choose>
    </div>
    <br> 
    <br>
</form>