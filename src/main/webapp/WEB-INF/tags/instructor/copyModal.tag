<%@ tag description="instructorHome / instructorFeedbacks / instructorFeedbackEdit - Copy modal" %>
<%@ tag import="teammates.common.util.Const" %>
<div class="modal fade" id="fsCopyModal" tabindex="-1" role="dialog" 
     aria-labelledby="fsCopyModal" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="post" name="form_copy_list" role="form"
                  action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY %>">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title">
                        Copy this feedback session to other courses <br/>
                        <small>(Select the course(s) you want to copy this feedback session to)</small>
                    </h4>
                </div>
                <div class="modal-body">
                    <div id="courseList" class="form-group"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <input type="submit" class="btn btn-primary" id="fscopy_submit" value="Copy">
                    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
               </div>
            </form>
        </div>
    </div>
</div>