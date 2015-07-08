<%@ tag description="Feedback Response Comment" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>
<%@ attribute name="frc" type="teammates.ui.template.FeedbackResponseComment" required="true" %>
<%@ attribute name="firstIndex" %>
<%@ attribute name="secondIndex" %>
<%@ attribute name="thirdIndex" %>
<%@ attribute name="frcIndex" %>
<c:choose>
    <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex && not empty frcIndex}">
        <c:set var="divId" value="${firstIndex}-${secondIndex}-${thirdIndex}-${frcIndex}" />
        <c:set var="divIdAsJsParams" value="${firstIndex},${secondIndex},${thirdIndex},${frcIndex}" />
    </c:when>
    <c:otherwise>
        <c:set var="divId" value="${frc.commentId}" />
        <c:set var="divIdAsJsParams" value="" />
    </c:otherwise>
</c:choose>
<li class="list-group-item list-group-item-warning${frc.extraClass}" id="responseCommentRow-${divId}">
    <div id="commentBar-${divId}">
        <span class="text-muted">
            From: ${frc.giverDisplay} [${frc.createdAt}] ${frc.editedAt}
        </span>
        <c:if test="${frc.withVisibilityIcon}">
            <span class="glyphicon glyphicon-eye-open"
                  data-toggle="tooltip"
                  data-placement="top"
                  style="margin-left: 5px;"
                  title="This response comment is visible to ${frc.whoCanSeeComment}"></span>
        </c:if>
        <c:if test="${frc.withNotificationIcon}">
            <span class="glyphicon glyphicon-bell"
                  data-toggle="tooltip"
                  data-placement="top"
                  title="This comment is pending to notify recipients"></span>
        </c:if>
        <c:if test="${frc.withLinkToCommentsPage}">
            <a type="button"
               href="${frc.linkToCommentsPage}"
               target="_blank"
               class="btn btn-default btn-xs icon-button pull-right"
               data-toggle="tooltip"
               data-placement="top"
               title="Edit comment in the Comments page"
               style="display:none;">
                <span class="glyphicon glyphicon-new-window glyphicon-primary"></span>
            </a>
        </c:if>
        <c:if test="${frc.editDeleteEnabled}">
            <form class="responseCommentDeleteForm pull-right">
                <a href="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE %>"
                   type="button"
                   id="commentdelete-${divId}"
                   class="btn btn-default btn-xs icon-button"
                   data-toggle="tooltip"
                   data-placement="top"
                   title="<%= Const.Tooltips.COMMENT_DELETE %>"
                   <c:if test="${frc.editDeleteEnabledOnlyOnHover}">style="display: none;"</c:if>
                   <c:if test="${not frc.instructorAllowedToDelete}">disabled="disabled"</c:if>>
                    <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
                </a>
                <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="${frc.feedbackResponseId}">
                <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="${frc.commentId}">
                <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${frc.courseId}">
                <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${frc.feedbackSessionName}">
                <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${frc.googleId}">
            </form>
            <a type="button"
               id="commentedit-${divId}"
               class="btn btn-default btn-xs icon-button pull-right"
               onclick="showResponseCommentEditForm(${divIdAsJsParams})"
               data-toggle="tooltip"
               data-placement="top"
               title="<%= Const.Tooltips.COMMENT_EDIT %>"
               <c:if test="${frc.editDeleteEnabledOnlyOnHover}">style="display: none;"</c:if>
               <c:if test="${not frc.instructorAllowedToEdit}">disabled="disabled"</c:if>>
                <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
            </a>
        </c:if>
    </div>
    <!-- do not add whitespace between the opening and closing tags -->
    <div id="plainCommentText-${divId}" style="margin-left: 15px;">${frc.commentText}</div>
    <c:if test="${frc.editDeleteEnabled}">
        <form style="display: none;" id="responseCommentEditForm-${divId}" class="responseCommentEditForm">
            <div class="form-group form-inline">
                <div class="form-group text-muted">
                    <p>
                        Giver: ${frc.responseGiverName}<br>
                        Recipient: ${frc.responseRecipientName}
                    </p>
                    You may change comment's visibility using the visibility options on the right hand side.
                </div>
                <a id="frComment-visibility-options-trigger-${divId}"
                   class="btn btn-sm btn-info pull-right"
                   onclick="toggleVisibilityEditForm(${divIdAsJsParams})">
                    <span class="glyphicon glyphicon-eye-close"></span>
                    Show Visibility Options
                </a>
            </div>
            <div id="visibility-options-${divId}" class="panel panel-default" style="display: none;">
                <div class="panel-heading">
                    Visibility Options
                </div>
                <table class="table text-center" style="color: #000;">
                    <tbody>
                        <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see your comment</th>
                            <th class="text-center">Can see your name</th>
                        </tr>
                        <tr id="response-giver-${divId}">
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
                                       <c:if test="${frc.showCommentToResponseGiver}">checked="checked"</c:if>>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox"
                                       type="checkbox"
                                       value="<%= FeedbackParticipantType.GIVER %>"
                                       <c:if test="${frc.showGiverNameToResponseGiver}">checked="checked"</c:if>>
                            </td>
                        </tr>
                        <c:if test="${frc.responseVisibleToRecipient}">
		                    <tr id="response-recipient-${divId}">
		                        <td class="text-left">
		                            <div data-toggle="tooltip"
		                                 data-placement="top"
		                                 title=""
		                                 data-original-title="Control what response recipient(s) can view">
		                                Response Recipient(s)
		                            </div>
		                        </td>
		                        <td>
		                            <input class="visibilityCheckbox answerCheckbox centered"
		                                   name="receiverLeaderCheckbox"
		                                   type="checkbox"
		                                   value="<%= FeedbackParticipantType.RECEIVER %>"
		                                   <c:if test="${frc.showCommentToResponseRecipient}">checked="checked"</c:if>>
		                        </td>
		                        <td>
		                            <input class="visibilityCheckbox giverCheckbox"
		                                   type="checkbox"
		                                   value=<%= FeedbackParticipantType.RECEIVER %>"
		                                   <c:if test="${frc.showGiverNameToResponseRecipient}">checked="checked"</c:if>>
		                        </td>
		                    </tr>
		                </c:if>
                        <c:if test="${frc.responseVisibleToGiverTeam}">
	                        <tr id="response-giver-team-${divId}">
	                            <td class="text-left">
	                                <div data-toggle="tooltip"
	                                     data-placement="top"
	                                     title=""
	                                     data-original-title="Control what team members of response giver can view">
	                                    Response Giver's Team Members
	                                </div>
	                            </td>
	                            <td>
	                                <input class="visibilityCheckbox answerCheckbox"
	                                       type="checkbox"
	                                       value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>"
	                                       <c:if test="${frc.showCommentToResponseGiverTeam}">checked="checked"</c:if>>
	                            </td>
	                            <td>
	                                <input class="visibilityCheckbox giverCheckbox"
	                                       type="checkbox"
	                                       value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>"
	                                       <c:if test="${frc.showGiverNameToResponseGiverTeam}">checked="checked"</c:if>>
	                            </td>
	                        </tr>
	                    </c:if>
                        <c:if test="${frc.responseVisibleToRecipientTeam}">
	                        <tr id="response-recipient-team-${divId}">
	                            <td class="text-left">
	                                <div data-toggle="tooltip"
	                                     data-placement="top"
	                                     title=""
	                                     data-original-title="Control what team members of response recipient(s) can view">
	                                    Response Recipient's Team Members
	                                </div>
	                            </td>
	                            <td>
	                                <input class="visibilityCheckbox answerCheckbox"
	                                       type="checkbox"
	                                       value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>"
	                                       <c:if test="${frc.showCommentToResponseRecipientTeam}">checked="checked"</c:if>>
	                            </td>
	                            <td>
	                                <input class="visibilityCheckbox giverCheckbox"
	                                       type="checkbox"
	                                       value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>"
	                                       <c:if test="${frc.showGiverNameToResponseRecipientTeam}">checked="checked"</c:if>>
	                            </td>
	                        </tr>
	                    </c:if>
                        <c:if test="${frc.responseVisibleToStudents}">
	                        <tr id="response-students-${divId}">
	                            <td class="text-left">
	                                <div data-toggle="tooltip"
	                                     data-placement="top"
	                                     title=""
	                                     data-original-title="Control what other students in this course can view">
	                                    Other students in this course
	                                </div>
	                            </td>
	                            <td>
	                                <input class="visibilityCheckbox answerCheckbox"
	                                       type="checkbox"
	                                       value="<%= FeedbackParticipantType.STUDENTS %>"
	                                       <c:if test="${frc.showCommentToStudents}">checked="checked"</c:if>>
	                            </td>
	                            <td>
	                                <input class="visibilityCheckbox giverCheckbox"
	                                       type="checkbox"
	                                       value="<%= FeedbackParticipantType.STUDENTS %>"
	                                       <c:if test="${frc.showGiverNameToStudents}">checked="checked"</c:if>>
	                            </td>
	                        </tr>
	                    </c:if>
                        <c:if test="${frc.responseVisibleToInstructors}">
	                        <tr id="response-instructors-${divId}">
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
	                                       <c:if test="${frc.showCommentToInstructors}">checked="checked"</c:if>>
	                            </td>
	                            <td>
	                                <input class="visibilityCheckbox giverCheckbox"
	                                       type="checkbox"
	                                       value="<%= FeedbackParticipantType.INSTRUCTORS %>"
	                                       <c:if test="${frc.showGiverNameToInstructors}">checked="checked"</c:if>>
	                            </td>
	                        </tr>
	                    </c:if>
                    </tbody>
                </table>
            </div>
            <div class="form-group">
                <textarea class="form-control"
                          rows="3"
                          placeholder="Your comment about this response"
                          name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %>"
                          id="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %>-${divId}">${frc.commentText}</textarea>
            </div>
            <div class="col-sm-offset-5">
                <a href="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT %>"
                   type="button"
                   class="btn btn-primary"
                   id="button_save_comment_for_edit-${divId}">
                    Save
                </a>
                <input type="button"
                       class="btn btn-default"
                       value="Cancel"
                       onclick="return hideResponseCommentEditForm(${divIdAsJsParams});">
            </div>
            <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="${frc.feedbackResponseId}">
            <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="${frc.commentId}">
            <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${frc.courseId}">
            <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${frc.feedbackSessionName}">
            <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${frc.googleId}">
            <input type="hidden" name="<%= Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO %>" value="${frc.showCommentToString}">
            <input type="hidden" name="<%= Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO %>" value="${frc.showGiverNameToString}">
        </form>
    </c:if>
</li>
