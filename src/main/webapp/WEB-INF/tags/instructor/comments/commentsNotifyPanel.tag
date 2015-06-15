<%@ tag description="Panel to notify students of new/edited comments" %>
<%@ tag import="teammates.common.util.Const" %>
<div class="btn-group pull-right" style="${data.numberOfPendingComments==0 ? 'display:none' : ''}">
    <a type="button" class="btn btn-sm btn-info" data-toggle="tooltip" style="margin-right: 17px;"
        href="<%= Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_CLEAR_PENDING %>?<%= Const.ParamsNames.COURSE_ID %>=${data.courseId}
                    &<%= Const.ParamsNames.USER_ID %>=${data.account.googleId}"
        title="Send email notification to ${data.numberOfPendingComments} recipient(s) of comments pending notification">
        <span class="badge" style="margin-right: 5px">${data.numberOfPendingComments}</span>
        <span class="glyphicon glyphicon-comment"></span>
        <span class="glyphicon glyphicon-arrow-right"></span>
        <span class="glyphicon glyphicon-envelope"></span>
    </a>
</div>