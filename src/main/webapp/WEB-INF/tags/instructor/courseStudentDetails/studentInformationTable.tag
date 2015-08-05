<%@ tag description="instructorCourseStudentDetails / instructorStudentRecords - Student Information" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="studentInfoTable" type="teammates.ui.template.StudentInfoTable" required="true" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.CommentParticipantType" %>
<div class="well well-plain">
    <button type="button" class="btn btn-default btn-xs icon-button pull-right"
            id="button_add_comment" data-toggle="tooltip"
            data-placement="top" title="Add comment"
        <c:if test="${not studentInfoTable.ableToAddComment}">disabled="disabled"</c:if>>
        <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
    </button>
    <div class="form form-horizontal" id="studentInfomationTable">
        <div class="form-group">
            <label class="col-sm-1 control-label">Student Name:</label>
            <div class="col-sm-11" id="<%=Const.ParamsNames.STUDENT_NAME%>">
                <p class="form-control-static">${studentInfoTable.name}</p>
            </div>
        </div>
        <c:if test="${studentInfoTable.hasSection}">
            <div class="form-group">
                <label class="col-sm-1 control-label">Section Name:</label>
                <div class="col-sm-11" id="<%= Const.ParamsNames.SECTION_NAME %>">
                    <p class="form-control-static">${studentInfoTable.section}</p>
                </div>
            </div>
        </c:if>
        <div class="form-group">
            <label class="col-sm-1 control-label">Team Name:</label>
            <div class="col-sm-11" id="<%= Const.ParamsNames.TEAM_NAME %>">
                <p class="form-control-static">${studentInfoTable.team}</p>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-1 control-label">Official Email Address:</label>
            <div class="col-sm-11" id="<%= Const.ParamsNames.STUDENT_EMAIL %>">
                <p class="form-control-static">${studentInfoTable.email}</p>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-1 control-label">Comments:</label>
            <div class="col-sm-11" id="<%= Const.ParamsNames.COMMENTS %>">
                <p class="form-control-static">${studentInfoTable.comments}</p>
            </div>
        </div>
    </div>
