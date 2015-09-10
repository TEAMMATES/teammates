<%@ tag description="instructorCourseDetails - Course Information Board" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.CommentParticipantType" %>
<%@ attribute name="courseId" required="true" %>

<div id="commentArea" class="well well-plain" style="display: none;">
    <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_ADD%>" name="form_commentadd">
        <div class="form-group form-inline">
            <label style="margin-right: 24px;">Recipient:</label> 
            
            <select id="comment_recipient_select" class="form-control" disabled="disabled">
                <option value="<%=CommentParticipantType.COURSE%>" selected>The whole class</option>
            </select>
            
            <a id="visibility-options-trigger" class="btn btn-sm btn-info pull-right">
                <span class="glyphicon glyphicon-eye-close"></span>
                Show Visibility Options
            </a>
        </div>
        
        <p class="form-group text-muted">
            The default visibility for your comment is private. You may change it using the &#8216;show visibility options&#8217; button above.
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
                    
                    <tr id="recipient-course">
                        <td class="text-left">
                            <div data-toggle="tooltip" data-placement="top" title="Control what students in this course can view">
                                Students in this course
                            </div>
                        </td>
                        <td>
                            <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=CommentParticipantType.COURSE%>">
                        </td>
                        <td>
                            <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=CommentParticipantType.COURSE%>">
                        </td>
                        <td>
                            <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=CommentParticipantType.COURSE%>" disabled="disabled">
                        </td>
                    </tr>
                    <tr>
                        <td class="text-left">
                            <div data-toggle="tooltip" data-placement="top" title="Control what instructors can view">
                                Instructors
                            </div>
                        </td>
                        <td>
                            <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=CommentParticipantType.INSTRUCTOR%>">
                        </td>
                        <td>
                            <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=CommentParticipantType.INSTRUCTOR%>">
                        </td>
                        <td>
                            <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=CommentParticipantType.INSTRUCTOR%>">
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <textarea class="form-control" rows="6" placeholder="Enter your comment here ..." style="margin-bottom: 15px;"
                name=<%=Const.ParamsNames.COMMENT_TEXT%> id="commentText"></textarea>
        <div style="text-align: center;">
            <input type="submit" class="btn btn-primary" id="button_save_comment" value="Save"> 
            <input type="button" class="btn btn-default" id="button_cancel_comment" value="Cancel">
            <input type="hidden" name=<%=Const.ParamsNames.COURSE_ID%> value="${courseId}">
            <input type="hidden" name=<%=Const.ParamsNames.RECIPIENT_TYPE%> value="<%=CommentParticipantType.COURSE%>">
            <input type="hidden" name=<%=Const.ParamsNames.RECIPIENTS%> value="${courseId}">
            <input type="hidden" name=<%=Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO%> value="">
            <input type="hidden" name=<%=Const.ParamsNames.COMMENTS_SHOWGIVERTO%> value="">
            <input type="hidden" name=<%=Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO%> value="">
            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
            <input type="hidden" name="<%=Const.ParamsNames.FROM_COURSE_DETAILS_PAGE%>" value="true">
        </div>
    </form>
</div>