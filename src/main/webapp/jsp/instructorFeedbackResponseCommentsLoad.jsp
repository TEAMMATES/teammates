<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType" %>
<%@ page import="teammates.common.util.Const" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="hidden number-of-pending-comments">${data.numberOfPendingComments}</div>

<c:choose>
    <c:when test="${empty data.feedbackResultsBundle}">
        <div id="no-comment-panel" style="">
	        <br>
	        <div class="panel panel-info">
	            <ul class="list-group comments">
	                <li class="list-group-item list-group-item-warning">
	                    You don't have any comment in this session.
	                </li>
	            </ul>
	        </div>
	    </div>
    </c:when>
    <c:otherwise>
        <c:set var="fsIndex" value="${data.feedbackSessionIndex}" />
        <c:set var="ifrc" value="${data.instructorFeedbackResponseComment}"/>
        <c:forEach var="responseEntries" items="${data.feedbackResultsBundle.questionResponseMap}" varStatus="responseEntriesStatus">
            <div class="panel panel-info">
                <div class="panel-heading">
                    <c:set var="question" value="${data.feedbackResultsBundle.questions[responseEntries.key.id]}"/>
                    <c:set var="questionDetails" value="${question.questionDetails}"/>
			        <b>Question ${responseEntries.key.questionNumber}</b>:
			        ${questionDetails.questionText}
			        ${question.questionAdditionalInfoHtml}
			    </div>
			    <table class="table">
                    <tbody>
                        <c:forEach var="responseEntry" items="${responseEntries.value}" varStatus="responseEntryStatus">
                            <c:set var="giverName" value="${ifrc.giverNames[responseEntry]}"/>
                            <c:set var="recipientName" value="${ifrc.recipientNames[responseEntry]}"/>
                            <tr>
                                <td><b>From:</b> ${giverName} <b>To:</b> ${recipientName}</td>
                            </tr>
                            <tr>
                                <td><strong>Response: </strong>${ifrc.responseEntryAnswerHtmls[responseEntry]}</td>
                            </tr>
                            <tr class="active">
	                            <td>Comment(s):
	                                <button type="button"
	                                        class="btn btn-default btn-xs icon-button pull-right"
	                                        id="button_add_comment-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}"
	                                        onclick="showResponseCommentAddForm(${fsIndex},${responseEntriesStatus.count},${responseEntryStatus.count})"
	                                        data-toggle="tooltip" data-placement="top"
	                                        title="<%= Const.Tooltips.COMMENT_ADD %>"
	                                        <c:if test="${not ifrc.instructorAllowedToSubmit[responseEntry]}">disabled="disabled"</c:if>>
	                                    <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
	                                </button>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>
                                    <c:set var="frcList" value="${ifrc.feedbackResponseCommentsList[responseEntry.id]}"/>
	                                <ul class="list-group comments"
	                                    id="responseCommentTable-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}"
	                                    <c:if test="${empty frcList}">style="display: none;"</c:if>>
	                                    <c:forEach var="frc" items="${frcList}" varStatus="frcStatus">
                                            <shared:feedbackResponseComment frc="${frc}"
                                                                            firstIndex="${fsIndex}"
                                                                            secondIndex="${responseEntriesStatus.count}"
                                                                            thirdIndex="${responseEntryStatus.count}"
                                                                            frcIndex="${frcStatus.count}" />
                                        </c:forEach>
                                        <shared:feedbackResponseCommentAdd frc="${ifrc.feedbackResponseCommentAdd[question]}"
                                                                           firstIndex="${fsIndex}"
                                                                           secondIndex="${responseEntriesStatus.count}"
                                                                           thirdIndex="${responseEntryStatus.count}" />
	                                </ul>
	                            </td>
	                        </tr>
                        </c:forEach>
		            </tbody>
	            </table>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>
