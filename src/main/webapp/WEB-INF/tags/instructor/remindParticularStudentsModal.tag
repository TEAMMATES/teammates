<%@ tag description="instructorHome / instructorFeedbacks - Remind modal" %>
<%@ tag import="teammates.common.util.Const" %>
<div class="modal fade" id="remindModal" tabindex="-1" role="dialog" 
     aria-labelledby="remindModal" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="post" name="form_remind_list" role="form"
                  action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS %>"> 
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" 
                            aria-hidden="true">&times;</button>
                    <h4 class="modal-title">
                        Remind Particular Students
                        <small>(Select the student(s) you want to remind)</small>
                    </h4>
                </div>
                <div class="modal-body">
                    <div id="studentList" class="form-group"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" 
                            data-dismiss="modal">Cancel</button>
                    <input type="submit" class="btn btn-primary" value="Remind">
                    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" 
                           value="${data.account.googleId}">
                </div>
            </form>
        </div>
    </div>
</div>