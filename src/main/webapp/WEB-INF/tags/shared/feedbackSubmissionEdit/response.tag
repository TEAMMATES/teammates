<%@ tag description="questionWithResponses.tag - Display question with responses" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>

<%@ attribute name="questionWithResponses" type="teammates.ui.template.StudentFeedbackSubmissionEditQuestionsWithResponses" required="true" %>
<%@ attribute name="response" type="teammates.ui.template.FeedbackSubmissionEditResponse" required="true" %>
<%@ attribute name="isSessionOpenForSubmission" type="java.lang.Boolean" required="true" %>
<%@ attribute name="firstIndex" required="true" %>
<%@ attribute name="secondIndex" required="true" %>
<%@ attribute name="thirdIndex" required="true" %>
<%@ attribute name="isInstructor" required="true" %>
<%@ attribute name="moderatedPersonEmail" required="true" %>
<c:set var="isNumResponsesMax" value="${questionWithResponses.numOfResponseBoxes eq questionWithResponses.maxResponsesPossible}"/>
<c:set var="isRecipientNameHidden" value="${questionWithResponses.question.recipientNameHidden}"/>
<c:set var="isRecipientTeam" value="${questionWithResponses.question.recipientTeam}"/>

<c:choose>
    <c:when test="${isRecipientNameHidden}"><c:set var="divClassType" value="col-sm-12"/></c:when>
    <c:when test="${isNumResponsesMax}"><c:set var="divClassType" value="col-sm-10"/></c:when>
    <c:otherwise><c:set var="divClassType" value="col-sm-8"/></c:otherwise>
</c:choose>

<c:set var="autoWidth" value="" />
<c:if test="${questionWithResponses.question.questionTypeConstsum}">
    <c:set var="autoWidth" value="width-auto" />
</c:if>

<br>
<div class="form-group margin-0">
    <div ${isNumResponsesMax ? 'class="col-sm-2 form-inline mobile-align-left"' : 'class="col-sm-4 form-inline mobile-align-left"'}
         ${isRecipientNameHidden ?  'style="display:none"' : 'style="text-align:right"'}>

        <label for="input">To${isRecipientTeam ? ' Team' : ''}: </label>

        <select class="participantSelect middlealign<c:if test="${not response.existingResponse}"> newResponse</c:if> form-control"
                name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT %>-${questionWithResponses.question.qnIndx}-${response.responseIndx}"
                ${isNumResponsesMax ? 'style="display:none;max-width:125px"' : 'style="width:275px;max-width:275px"'}
                ${isSessionOpenForSubmission ? '' : 'disabled' }>

                <c:forEach items="${response.recipientOptionsForQuestion}" var="option">
                    ${option}
                </c:forEach>
        </select>
    </div>
    <div class="${divClassType}<c:if test="${questionWithResponses.question.questionTypeConstsum}"> width-auto</c:if>">
      <c:choose>
        <c:when test="${questionWithResponses.studentCommentsOnResponsesAllowed}">
                <button type="button" class="btn btn-default btn-xs icon-button pull-right show-frc-add-form"
                            data-recipientindex="${firstIndex}" data-giverindex="${secondIndex}"
                            data-qnindex="${thirdIndex}" style="margin-bottom:1em;">
                            <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
                </button>
                ${response.submissionFormHtml}
        <br>
        <ul class="list-group" id="responseCommentTable-${firstIndex}-${secondIndex}-${thirdIndex}"
                style="${not empty response.commentsOnResponses ? 'margin-top:15px;': 'display:none'}">
                <c:forEach items="${response.commentsOnResponses}" var="responseComment" varStatus="status">
                    <shared:feedbackResponseCommentRow frc="${responseComment}" firstIndex="${firstIndex}"
                                                    secondIndex="${secondIndex}" thirdIndex="${thirdIndex}"
                                                    frcIndex="${status.count}" isOnFeedbackSubmissionEditPage="true" isInstructor="${isInstructor}"
                                                    moderatedPersonEmail="${moderatedPersonEmail}"/>
                </c:forEach>
                <shared:feedbackResponseCommentAdd frc="${response.responseExplainationComment}" firstIndex="${firstIndex}"
                                                    secondIndex="${secondIndex}" thirdIndex="${thirdIndex}" isOnFeedbackSubmissionEditPage="true" isInstructor="${isInstructor}"
                                                    moderatedPersonEmail="${moderatedPersonEmail}"
                                                    isPreview="${data.preview}" submitTable="${data.submittable}"/>
        </ul>
        </c:when>
        <c:otherwise>
        ${response.submissionFormHtml}
        </c:otherwise>
        </c:choose>
        <c:if test="${response.existingResponse}">
            <input type="hidden"
                name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>-${questionWithResponses.question.qnIndx}-${response.responseIndx}"
                value="<c:out value="${response.responseId}"/>">
        </c:if>
    </div>
</div>
