<%@ tag description="instructorFeedbackResultsTop - Copy & Paste Feedback Session Table Modal" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="r" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="feedbackSession" required="true" %>
<%@ attribute name="selectedSection" required="true" %>
<form id="csvToHtmlForm">
    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${courseId}">
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="${feedbackSession}">
    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION%>" value="${selectedSection}">
    <input type="hidden" name="<%=Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED%>" value="true">
</form>
<div class="modal fade align-center" id="fsResultsTableWindow">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <span class="help-block" style="display:inline;">
                    Tips: After selecting the table, <kbd>Ctrl + C</kbd> to COPY and
                    <kbd>Ctrl + V</kbd> to PASTE to your Excel Workbook.
                </span>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <button type="button" class="btn btn-default"
                    data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary"
                    onclick="selectElementContents(document.getElementById('fsModalTable'));">
                    Select Table</button>
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