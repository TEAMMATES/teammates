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
                                <td><strong>Response: </strong>${ifrc.responseEntryAnswerHtmls[questionDetails]}</td>
                            </tr>
                            <tr class="active">
	                            <td>Comment(s):
	                                <button type="button"
	                                        class="btn btn-default btn-xs icon-button pull-right"
	                                        id="button_add_comment-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}"
	                                        onclick="showResponseCommentAddForm(${fsIndex},${responseEntriesStatus.count},${responseEntryStatus.count})"
	                                        data-toggle="tooltip" data-placement="top"
	                                        title="<%= Const.Tooltips.COMMENT_ADD %>"
	                                        <c:if test="${not ifrc.instructorAllowedToSubmit}">disabled="disabled"</c:if>>
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
                                        <!-- Find a way to reuse the shared tag for this similar portion -->
				                        <li class="list-group-item list-group-item-warning"
				                            id="showResponseCommentAddForm-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}"
				                            style="display: none;">
				                            <form class="responseCommentAddForm">
				                                <div class="form-group">
				                                    <div class="form-group form-inline">
				                                        <div class="form-group text-muted">
				                                            <p>
				                                                Giver: ${giverName}
				                                                <br>
				                                                Recipient: ${recipientName}
				                                            </p>
				                                            You may change comment's
				                                            visibility using the
				                                            visibility options on the
				                                            right hand side.</div>
				                                        <a id="frComment-visibility-options-trigger-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}"
				                                           class="btn btn-sm btn-info pull-right"
				                                           onclick="toggleVisibilityEditForm(${fsIndex},${responseEntriesStatus.count},${responseEntryStatus.count})">
				                                            <span class="glyphicon glyphicon-eye-close"></span>
				                                            Show Visibility Options
				                                        </a>
				                                    </div>
				                                    <div id="visibility-options-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}"
				                                         class="panel panel-default"
				                                         style="display: none;">
				                                        <div class="panel-heading">Visibility Options</div>
				                                        <table class="table text-center"
				                                               style="color: #000;"
				                                               style="background: #fff;">
				                                            <tbody>
				                                                <tr>
				                                                    <th class="text-center">User/Group</th>
				                                                    <th class="text-center">Can see your comment</th>
				                                                    <th class="text-center">Can see your name</th>
				                                                </tr>
				                                                <tr id="response-giver-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}">
				                                                    <td class="text-left">
				                                                        <div data-toggle="tooltip"
				                                                             data-placement="top"
				                                                             title=""
				                                                             data-original-title="Control what response giver can view">
				                                                            Response Giver
				                                                        </div>
				                                                    </td>
				                                                    <td>
				                                                       <input class="visibilityCheckbox answerCheckbox centered"
				                                                              name="receiverLeaderCheckbox"
				                                                              type="checkbox"
				                                                              value="<%= FeedbackParticipantType.GIVER %>"
				                                                              <c:if test="${ifrc.responseVisibleToGiver[question]}">checked="checked"</c:if>>
				                                                    </td>
				                                                    <td>
				                                                       <input class="visibilityCheckbox giverCheckbox"
				                                                              type="checkbox"
				                                                              value="<%= FeedbackParticipantType.GIVER %>"
				                                                              <c:if test="${ifrc.responseVisibleToGiver[question]}">checked="checked"</c:if>>
				                                                    </td>
				                                                </tr>
				                                                <c:if test="${ifrc.responseVisibleToRecipient[question]}">
					                                                <tr id="response-recipient-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}">
					                                                    <td class="text-left">
					                                                        <div data-toggle="tooltip"
					                                                             data-placement="top"
					                                                             title=""
					                                                             data-original-title="Control what response recipient(s) can view">
					                                                            Response
					                                                            Recipient(s)
					                                                        </div>
					                                                    </td>
					                                                    <td>
                                                                            <input class="visibilityCheckbox answerCheckbox centered"
                                                                                   name="receiverLeaderCheckbox"
                                                                                   type="checkbox"
                                                                                   value="<%= FeedbackParticipantType.RECEIVER %>"
                                                                                   <c:if test="${ifrc.responseVisibleToRecipient[question]}">checked="checked"</c:if>>
					                                                    </td>
					                                                    <td>
                                                                            <input class="visibilityCheckbox giverCheckbox"
                                                                                   type="checkbox"
                                                                                   value="<%= FeedbackParticipantType.RECEIVER %>"
                                                                                   <c:if test="${ifrc.responseVisibleToRecipient[question]}">checked="checked"</c:if>>
					                                                    </td>
					                                                </tr>
				                                                </c:if>
                                                                <c:if test="${ifrc.responseVisibleToGiverTeam[question]}">
					                                                <tr id="response-giver-team-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}">
					                                                    <td class="text-left">
					                                                        <div data-toggle="tooltip"
					                                                             data-placement="top"
					                                                             title=""
					                                                             data-original-title="Control what team members of response giver can view">
					                                                            Response
					                                                            Giver's Team
					                                                            Members
					                                                        </div>
					                                                    </td>
					                                                    <td>
                                                                            <input class="visibilityCheckbox answerCheckbox"
                                                                                   type="checkbox"
                                                                                   value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>"
                                                                                   <c:if test="${ifrc.responseVisibleToGiverTeam[question]}">checked="checked"</c:if>>
					                                                    </td>
					                                                    <td>
                                                                            <input class="visibilityCheckbox giverCheckbox"
                                                                                   type="checkbox"
                                                                                   value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>"
                                                                                   <c:if test="${ifrc.responseVisibleToGiverTeam[question]}">checked="checked"</c:if>>
					                                                    </td>
					                                                </tr>
					                                            </c:if>
                                                                <c:if test="${ifrc.responseVisibleToRecipientTeam[question]}">
					                                                <tr id="response-recipient-team-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}">
					                                                    <td class="text-left">
					                                                        <div data-toggle="tooltip"
					                                                             data-placement="top"
					                                                             title=""
					                                                             data-original-title="Control what team members of response recipient(s) can view">
					                                                            Response
					                                                            Recipient's
					                                                            Team Members
					                                                        </div>
					                                                    </td>
					                                                    <td>
                                                                            <input class="visibilityCheckbox answerCheckbox"
                                                                                   type="checkbox"
                                                                                   value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>"
                                                                                   <c:if test="${ifrc.responseVisibleToRecipientTeam[question]}">checked="checked"</c:if>>
					                                                    </td>
					                                                    <td>
                                                                            <input class="visibilityCheckbox giverCheckbox"
                                                                                   type="checkbox"
                                                                                   value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>"
                                                                                   <c:if test="${ifrc.responseVisibleToRecipientTeam[question]}">checked="checked"</c:if>>
					                                                    </td>
					                                                </tr>
                                                                </c:if>
                                                                <c:if test="${ifrc.responseVisibleToStudents[question]}">
					                                                <tr id="response-students-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}">
					                                                    <td class="text-left">
					                                                        <div data-toggle="tooltip"
					                                                             data-placement="top"
					                                                             title=""
					                                                             data-original-title="Control what other students in this course can view">
					                                                            Other
					                                                            students in
					                                                            this course
					                                                         </div>
					                                                    </td>
					                                                    <td>
                                                                            <input class="visibilityCheckbox answerCheckbox"
                                                                                   type="checkbox"
                                                                                   value="<%= FeedbackParticipantType.STUDENTS %>"
                                                                                   <c:if test="${ifrc.responseVisibleToStudents[question]}">checked="checked"</c:if>>
					                                                    </td>
					                                                    <td>
                                                                            <input class="visibilityCheckbox giverCheckbox"
                                                                                   type="checkbox"
                                                                                   value="<%= FeedbackParticipantType.STUDENTS %>"
                                                                                   <c:if test="${ifrc.responseVisibleToStudents[question]}">checked="checked"</c:if>>
					                                                    </td>
					                                                </tr>
				                                                </c:if>
                                                                <c:if test="${ifrc.responseVisibleToInstructors[question]}">
					                                                <tr id="response-instructors-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}">
					                                                    <td class="text-left">
					                                                        <div data-toggle="tooltip"
					                                                             data-placement="top"
					                                                             title=""
					                                                             data-original-title="Control what instructors can view">
					                                                            Instructors
					                                                        </div>
					                                                    </td>
					                                                    <td>
                                                                            <input class="visibilityCheckbox answerCheckbox"
                                                                                   type="checkbox"
                                                                                   value="<%= FeedbackParticipantType.INSTRUCTORS %>"
                                                                                   <c:if test="${ifrc.responseVisibleToInstructors[question]}">checked="checked"</c:if>>
					                                                    </td>
					                                                    <td>
                                                                            <input class="visibilityCheckbox giverCheckbox"
                                                                                   type="checkbox"
                                                                                   value="<%= FeedbackParticipantType.INSTRUCTORS %>"
                                                                                   <c:if test="${ifrc.responseVisibleToInstructors[question]}">checked="checked"</c:if>>
					                                                    </td>
					                                                </tr>
					                                            </c:if>
				                                            </tbody>
				                                        </table>
				                                    </div>
				                                    <textarea class="form-control"
				                                              rows="3"
				                                              placeholder="Your comment about this response"
				                                              name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %>"
				                                              id="responseCommentAddForm-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}">
				                                    </textarea>
				                                </div>
				                                <div class="col-sm-offset-5">
				                                    <a href="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD %>"
				                                       class="btn btn-primary"
				                                       id="button_save_comment_for_add-${fsIndex}-${responseEntriesStatus.count}-${responseEntryStatus.count}">
				                                        Save
				                                    </a>
				                                    <input type="button"
				                                           class="btn btn-default"
				                                           value="Cancel"
				                                           onclick="hideResponseCommentAddForm(${fsIndex},${responseEntriesStatus.count},${responseEntryStatus.count})">
				                                    <input type="hidden"
				                                           name="<%= Const.ParamsNames.COURSE_ID %>"
				                                           value="${question.courseId}">
				                                    <input type="hidden"
				                                           name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
				                                           value="${question.feedbackSessionName}">
				                                    <input type="hidden"
				                                           name="<%= Const.ParamsNames.FEEDBACK_QUESTION_ID %>"
				                                           value="${question.feedbackQuestionId}">
				                                    <input type="hidden"
				                                           name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>"
				                                           value="${question.id}">
				                                    <input type="hidden"
				                                           name="<%= Const.ParamsNames.USER_ID %>"
				                                           value="${data.account.googleId}">
				                                    <input type="hidden"
				                                           name="<%= Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO %>"
				                                           value="${ifrc.showResponseCommentToStrings[question]}">
				                                    <input type="hidden"
				                                           name="<%= Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO %>"
				                                           value="${ifrc.showResponseGiverNameToStrings[question]}">
				                                </div>
				                            </form>
				                        </li>
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
