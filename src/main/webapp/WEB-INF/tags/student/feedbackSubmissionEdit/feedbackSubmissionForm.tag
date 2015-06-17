<%@ tag description="studentFeedbackSubmissionEdit.jsp - Display student feedback submission form" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackSubmissionEdit" prefix="feedbackSubmissionEdit" %>

<%@ attribute name="submitAction" required="true" %>
<%@ attribute name="moderation" type="java.lang.Boolean" required="true" %>
<%@ attribute name="moderatedStudentEmail" required="true" %>
<%@ attribute name="questionResponseBundle" type="java.util.Map" required="true" %>
<%@ attribute name="preview" type="java.lang.Boolean" required="true" %>
<%@ attribute name="submittable" type="java.lang.Boolean" required="true" %>

<form method="post" name="form_student_submit_response" action="${submitAction}">
    <jsp:include page="<%=Const.ViewURIs.FEEDBACK_SUBMISSION_EDIT%>" />
    
    <div class="bold align-center"> 
        <c:if test="${moderation}">       
            <input name="moderatedstudent" value="${moderatedStudentEmail}" type="hidden">
        </c:if>

        <c:choose>
            <c:when test="${empty questionResponseBundle}">
                    There are no questions for you to answer here!
            </c:when>
            <c:otherwise>
                <feedbackSubmissionEdit:submitButton
                    preview="${preview}" submittable="${submittable}" />
            </c:otherwise>
        </c:choose>
    </div>
    <br> 
    <br>
</form>