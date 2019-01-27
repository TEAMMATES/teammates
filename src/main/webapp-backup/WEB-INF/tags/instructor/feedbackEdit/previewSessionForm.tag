<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - feedback sessions preview form" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ attribute name="previewForm" type="teammates.ui.template.FeedbackSessionPreviewForm" required="true"%>

<div class="well well-plain" id="questionPreviewTable">
  <div class="row">
    <form class="form-horizontal">
      <label class="control-label col-xs-6 col-md-2 tablet-align-left text-right">
        Preview Session:
      </label>
    </form>
    <div class="col-xs-12 col-md-5 row tablet-margin-top-10px" data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.FEEDBACK_PREVIEW_ASSTUDENT %>">
      <form method="post" action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASSTUDENT %>"
          name="form_previewasstudent" class="form_preview" target="_blank">

        <div class="col-sm-6">
          <select class="form-control margin-bottom-7px" name="<%= Const.ParamsNames.PREVIEWAS %>">
            <c:forEach items="${previewForm.studentToPreviewAsOptions}" var="option">
              <option ${option.attributesToString}>
                ${fn:escapeXml(option.content)}
              </option>
            </c:forEach>
          </select>
        </div>
        <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${previewForm.fsName}">
        <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${previewForm.courseId}">
        <div class="col-sm-6">
          <input id="button_preview_student" type="submit" class="btn btn-primary" value="Preview as Student"
              <c:if test="${empty previewForm.studentToPreviewAsOptions}"> disabled style="background: #66727A;"</c:if>>
        </div>
        <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
      </form>
    </div>
    <div class="col-xs-12 col-md-5 row tablet-margin-top-10px" data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.FEEDBACK_PREVIEW_ASINSTRUCTOR %>">
      <form method="post" action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR %>"
          name="form_previewasinstructor" class="form_preview" target="_blank">
        <div class="col-sm-6">
          <select class="form-control margin-bottom-7px" name="<%= Const.ParamsNames.PREVIEWAS %>">
            <c:forEach items="${previewForm.instructorToPreviewAsOptions}" var="option">
              <option ${option.attributesToString}>
                ${option.content}
              </option>
            </c:forEach>
          </select>
        </div>
        <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${previewForm.fsName}">
        <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${previewForm.courseId}">
        <div class="col-sm-6">
          <input id="button_preview_instructor" type="submit" class="btn btn-primary" value="Preview as Instructor">
        </div>
        <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
      </form>
    </div>
  </div>
</div>
