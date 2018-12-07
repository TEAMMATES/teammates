<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResultsTop - Copy & Paste Feedback Session Table Modal" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="r" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="feedbackSession" required="true" %>
<%@ attribute name="selectedSection" required="true" %>
<form id="csvToHtmlForm">
  <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${courseId}">
  <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="${feedbackSession}">
  <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
  <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION%>" value="${fn:escapeXml(selectedSection)}">
  <input type="hidden" name="<%=Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED%>" value="true">
</form>
<div class="modal fade align-center" id="fsResultsTableWindow">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
        <span class="help-block">
          Tips: <span class="btn-link" id="btn-select-element-contents">Click here</span> to select the table,
          then <kbd>Ctrl + C</kbd> or <kbd>⌘ + C</kbd> to COPY and <kbd>Ctrl + V</kbd> or <kbd>⌘ + V</kbd> to PASTE to your Excel Workbook.
        </span>
      </div>
      <div class="modal-body">
        <div class="table-responsive">
          <div id="fsModalTable"></div>
          <br>
          <div id="ajaxStatus"></div>
        </div>
      </div>
      <div class="modal-footer"></div>
    </div>
  </div>
</div>
