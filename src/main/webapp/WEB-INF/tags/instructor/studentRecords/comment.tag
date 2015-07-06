<%@ tag description="instructorStudentRecords - Individual comments" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.CommentParticipantType" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="comment" type="teammates.ui.template.InstructorStudentRecordsComment" required="true" %>
<%@ attribute name="index" required="true" %>
<li class="list-group-item list-group-item-warning">
    <form method="post"
          action="<%= Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_EDIT %>"
          name="form_commentedit"
          class="form_comment"
          id="form_commentedit-${index}">
        <div id="commentBar${index}">
            <span class="text-muted">
                ${comment.commentCreatedAt}
            </span>
            <a type="button"
               id="commentdelete-${index}"
               class="btn btn-default btn-xs icon-button pull-right"
               onclick="return deleteComment('${index}');"
               data-toggle="tooltip"
               data-placement="top"
               title="<%=Const.Tooltips.COMMENT_DELETE%>"> 
                <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
            </a>
            <a type="button"
               id="commentedit-${index}"
               class="btn btn-default btn-xs icon-button pull-right"
               onclick="return enableEdit('${index}', '${comment.numOfComments}');"
               data-toggle="tooltip"
               data-placement="top"
               title="<%= Const.Tooltips.COMMENT_EDIT %>">
                <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
            </a>
            <c:if test="${not empty comment.typeOfPeopleCanViewComment}">
                <span class="glyphicon glyphicon-eye-open"
                      data-toggle="tooltip"
                      style="margin-left: 5px;"
                      data-placement="top"
                      title="This comment is public to ${comment.typeOfPeopleCanViewComment}"></span>
            </c:if>
        </div>
        <div id="plainCommentText${index}">
            ${comment.commentText}
        </div>
        <div id="commentTextEdit${index}" style="display: none;">
            <div class="form-group form-inline">
                <div class="form-group text-muted">
                    <p>
                        Comment about ${comment.studentName}:
                    </p>
                    You may change comment's visibility using the visibility options on the right hand side.
                </div>
                <a id="visibility-options-trigger${index}" class="btn btn-sm btn-info pull-right">
                    <span class="glyphicon glyphicon-eye-close"></span>
                    Show Visibility Options
                </a>
            </div>
            <div id="visibility-options${index}" class="panel panel-default" style="display: none;">
                <div class="panel-heading">
                    Visibility Options
                </div>
                <table class="table text-center" style="color: #000;">
                    <tbody>
                        <tr>
                            <th class="text-center">
                                User/Group
                            </th>
                            <th class="text-center">
                                Can see your comment
                            </th>
                            <th class="text-center">
                                Can see giver's name
                            </th>
                            <th class="text-center">
                                Can see recipient's name
                            </th>
                        </tr>
                        <tr id="recipient-person${index}">
                            <td class="text-left">
                                <div data-toggle="tooltip"
                                     data-placement="top"
                                     title
                                     data-original-title="Control what comment recipient(s) can view">
                                    Recipient(s)
                                </div>
                            </td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox centered"
                                       name="receiverLeaderCheckbox"
                                       type="checkbox"
                                       value="<%= CommentParticipantType.PERSON %>"
                                       ${comment.checkIfShowCommentToRecipient}>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox"
                                       type="checkbox"
                                       value="<%= CommentParticipantType.PERSON %>"
                                       ${comment.checkIfShowGiverNameToRecipient}>
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox"
                                       name="receiverFollowerCheckbox"
                                       type="checkbox"
                                       value="<%= CommentParticipantType.PERSON %>"
                                       disabled="disabled">
                            </td>
                        </tr>
                        <tr id="recipient-team${index}">
                            <td class="text-left">
                                <div data-toggle="tooltip"
                                     data-placement="top"
                                     title=""
                                     data-original-title="Control what team members of comment recipients can view">
                                    Recipient's Team
                                </div>
                            </td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox"
                                       type="checkbox"
                                       value="<%= CommentParticipantType.TEAM %>"
                                       ${comment.checkIfShowCommentToTeam}>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox"
                                       type="checkbox"
                                       value="<%= CommentParticipantType.TEAM %>"
                                       ${comment.checkIfShowGiverNameToTeam}>
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox"
                                       type="checkbox"
                                       value="<%= CommentParticipantType.TEAM %>"
                                       ${comment.checkIfShowRecipientNameToTeam}>
                            </td>
                        </tr>
                        <c:if test="${not empty comment.checkIfShowCommentToSection}">
                            <tr id="recipient-section${index}">
                                <td class="text-left">
                                    <div data-toggle="tooltip"
                                         data-placement="top"
                                         title=""
                                         data-original-title="Control what other students in the same section can view">
                                        Recipient's Section
                                    </div>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox answerCheckbox"
                                           type="checkbox"
                                           value="<%= CommentParticipantType.SECTION %>"
                                           ${comment.checkIfShowCommentToSection}>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox giverCheckbox"
                                           type="checkbox"
                                           value="<%= CommentParticipantType.SECTION %>"
                                           ${comment.checkIfShowGiverNameToSection}>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox recipientCheckbox"
                                           type="checkbox"
                                           value="<%= CommentParticipantType.SECTION %>"
                                           ${comment.checkIfShowRecipientNameToSection}>
                                </td>
                            </tr>
                        </c:if>
                        <tr id="recipient-course${index}">
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
                                       value="<%= CommentParticipantType.COURSE %>"
                                       ${comment.checkIfShowCommentToCourse}>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox"
                                       type="checkbox"
                                       value="<%= CommentParticipantType.COURSE %>"
                                       ${comment.checkIfShowGiverNameToCourse}>
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox"
                                       type="checkbox"
                                       value="<%= CommentParticipantType.COURSE %>"
                                       ${comment.checkIfShowRecipientNameToCourse}>
                            </td>
                        </tr>
                        <tr>
                            <td class="text-left">
                                <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what instructors can view">
                                    Instructors
                                </div>
                            </td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox"
                                       type="checkbox"
                                       value="<%= CommentParticipantType.INSTRUCTOR %>"
                                       ${comment.checkIfShowCommentToInstructor}>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox"
                                       type="checkbox"
                                       value="<%= CommentParticipantType.INSTRUCTOR %>"
                                       ${comment.checkIfShowGiverNameToInstructor}>
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox"
                                       type="checkbox"
                                       value="<%= CommentParticipantType.INSTRUCTOR %>"
                                       ${comment.checkIfShowRecipientNameToInstructor}>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="form-group">
                <!-- Do not add whitespace between the opening and closing tags -->
                <textarea class="form-control"
                          rows="3"
                          placeholder="Your comment about this student"
                          name="<%= Const.ParamsNames.COMMENT_TEXT %>"
                          id="commentText${index}">${comment.commentText}</textarea>
            </div>
            <div class="col-sm-offset-5">
                <input id="commentsave-${index}"
                       title="Save comment"
                       onclick="return submitCommentForm('${index}');"
                       type="submit"
                       class="btn btn-primary"
                       id="button_save_comment"
                       value="Save">
                <input type="button"
                       class="btn btn-default"
                       value="Cancel"
                       onclick="return disableComment('${index}');">
            </div>
        </div>
        <input type="hidden" name="<%= Const.ParamsNames.COMMENT_EDITTYPE %>" id="<%= Const.ParamsNames.COMMENT_EDITTYPE %>-${index}" value="edit">
        <input type="hidden" name="<%= Const.ParamsNames.COMMENT_ID %>" value="${comment.commentId}">
        <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${comment.courseId}">
        <input type="hidden" name="<%= Const.ParamsNames.STUDENT_EMAIL %>" value="${comment.studentEmail}">
        <input type="hidden" name="<%= Const.ParamsNames.RECIPIENT_TYPE %>" value="${comment.recipientType}">
        <input type="hidden" name="<%= Const.ParamsNames.RECIPIENTS %>" value="${comment.recipientsString}">
        <input type="hidden" name="<%= Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO %>" value="${comment.showCommentToString}">
        <input type="hidden" name="<%= Const.ParamsNames.COMMENTS_SHOWGIVERTO %>" value="${comment.showGiverNameToString}">
        <input type="hidden" name="<%= Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO %>" value="${comment.showRecipientNameToString}">
        <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${comment.googleId}">
    </form>
</li>