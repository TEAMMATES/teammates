<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - feedback sessions 'copy question' modal" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ attribute name="feedbackSessionName" required="true"%>
<%@ attribute name="courseId" required="true"%>

<div class="modal fade" id="copyModal" tabindex="-1" role="dialog" aria-labelledby="copyModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="copyModalTitle">Copy Questions</h4>
      </div>
      <div class="modal-body">
        <form class="form" id="copyModalForm" role="form" method="post"
            action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_COPY %>">

          <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${feedbackSessionName}">
          <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
          <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${courseId}">
          <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN %>" value="${data.sessionToken}">
        </form>
        <div id="question-copy-modal-status"></div>
      </div>
      <div class="modal-footer margin-0">
        <button type="button" class="btn btn-primary" id="button_copy_submit" disabled>Copy</button>
        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
      </div>
    </div>
  </div>
</div>
