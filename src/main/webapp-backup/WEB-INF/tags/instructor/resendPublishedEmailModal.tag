<%@ tag description="instructorHome - Resend published email modal" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="sessionResendPublishedEmailLink" required="true" %>

<div class="modal fade" id="resendPublishedEmailModal" tabindex="-1" role="dialog"
    aria-labelledby="resendPublishedEmailModal" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <form method="post" name="form_email_list" role="form"
          action="${sessionResendPublishedEmailLink}">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal"
              aria-hidden="true">&times;</button>
          <h4 class="modal-title">
            Resend Published Email
            <small>(Select the student(s) you want to resend the published email to)</small>
          </h4>
        </div>
        <div class="modal-body">
          <div id="studentEmailList" class="form-group"></div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default"
              data-dismiss="modal">Cancel</button>
          <input type="button" class="btn btn-primary resend-published-email-particular-button" data-dismiss="modal" value="Send">
          <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>"
              value="${data.account.googleId}">
        </div>
      </form>
    </div>
  </div>
</div>
