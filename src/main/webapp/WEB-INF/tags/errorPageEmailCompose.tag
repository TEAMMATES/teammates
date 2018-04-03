<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Email composer to send feedback for error pages" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Config" %>
<%@ tag import="teammates.common.util.Const" %>
<form action="<%= Const.ActionURIs.ERROR_FEEDBACK_SUBMIT %>" method="post" id="error-feedback-form">
  <div class="form-group">
    <label for="error-feedback-email-composer-recipient-email">To:</label>
    <br><em>Note: This is the TEAMMATES support email. It has been pre-filled for your convenience and is not editable. </em>
    <input type="email" value="<%= Config.SUPPORT_EMAIL %>" id="error-feedback-email-composer-recipient-email" class="form-control" name="<%= Const.ParamsNames.ERROR_FEEDBACK_EMAIL_RECEIVER_ADDRESS %>" readonly="">
  </div>
  <div class="form-group">
    <label for="error-feedback-email-composer-subject">Subject:</label>
    <br><em>Note: This field has also been pre-filled for convenience. Feel free to change it to suit your needs. </em>
    <input type="text" value="<%= Const.ERROR_FEEDBACK_EMAIL_SUBJECT %>" id="error-feedback-email-composer-subject" name="<%= Const.ParamsNames.ERROR_FEEDBACK_EMAIL_SUBJECT %>" class="form-control">
  </div>
  <div class="form-group">
    <label for="error-feedback-email-composer-content">Content:</label>
    <textarea id="error-feedback-email-composer-content" name="<%= Const.ParamsNames.ERROR_FEEDBACK_EMAIL_CONTENT %>" class="form-control" placeholder="Tell us the steps you took that led you to this error page"></textarea>
  </div>
  <button type="submit" class="btn btn-success">Send feedback</button>
  <input type="hidden" name="<%= Const.ParamsNames.REGKEY %>" value="${param.key}">
  <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${param.courseid}">
  <input type="hidden" name="<%= Const.ParamsNames.STUDENT_EMAIL %>" value="${param.studentemail}">
  <input type="hidden" name="<%= Const.ParamsNames.ERROR_FEEDBACK_URL_REQUESTED %>" value="${param.errorfeedbackrequestedurl}">
</form>
