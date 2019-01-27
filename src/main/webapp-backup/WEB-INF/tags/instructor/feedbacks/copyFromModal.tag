<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks  - Copy From Another FS modal" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>

<%@ attribute name="copyFromModal" type="teammates.ui.template.FeedbackSessionsCopyFromModal" required="true"%>

<div class="modal fade" id="copyModal" tabindex="-1" role="dialog"
    aria-labelledby="copyModalTitle" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="copyModalTitle">
          Creating a new session by copying a previous session
        </h4>
      </div>
      <div class="modal-body" id="copySessionsBody">
        <form class="form" id="copyModalForm" role="form" method="post"
            action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_COPY %>">
          <%-- Course --%>
          <div class="form-group">
            <label for="modalCopiedCourseId" class="control-label">
              Create in course
            </label>
            <select class="form-control"
                name="<%= Const.ParamsNames.COPIED_COURSE_ID %>"
                id="modalCopiedCourseId">
              <c:forEach items="${copyFromModal.coursesSelectField}" var="option">
                <option ${option.attributesToString}>${option.content}</option>
              </c:forEach>
            </select>
          </div>
          <%-- Session Name --%>
          <div class="form-group">
            <label for="modalCopiedSessionName" class="control-label">
              Name for new session
            </label>
            <input class="form-control" type="text"
                name="<%= Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME %>"
                id="modalCopiedSessionName"
                maxlength="<%= FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH %>"
                value="${copyFromModal.fsName}"
                placeholder="e.g. Feedback for Project Presentation 1">
          </div>
          <%-- Previous Session --%>
          <label>Copy settings and questions from</label>
          <table class="table-responsive table table-bordered table-hover margin-0" id="copyTableModal">
            <thead class="fill-primary">
              <tr>
                <th style="width:20px;">&nbsp;</th>
                <th>Course ID</th>
                <th>Feedback Session Name</th>
              </tr>
            </thead>
            <c:forEach items="${copyFromModal.existingFeedbackSessions}" var="session" varStatus="i">
              <tr style="cursor:pointer;">
                <td><input type="radio"></td>
                <td>${session.courseId}</td>
                <td>${session.name}</td>
              </tr>
            </c:forEach>
          </table>
          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
              value="" id="modalSessionName">
          <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>"
              value="" id="modalCourseId">
          <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>"
              value="${data.account.googleId }">
          <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN %>"
              value="${data.sessionToken}">
        </form>
      </div>
      <div class="modal-footer margin-0">
        <button type="button" class="btn btn-primary" id="button_copy_submit" disabled>
          Copy
        </button>
        <button type="button" class="btn btn-default" data-dismiss="modal">
          Cancel
        </button>
      </div>
    </div>
  </div>
</div>
