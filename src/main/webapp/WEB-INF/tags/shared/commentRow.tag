<%@ tag description="Student Comment" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.CommentParticipantType" %>
<%@ attribute name="comment" type="teammates.ui.template.CommentRow" required="true" %>
<%@ attribute name="commentIndex" required="true" %>
<li class="list-group-item list-group-item-warning${comment.extraClass}">
    <div id="commentBar-${commentIndex}">
        <span class="text-muted">
            To <b>${fn:escapeXml(comment.recipientDisplay)}</b> [${comment.createdAt}] ${comment.editedAt}
        </span>
        <c:if test="${comment.withVisibilityIcon}">
            <span class="glyphicon glyphicon-eye-open"
                  data-toggle="tooltip"
                  data-placement="top"
                  style="margin-left: 5px;"
                  title="This comment is visible to ${comment.whoCanSeeComment}"></span>
        </c:if>
        <c:if test="${comment.withNotificationIcon}">
            <span class="glyphicon glyphicon-bell"
                  data-toggle="tooltip"
                  data-placement="top"
                  title="This comment is pending notification. i.e., you have not sent a notification about this comment yet"></span>
        </c:if>
        <c:if test="${comment.withLinkToCommentsPage}">
            <a type="button"
               href="${comment.linkToCommentsPage}"
               target="_blank" rel="noopener noreferrer"
               class="btn btn-default btn-xs icon-button pull-right"
               data-toggle="tooltip"
               data-placement="top"
               title="<%= Const.Tooltips.COMMENT_EDIT_IN_COMMENTS_PAGE %>"
               style="display:none;">
                <span class="glyphicon glyphicon-new-window glyphicon-primary"></span>
            </a>
        </c:if>
        <c:if test="${comment.editDeleteEnabled}">
            <a type="button"
               id="commentdelete-${commentIndex}"
               class="btn btn-default btn-xs icon-button pull-right"
               onclick="return deleteComment('${commentIndex}');"
               data-toggle="tooltip"
               data-placement="top"
               title="<%= Const.Tooltips.COMMENT_DELETE %>"
               <c:if test="${comment.editDeleteEnabledOnlyOnHover}">style="display: none;"</c:if>> 
                <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
            </a>
            <a type="button"
               id="commentedit-${commentIndex}"
               class="btn btn-default btn-xs icon-button pull-right"
               onclick="return enableEdit(${commentIndex}, ${comment.numComments});"
               data-toggle="tooltip"
               data-placement="top"
               title="<%= Const.Tooltips.COMMENT_EDIT %>"
               <c:if test="${comment.editDeleteEnabledOnlyOnHover}">style="display: none;"</c:if>>
                <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
            </a>
        </c:if>
    </div>
    <%-- do not add whitespace between the opening and closing tags --%>
    <div id="plainCommentText${commentIndex}" style="margin-left: 15px;">${comment.commentText}</div>
    <c:if test="${comment.editDeleteEnabled}">
        <form method="post"
              action="<%= Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_EDIT %>"
              name="form_commentedit"
              class="form_comment"
              id="form_commentedit-${commentIndex}">
            <div id="commentTextEdit${commentIndex}" style="display: none;">
                <div class="form-group form-inline">
                    <div class="form-group text-muted">
                    <p>
                        Comment about ${fn:escapeXml(comment.recipientDisplay)}:
                    </p>
                    You may change comment's visibility using the visibility options on the right hand side.
                    </div>
                    <a id="visibility-options-trigger${commentIndex}" class="btn btn-sm btn-info pull-right">
                        <span class="glyphicon glyphicon-eye-close"></span>
                        Show Visibility Options
                    </a>
                </div>
                <div id="visibility-options${commentIndex}" class="panel panel-default" style="display: none;">
                    <div class="panel-heading">
                        Visibility Options
                    </div>
                    <table class="table text-center" style="color: #000;">
                        <tbody>
                            <tr>
                                <th class="text-center">User/Group</th>
                                <th class="text-center">Can see this comment</th>
                                <th class="text-center">Can see comment giver's name</th>
                                <th class="text-center">Can see comment recipient's name</th>
                            </tr>
                            <c:if test="${comment.commentForPerson}">
                                <tr id="recipient-person${commentIndex}">
                                    <td class="text-left">
                                        <div data-toggle="tooltip"
                                             data-placement="top"
                                             title="Control what comment recipient(s) can view">
                                            Recipient(s)
                                        </div>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox answerCheckbox centered"
                                               name="receiverLeaderCheckbox"
                                               type="checkbox"
                                               value="<%= CommentParticipantType.PERSON %>"
                                               <c:if test="${comment.showCommentToRecipient}">checked</c:if>>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox giverCheckbox"
                                               type="checkbox"
                                               value="<%= CommentParticipantType.PERSON %>"
                                               <c:if test="${comment.showGiverNameToRecipient}">checked</c:if>>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox recipientCheckbox"
                                               name="receiverFollowerCheckbox"
                                               type="checkbox"
                                               value="<%= CommentParticipantType.PERSON %>"
                                               disabled>
                                    </td>
                                </tr>
                            </c:if>
                            <c:if test="${comment.commentForPerson || comment.commentForTeam}">
                                <tr id="recipient-team${commentIndex}">
                                    <td class="text-left">
                                        <div data-toggle="tooltip"
                                             data-placement="top"
                                             title="Control what team members of comment recipients can view">
                                            <c:choose>
                                                <c:when test="${comment.commentForTeam}">
                                                    Recipient Team
                                                </c:when>
                                                <c:otherwise>
                                                    Recipient's Team
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox answerCheckbox"
                                               type="checkbox"
                                               value="<%= CommentParticipantType.TEAM %>"
                                               <c:if test="${comment.showCommentToRecipientTeam}">checked</c:if>>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox giverCheckbox"
                                               type="checkbox"
                                               value="<%= CommentParticipantType.TEAM %>"
                                               <c:if test="${comment.showGiverNameToRecipientTeam}">checked</c:if>>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox recipientCheckbox"
                                               type="checkbox"
                                               value="<%= CommentParticipantType.TEAM %>"
                                               <c:if test="${comment.commentForTeam}">disabled</c:if>
                                               <c:if test="${comment.showRecipientNameToRecipientTeam}">checked</c:if>>
                                    </td>
                                </tr>
                            </c:if>
                            <c:if test="${not comment.commentForCourse}">
                                <tr id="recipient-section${commentIndex}">
                                    <td class="text-left">
                                        <div data-toggle="tooltip"
                                             data-placement="top"
                                             title="Control what other students in the same section can view">
                                            <c:choose>
                                                <c:when test="${comment.commentForSection}">
                                                    Recipient Section
                                                </c:when>
                                                <c:otherwise>
                                                    Recipient's Section
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox answerCheckbox"
                                               type="checkbox"
                                               value="<%= CommentParticipantType.SECTION %>"
                                               <c:if test="${comment.showCommentToRecipientSection}">checked</c:if>>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox giverCheckbox"
                                               type="checkbox"
                                               value="<%= CommentParticipantType.SECTION %>"
                                               <c:if test="${comment.showGiverNameToRecipientSection}">checked</c:if>>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox recipientCheckbox"
                                               type="checkbox"
                                               value="<%= CommentParticipantType.SECTION %>"
                                               <c:if test="${comment.commentForSection}">disabled</c:if>
                                               <c:if test="${comment.showRecipientNameToRecipientSection}">checked</c:if>>
                                    </td>
                                </tr>
                            </c:if>
                            <tr id="recipient-course${commentIndex}">
                                <td class="text-left">
                                    <div data-toggle="tooltip"
                                         data-placement="top"
                                         title="Control what other students in this course can view">
                                        <c:choose>
                                            <c:when test="${comment.commentForCourse}">
                                                Students in this course
                                            </c:when>
                                            <c:otherwise>
                                                Other students in this course
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox answerCheckbox"
                                           type="checkbox"
                                           value="<%= CommentParticipantType.COURSE %>"
                                           <c:if test="${comment.showCommentToCourse}">checked</c:if>>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox giverCheckbox"
                                           type="checkbox"
                                           value="<%= CommentParticipantType.COURSE %>"
                                           <c:if test="${comment.showGiverNameToCourse}">checked</c:if>>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox recipientCheckbox"
                                           type="checkbox"
                                           value="<%= CommentParticipantType.COURSE %>"
                                           <c:if test="${comment.commentForCourse}">disabled</c:if>
                                           <c:if test="${comment.showRecipientNameToCourse}">checked</c:if>>
                                </td>
                            </tr>
                            <tr>
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
                                           value="<%= CommentParticipantType.INSTRUCTOR %>"
                                           <c:if test="${comment.showCommentToInstructors}">checked</c:if>>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox giverCheckbox"
                                           type="checkbox"
                                           value="<%= CommentParticipantType.INSTRUCTOR %>"
                                           <c:if test="${comment.showGiverNameToInstructors}">checked</c:if>>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox recipientCheckbox"
                                           type="checkbox"
                                           value="<%= CommentParticipantType.INSTRUCTOR %>"
                                           <c:if test="${comment.showRecipientNameToInstructors}">checked</c:if>>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="form-group">
                    <div id="rich-text-toolbar-comment-container-${commentIndex}"></div>
                    <div class="panel panel-default panel-body" id="commentText${commentIndex}">
                         ${comment.commentText}
                    </div>
                    <input type="hidden" name="<%= Const.ParamsNames.COMMENT_TEXT %>">
                </div>
                <div class="col-sm-offset-5">
                    <input id="commentsave-${commentIndex}"
                           title="Save comment"
                           onclick="return submitCommentForm('${commentIndex}');"
                           type="submit"
                           class="btn btn-primary"
                           value="Save">
                    <input type="button"
                           class="btn btn-default"
                           value="Cancel"
                           onclick="return disableComment('${commentIndex}');">
                </div>
            </div>
            <input type="hidden" name="<%= Const.ParamsNames.COMMENT_EDITTYPE %>" id="<%= Const.ParamsNames.COMMENT_EDITTYPE %>-${commentIndex}" value="edit">
            <input type="hidden" name="<%= Const.ParamsNames.COMMENT_ID %>" value="${comment.commentId}">
            <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${comment.courseId}">
            <input type="hidden" name="<%= Const.ParamsNames.FROM_COMMENTS_PAGE %>" value="${comment.fromCommentsPage}">
            <c:if test="${not comment.fromCommentsPage}">
                <input type="hidden" name="<%= Const.ParamsNames.STUDENT_EMAIL %>" value="${comment.studentEmail}">
                <input type="hidden" name="<%= Const.ParamsNames.RECIPIENT_TYPE %>" value="${comment.recipientType}">
                <input type="hidden" name="<%= Const.ParamsNames.RECIPIENTS %>" value="${comment.recipientsString}">
            </c:if>
            <input type="hidden" name="<%= Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO %>" value="${comment.showCommentToString}">
            <input type="hidden" name="<%= Const.ParamsNames.COMMENTS_SHOWGIVERTO %>" value="${comment.showGiverNameToString}">
            <input type="hidden" name="<%= Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO %>" value="${comment.showRecipientNameToString}">
            <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
        </form>
    </c:if>
</li>