</div>
<div id="commentArea" class="well well-plain" style="display: none;">
    <form method="post" action="<%= Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_ADD %>" name="form_commentadd">
        <div class="form-group form-inline">
            <label style="margin-right: 24px;">Recipient:</label>
            <select id="comment_recipient_select" class="form-control">
                <option value="<%= CommentParticipantType.PERSON %>" selected>${studentInfoTable.name}</option>
                <option value="<%= CommentParticipantType.TEAM %>">${studentInfoTable.team}</option>
                <c:if test="${studentInfoTable.hasSection && studentInfoTable.section != 'None'}">
                    <option value="<%= CommentParticipantType.SECTION %>">${studentInfoTable.section}</option>
                </c:if>
            </select>
            <a id="visibility-options-trigger" class="btn btn-sm btn-info pull-right">
                <span class="glyphicon glyphicon-eye-close"></span>
                Show Visibility Options
            </a>
        </div>
        <p class="form-group text-muted">
            The default visibility for your comment is private. You may change it using the visibility options above.
        </p>
        <div id="visibility-options" class="panel panel-default" style="display: none;">
            <div class="panel-heading">Visibility Options</div>
            <table class="table text-center" style="background: #fff;">
                <tbody>
                    <tr>
                        <th class="text-center">User/Group</th>
                        <th class="text-center">Can see your comment</th>
                        <th class="text-center">Can see giver's name</th>
                        <th class="text-center">Can see recipient's name</th>
                    </tr>
                    <tr id="recipient-person">
                        <td class="text-left">
                            <div data-toggle="tooltip" data-placement="top"
                                 title="Control what comment recipient(s) can view">
                                Recipient(s)
                            </div>
                        </td>
                        <td>
                            <input class="visibilityCheckbox answerCheckbox centered"
                                   name="receiverLeaderCheckbox"
                                   type="checkbox" value="<%= CommentParticipantType.PERSON %>">
                        </td>
                        <td>
                            <input class="visibilityCheckbox giverCheckbox"
                                   type="checkbox" value="<%= CommentParticipantType.PERSON %>">
                        </td>
                        <td>
                            <input class="visibilityCheckbox recipientCheckbox"
                                   name="receiverFollowerCheckbox"
                                   type="checkbox" value="<%= CommentParticipantType.PERSON %>"
                                   disabled="disabled">
                        </td>
                    </tr>
                    <tr id="recipient-team">
                        <td class="text-left">
                            <div data-toggle="tooltip" data-placement="top"
                                 title="Control what team members of comment recipients can view">
                                Recipient's Team
                            </div>
                        </td>
                        <td>
                            <input class="visibilityCheckbox answerCheckbox"
                                   type="checkbox" value="<%= CommentParticipantType.TEAM %>">
                        </td>
                        <td>
                            <input class="visibilityCheckbox giverCheckbox"
                                   type="checkbox" value="<%= CommentParticipantType.TEAM %>">
                        </td>
                        <td>
                            <input class="visibilityCheckbox recipientCheckbox"
                                   type="checkbox" value="<%= CommentParticipantType.TEAM %>">
                        </td>
                    </tr>
                    <c:if test="${studentInfoTable.hasSection}">
                        <tr id="recipient-section">
                            <td class="text-left">
                                <div data-toggle="tooltip" data-placement="top"
                                     title="Control what students in the same section can view">
                                    Recipient's Section
                                </div>
                            </td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox"
                                       type="checkbox" value="<%= CommentParticipantType.SECTION %>">
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox"
                                       type="checkbox" value="<%= CommentParticipantType.SECTION %>">
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox"
                                       type="checkbox" value="<%= CommentParticipantType.SECTION %>">
                            </td>
                        </tr>
                    </c:if>
                    <tr id="recipient-course">
                        <td class="text-left">
                            <div data-toggle="tooltip" data-placement="top"
                                 title="Control what other students in this course can view">
                                Other students in this course
                            </div>
                        </td>
                        <td>
                            <input class="visibilityCheckbox answerCheckbox"
                                   type="checkbox" value="<%= CommentParticipantType.COURSE %>">
                        </td>
                        <td>
                            <input class="visibilityCheckbox giverCheckbox"
                                   type="checkbox" value="<%= CommentParticipantType.COURSE %>">
                        </td>
                        <td>
                            <input class="visibilityCheckbox recipientCheckbox"
                                   type="checkbox" value="<%= CommentParticipantType.COURSE %>">
                        </td>
                    </tr>
                    <tr>
                        <td class="text-left">
                            <div data-toggle="tooltip" data-placement="top"
                                 title="Control what instructors can view">
                                Instructors
                            </div>
                        </td>
                        <td>
                            <input class="visibilityCheckbox answerCheckbox"
                                   type="checkbox" value="<%= CommentParticipantType.INSTRUCTOR %>">
                        </td>
                        <td>
                            <input class="visibilityCheckbox giverCheckbox"
                                   type="checkbox" value="<%= CommentParticipantType.INSTRUCTOR %>">
                        </td>
                        <td>
                            <input class="visibilityCheckbox recipientCheckbox"
                                   type="checkbox" value="<%= CommentParticipantType.INSTRUCTOR %>">
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <textarea class="form-control" rows="6" placeholder="Enter your comment here ..." style="margin-bottom: 15px;"
                  name="<%= Const.ParamsNames.COMMENT_TEXT %>" id="commentText"></textarea>
        <div style="text-align: center;">
            <input type="submit" class="btn btn-primary" id="button_save_comment" value="Save">
            <input type="button" class="btn btn-default" id="button_cancel_comment" value="Cancel">
            <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${studentInfoTable.course}">
            <input type="hidden" name="<%=Const.ParamsNames.STUDENT_EMAIL%>" value="${studentInfoTable.email}">
            <input type="hidden" name="<%=Const.ParamsNames.RECIPIENT_TYPE%>" value="<%= CommentParticipantType.PERSON %>">
            <input type="hidden" name="<%=Const.ParamsNames.RECIPIENTS%>" value="${studentInfoTable.email}">
            <input type="hidden" name="<%=Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO%>" value="">
            <input type="hidden" name="<%=Const.ParamsNames.COMMENTS_SHOWGIVERTO%>" value="">
            <input type="hidden" name="<%=Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO%>" value="">
            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
            <input type="hidden" name="<%=Const.ParamsNames.FROM_STUDENT_DETAILS_PAGE%>" value="true">
        </div>
    </form>
</div>