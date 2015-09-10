<%@ tag description="Feedback Response Comment Form With Visibility Options" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>
<%@ attribute name="frc" type="teammates.ui.template.FeedbackResponseComment" required="true" %>
<%@ attribute name="divId" required="true" %>
<%@ attribute name="divIdAsJsParams" required="true" %>
<%@ attribute name="formType" required="true" %>
<%@ attribute name="textAreaId" required="true" %>
<%@ attribute name="submitLink" required="true" %>
<%@ attribute name="buttonText" required="true" %>
<c:set var="isEditForm" value="${formType eq 'Edit'}" />
<c:set var="isAddForm" value="${formType eq 'Add'}" />
<form class="responseComment${formType}Form"<c:if test="${isEditForm}"> style="display: none;" id="responseCommentEditForm-${divId}"</c:if>>
    <div class="form-group form-inline">
        <div class="form-group text-muted">
            <p>
                Giver: ${frc.responseGiverName}
                <br>
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
                             title="Control what response giver can view">
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
                                 title="Control what response recipient(s) can view">
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
                                   value="<%= FeedbackParticipantType.RECEIVER %>"
                                   <c:if test="${frc.showGiverNameToResponseRecipient}">checked="checked"</c:if>>
                        </td>
                    </tr>
                </c:if>
                <c:if test="${frc.responseVisibleToGiverTeam}">
                    <tr id="response-giver-team-${divId}">
                        <td class="text-left">
                            <div data-toggle="tooltip"
                                 data-placement="top"
                                 title="Control what team members of response giver can view">
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
                                 title="Control what team members of response recipient(s) can view">
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
                                 title="Control what other students in this course can view">
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
                                 title="Control what instructors can view">
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
                  id="${textAreaId}-${divId}">${frc.commentText}</textarea>
    </div>
    <div class="col-sm-offset-5">
        <a href="${submitLink}"
           type="button"
           class="btn btn-primary"
           id="button_save_comment_for_${fn:toLowerCase(formType)}-${divId}">
            ${buttonText}
        </a>
        <input type="button"
               class="btn btn-default"
               value="Cancel"
               onclick="return hideResponseComment${formType}Form(${divIdAsJsParams});">
    </div>
    <c:if test="${isEditForm}"><input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="${frc.commentId}"></c:if>
    <c:if test="${isAddForm}"><input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_ID %>" value="${frc.questionId}"></c:if>
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="${frc.feedbackResponseId}">
    <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${frc.courseId}">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${frc.feedbackSessionName}">
    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
    <input type="hidden" name="<%= Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO %>" value="${frc.showCommentToString}">
    <input type="hidden" name="<%= Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO %>" value="${frc.showGiverNameToString}">
</form>