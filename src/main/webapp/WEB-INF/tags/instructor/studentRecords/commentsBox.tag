<%@ tag description="instructorStudentRecords - Comments section" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.CommentParticipantType" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/studentRecords" prefix="tisr" %>
<%@ attribute name="comments" type="teammates.ui.template.InstructorStudentRecordsCommentsBox" required="true" %>
<c:set var="commentIdx" value="${fn:length(comments.comments) - 1}" />
<div class="panel panel-info">
    <div id="studentComments" class="panel-heading">
        Comments for ${comments.studentName}
    </div>
    <div class="panel-body">
        Your comments on this student:
        <button type="button"
                class="btn btn-default btn-xs icon-button pull-right"
                id="button_add_comment"
                onclick="showAddCommentBox();"
                data-toggle="tooltip"
                data-placement="top"
                title="<%= Const.Tooltips.COMMENT_ADD %>"
                <c:if test="${not comments.instructorAllowedToGiveComment}">disabled="disabled"</c:if>>
            <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
        </button>
        <ul class="list-group" style="margin-top: 15px;">
            <c:choose>
                <c:when test="${not empty comments.comments}">
                    <c:forEach items="${comments.comments}" var="comment" varStatus="i">
                        <tisr:comment comment="${comment}" index="${i.index}" />
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <li class="list-group-item list-group-item-warning">
                        You don't have any comments on this student.
                    </li>
                </c:otherwise>
            </c:choose>
            <li class="list-group-item list-group-item-warning" id="comment_box" style="display: none;">
                <form method="post"
                      action="<%= Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_ADD %>"
                      name="form_commentadd"
                      class="form_comment">
                    <div class="form-group form-inline">
                        <div class="form-group text-muted">
                            <p>
                                Comment about ${comments.studentName}:
                            </p>
                            The default visibility for your comment is private. You may change it using the visibility options.
                        </div>
                        <a id="visibility-options-trigger${commentIdx}" class="btn btn-sm btn-info pull-right">
                            <span class="glyphicon glyphicon-eye-close"></span>
                            Show Visibility Options
                        </a>
                    </div>
                    <div id="visibility-options${commentIdx}" class="panel panel-default" style="display: none;">
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
                                <tr id="recipient-person${commentIdx}">
                                    <td class="text-left">
                                        <div data-toggle="tooltip"
                                             data-placement="top"
                                             title=""
                                             data-original-title="Control what comment recipient(s) can view">
                                            Recipient(s)
                                        </div>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox answerCheckbox centered"
                                               name="receiverLeaderCheckbox"
                                               type="checkbox"
                                               value="<%= CommentParticipantType.PERSON %>">
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%= CommentParticipantType.PERSON %>">
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox recipientCheckbox"
                                               name="receiverFollowerCheckbox"
                                               type="checkbox"
                                               value="<%= CommentParticipantType.PERSON %>"
                                               disabled="disabled">
                                    </td>
                                </tr>
                                <tr id="recipient-team${commentIdx}">
                                    <td class="text-left">
                                        <div data-toggle="tooltip"
                                             data-placement="top"
                                             title=""
                                             data-original-title="Control what team members of comment recipients can view">
                                            Recipient's Team
                                        </div>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%= CommentParticipantType.TEAM %>">
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%= CommentParticipantType.TEAM %>">
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%= CommentParticipantType.TEAM %>">
                                    </td>
                                </tr>
                                <tr id="recipient-course${commentIdx}">
                                    <td class="text-left">
                                        <div data-toggle="tooltip"
                                             data-placement="top"
                                             title=""
                                             data-original-title="Control what other students in this course can view">
                                            Other students in this course
                                        </div>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%= CommentParticipantType.COURSE %>">
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%= CommentParticipantType.COURSE %>">
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%= CommentParticipantType.COURSE %>">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="text-left">
                                        <div data-toggle="tooltip"
                                             data-placement="top"
                                             title=""
                                             data-original-title="Control what instructors can view">
                                            Instructors
                                        </div>
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%= CommentParticipantType.INSTRUCTOR %>">
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%= CommentParticipantType.INSTRUCTOR %>">
                                    </td>
                                    <td>
                                        <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%= CommentParticipantType.INSTRUCTOR %>">
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
                                  id="commentText"></textarea>
                    </div>
                    <div class="col-sm-offset-5">
                        <input type="submit" class="btn btn-primary" id="button_save_comment" value="Save">
                        <input type="button" class="btn btn-default" value="Cancel" onclick="hideAddCommentBox();">
                        <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${comments.courseId}">
                        <input type="hidden" name="<%= Const.ParamsNames.STUDENT_EMAIL %>" value="${comments.email}">
                        <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${comments.googleId}">
                        <input type="hidden" name="<%= Const.ParamsNames.RECIPIENT_TYPE %>" value="<%= CommentParticipantType.PERSON %>">
                        <input type="hidden" name="<%= Const.ParamsNames.RECIPIENTS %>" value="${comments.email}">
                        <input type="hidden" name="<%= Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO %>" value="">
                        <input type="hidden" name="<%= Const.ParamsNames.COMMENTS_SHOWGIVERTO %>" value="">
                        <input type="hidden" name="<%= Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO %>" value="">
                        <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${comments.googleId}">
                    </div>
                </form>
            </li>
        </ul>
    </div>
</div>