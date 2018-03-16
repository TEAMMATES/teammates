<%@ tag description="instructorHome - Publish email modal" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="sessionResendPublishedEmailLink" required="true" %>

<div class="modal fade" id="publishEmailModal" tabindex="-1" role="dialog"
    aria-labelledby="publishEmailModal" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <form method="post" name="form_email_list" role="form"
          action="${sessionResendPublishedEmailLink}">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal"
              aria-hidden="true">&times;</button>
          <h4 class="modal-title">
            Email Particular Students
            <small>(Select the student(s) you want to send email to)</small>
          </h4>
        </div>
        <div class="modal-body">
          <div id="studentEmailList" class="form-group"></div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default"
              data-dismiss="modal">Cancel</button>
          <input type="button" class="btn btn-primary publish-email-particular-button" data-dismiss="modal" value="Send">
          <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>"
              value="${data.account.googleId}">
        </div>
      </form>
    </div>
  </div>
</div>
