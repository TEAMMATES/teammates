<%@ tag description="questionWithResponses.tag - Display question with responses" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="questionWithResponses" type="teammates.ui.template.StudentFeedbackSubmissionEditQuestionsWithResponses" required="true" %>
<%@ attribute name="response" type="teammates.ui.template.FeedbackSubmissionEditResponse" required="true" %>
<%@ attribute name="isSessionOpenForSubmission" type="java.lang.Boolean" required="true" %>

<c:set var="isNumResponsesMax" value="${questionWithResponses.numOfResponseBoxes eq questionWithResponses.maxResponsesPossible}"/>
<c:set var="isRecipientNameHidden" value="${questionWithResponses.question.recipientNameHidden}"/>

<c:choose>
    <c:when test="${isRecipientNameHidden}"><c:set var="divClassType" value="class=\"col-sm-12\""/></c:when>
    <c:when test="${isNumResponsesMax}"><c:set var="divClassType" value="class=\"col-sm-10\""/></c:when>
    <c:otherwise><c:set var="divClassType" value="class=\"col-sm-8\""/></c:otherwise>
</c:choose>

<br>
<div class="form-group margin-0">
    <div ${isNumResponsesMax ? 'class="col-sm-2 form-inline"' : 'class="col-sm-4 form-inline"'}
         ${isRecipientNameHidden ?  'style="display:none"' : 'style="text-align:right"'}>
         
        <label for="input">To: </label>
        
        <select class="participantSelect middlealign<c:if test="${not response.existingResponse}"> newResponse</c:if> form-control" 
                name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT %>-${questionWithResponses.question.qnIndx}-${response.responseIndx}"
                ${isNumResponsesMax ? 'style="display:none;max-width:125px"' : 'style="width:275px;max-width:275px"'}
                ${isSessionOpenForSubmission ? '' : 'disabled="disabled"' }>
                
                <c:forEach items="${response.recipientOptionsForQuestion}" var="option">
                    ${option}
                </c:forEach>
        </select>
    </div>
    <div ${divClassType}>
        ${response.submissionFormHtml}
        <c:if test="${response.existingResponse}">
            <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>-${questionWithResponses.question.qnIndx}-${response.responseIndx}"
                   value="<c:out value="${response.responseId}"/>">
        </c:if>                                    
    </div>
</div>