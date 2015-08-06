<%@ tag description="feedbackSubmissionEdit.jsp - Display question with responses" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackSubmissionEdit" prefix="feedbackSubmissionEdit" %>

<%@ attribute name="questionWithResponses" type="teammates.ui.template.StudentFeedbackSubmissionEditQuestionsWithResponses" required="true" %>
<%@ attribute name="isShowRealQuestionNumber" type="java.lang.Boolean" required="true" %>
<%@ attribute name="isSessionOpenForSubmission" type="java.lang.Boolean" required="true" %>

<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_TYPE %>-${questionWithResponses.question.qnIndx}" 
                     value="${questionWithResponses.question.questionType}">
                     
<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_ID %>-${questionWithResponses.question.qnIndx}" 
                     value="${questionWithResponses.question.questionId}">
                     
<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL %>-${questionWithResponses.question.qnIndx}" 
                     value="${questionWithResponses.numOfResponseBoxes}">

<div class="form-horizontal">
    <div class="panel panel-primary<c:if test="${questionWithResponses.question.moderatedQuestion}"> moderated-question</c:if>">
    
        <div class="panel-heading">Question ${isShowRealQuestionNumber ? questionWithResponses.question.questionNumber 
                                                                             : questionWithResponses.question.qnIndx}:
            <br>
            <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
            <span class="text-preserve-space"><c:out value="${questionWithResponses.question.questionText}"/></span>
        </div>
        
        <div class="panel-body">
            <p class="text-muted">Only the following persons can see your responses: </p>
            <ul class="text-muted">
                <c:if test="${empty questionWithResponses.question.visibilityMessages}">
                    <li class="unordered">No-one but the feedback session creator can see your responses.</li>
                </c:if>
                    
                <c:forEach items="${questionWithResponses.question.visibilityMessages}" var="line">
                    <li class="unordered">${line}</li>
                </c:forEach>
            </ul>
                
            <c:forEach items="${questionWithResponses.responses}" var="response">
                <feedbackSubmissionEdit:response response="${response}" isSessionOpenForSubmission="${isSessionOpenForSubmission}" 
                                                 questionWithResponses="${questionWithResponses}"/>
            </c:forEach>             
        </div>
    </div>
</div>
<br><br>