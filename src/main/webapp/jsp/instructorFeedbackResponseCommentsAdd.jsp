<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType" %>
<%@ page import="teammates.ui.controller.InstructorFeedbackResponseCommentAjaxPageData"%>
<%
    InstructorFeedbackResponseCommentAjaxPageData data = (InstructorFeedbackResponseCommentAjaxPageData) request.getAttribute("data");
%>
<li class="list-group-item list-group-item-warning"
    id="responseCommentRow-<%= data.commentId %>">
    <div id="commentBar-<%= data.commentId %>">
        <span class="text-muted">
            From: <%= data.comment.giverEmail %> [<%= data.comment.createdAt %>]
        </span>
        <form class="responseCommentDeleteForm pull-right">
            <a href="/page/instructorFeedbackResponseCommentDelete"
               type="button"
               id="commentdelete-<%= data.comment.getId() %>"
               class="btn btn-default btn-xs icon-button"
               data-toggle="tooltip"
               data-placement="top"
               title="Delete this comment">
                <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
            </a>
            <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="<%= data.comment.feedbackResponseId %>">
            <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="<%= data.comment.getId() %>">
            <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="<%= data.comment.courseId %>">
            <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%= data.comment.feedbackSessionName %>">
            <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="<%= data.account.googleId %>">
        </form>
        <a type="button"
           id="commentedit-<%= data.commentId %>"
           class="btn btn-default btn-xs icon-button pull-right"
           onclick="showResponseCommentEditForm(<%= data.commentId.replaceAll("-", ", ") %>)"
           data-toggle="tooltip"
           data-placement="top"
           title="Edit this comment">
            <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
        </a>
    </div>
    <!-- do not add whitespace between the opening and closing tags -->
    <div id="plainCommentText-<%= data.commentId %>"><%= data.comment.commentText.getValue() %></div>
    <form style="display: none;" id="responseCommentEditForm-<%= data.commentId %>" class="responseCommentEditForm">
        <div class="form-group form-inline">
            <div class="form-group text-muted">
                <p>
                    Giver: <%= data.giverName %><br>
                    Recipient: <%= data.recipientName %>
                </p>
                You may change comment's visibility using the visibility options on the right hand side.
            </div>
            <a id="frComment-visibility-options-trigger-<%= data.commentId %>"
               class="btn btn-sm btn-info pull-right"
               onclick="toggleVisibilityEditForm(<%= data.commentId.replaceAll("-", ", ") %>)">
                <span class="glyphicon glyphicon-eye-close"></span>
                Show Visibility Options
            </a>
        </div>
        <div id="visibility-options-<%= data.commentId %>" class="panel panel-default" style="display: none;">
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
                    <tr id="response-giver-<%= data.commentId %>">
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
                                   value="GIVER"
                                   <%= data.comment.showCommentTo.indexOf(FeedbackParticipantType.GIVER) == -1 ? "" : "checked=\"checked\"" %>>
                        </td>
                        <td>
                            <input class="visibilityCheckbox giverCheckbox"
                                   type="checkbox"
                                   value="GIVER"
                                   <%= data.comment.showGiverNameTo.indexOf(FeedbackParticipantType.GIVER) == -1 ? "" : "checked=\"checked\"" %>>
                        </td>
                    </tr>
                    <tr id="response-recipient-<%= data.commentId %>">
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
                                   value="RECEIVER"
                                   <%= data.comment.showCommentTo.indexOf(FeedbackParticipantType.RECEIVER) == -1 ? "" : "checked=\"checked\"" %>>
                        </td>
                        <td>
                            <input class="visibilityCheckbox giverCheckbox"
                                   type="checkbox"
                                   value="RECEIVER"
                                   <%= data.comment.showGiverNameTo.indexOf(FeedbackParticipantType.RECEIVER) == -1 ? "" : "checked=\"checked\"" %>>
                        </td>
                    </tr>
                    <tr id="response-giver-team-<%= data.commentId %>">
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
                                   value="OWN_TEAM_MEMBERS"
                                   <%= data.comment.showCommentTo.indexOf(FeedbackParticipantType.OWN_TEAM_MEMBERS) == -1 ? "" : "checked=\"checked\"" %>>
                        </td>
                        <td>
                            <input class="visibilityCheckbox giverCheckbox"
                                   type="checkbox"
                                   value="OWN_TEAM_MEMBERS"
                                   <%= data.comment.showGiverNameTo.indexOf(FeedbackParticipantType.OWN_TEAM_MEMBERS) == -1 ? "" : "checked=\"checked\"" %>>
                        </td>
                    </tr>
                    <tr id="response-recipient-team-<%= data.commentId %>">
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
                                   value="RECEIVER_TEAM_MEMBERS"
                                   <%= data.comment.showCommentTo.indexOf(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS) == -1 ? "" : "checked=\"checked\"" %>>
                        </td>
                        <td>
                            <input class="visibilityCheckbox giverCheckbox"
                                   type="checkbox"
                                   value="RECEIVER_TEAM_MEMBERS"
                                   <%= data.comment.showGiverNameTo.indexOf(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS) == -1 ? "" : "checked=\"checked\"" %>>
                        </td>
                    </tr>
                    <tr id="response-students-<%= data.commentId %>">
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
                                   value="STUDENTS"
                                   <%= data.comment.showCommentTo.indexOf(FeedbackParticipantType.STUDENTS) == -1 ? "" : "checked=\"checked\"" %>>
                        </td>
                        <td>
                            <input class="visibilityCheckbox giverCheckbox"
                                   type="checkbox"
                                   value="STUDENTS"
                                   <%= data.comment.showGiverNameTo.indexOf(FeedbackParticipantType.STUDENTS) == -1 ? "" : "checked=\"checked\"" %>>
                        </td>
                    </tr>
                    <tr id="response-instructors-<%= data.commentId %>">
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
                                   value="INSTRUCTORS"
                                   <%= data.comment.showCommentTo.indexOf(FeedbackParticipantType.INSTRUCTORS) == -1 ? "" : "checked=\"checked\"" %>>
                        </td>
                        <td>
                            <input class="visibilityCheckbox giverCheckbox"
                                   type="checkbox"
                                   value="INSTRUCTORS"
                                   <%= data.comment.showGiverNameTo.indexOf(FeedbackParticipantType.INSTRUCTORS) == -1 ? "" : "checked=\"checked\"" %>>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="form-group">
            <textarea class="form-control"
                      rows="3"
                      placeholder="Your comment about this response"
                      name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %>"
                      id="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %>-<%= data.commentId %>">
                <%= data.comment.commentText.getValue() %>
            </textarea>
        </div>
        <div class="col-sm-offset-5">
            <a href="/page/instructorFeedbackResponseCommentEdit"
               type="button"
               class="btn btn-primary"
               id="button_save_comment_for_edit-<%= data.commentId %>">
                Save
            </a>
            <input type="button"
                   class="btn btn-default"
                   value="Cancel"
                   onclick="return hideResponseCommentEditForm(<%= data.commentId.replaceAll("-", ", ") %>);">
        </div>
        <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="<%= data.comment.feedbackResponseId %>">
        <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="<%= data.comment.getId() %>">
        <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="<%= data.comment.courseId %>">
        <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%= data.comment.feedbackSessionName %>">
        <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="<%= data.account.googleId %>">
        <input type="hidden" name="<%= Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO %>" value="<%= data.showCommentToString %>">
        <input type="hidden" name="<%= Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO %>" value="<%= data.showGiverNameToString %>">
    </form>
</li>