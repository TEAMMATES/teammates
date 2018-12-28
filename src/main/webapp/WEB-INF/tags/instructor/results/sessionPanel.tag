<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResultsTop - Feedback Session Information Table" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="r" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ attribute name="sessionPanel" type="teammates.ui.template.InstructorFeedbackResultsSessionPanel" required="true" %>
<div class="well well-plain padding-0">
  <div class="form-horizontal">
    <div class="panel-heading">
      <div class="row">
        <div class="col-sm-5 col-lg-4">
          <div class="form-group">
            <label class="col-lg-4 control-label">Course ID:</label>
            <div class="col-lg-8">
              <p class="form-control-static">${sessionPanel.courseId}</p>
            </div>
          </div>
          <div class="form-group">
            <label class="col-lg-4 control-label">Session:</label>
            <div class="col-lg-8">
              <p class="form-control-static">${sessionPanel.feedbackSessionName}
                <c:if test="${not empty sessionPanel.editLink}">
                  <a href="${sessionPanel.editLink}"
                      id="edit_icon_button"
                      data-original-title="<%= Const.Tooltips.FEEDBACK_SESSION_EDIT %>"
                      data-placement="top"
                      data-toggle="tooltip">
                    <span class="glyphicon glyphicon-edit"></span>
                  </a>
                </c:if>
              </p>
            </div>
          </div>
        </div>
        <div class="col-sm-7 col-lg-6">
          <div class="form-group">
          <label class="col-lg-4 control-label">Session duration:</label>
          <div class="col-lg-8">
            <p class="form-control-static">${sessionPanel.startTime}&nbsp;&nbsp;&nbsp;<b>to</b>&nbsp;&nbsp;&nbsp;${sessionPanel.endTime}</p>
          </div>
          </div>
          <div class="form-group">
          <label class="col-lg-4 control-label">Results visible from:</label>
          <div class="col-lg-8">
            <p class="form-control-static">
              ${sessionPanel.resultsVisibleFrom}
            </p>
          </div>
        </div>
      </div>
      <div class="col-sm-4 col-lg-2">
        <div class="form-group">
          <div class="col-md-12">
            <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD%>">
              <div id="feedbackDataButtons">
                <input id="button_download" type="submit" class="btn btn-primary btn-block"
                    name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>"
                    value="Download Results">
              </div>
              <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
              <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="${sessionPanel.feedbackSessionName}">
              <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${sessionPanel.courseId}">
              <input type="hidden" name="<%=Const.ParamsNames.SECTION_NAME %>" value="${fn:escapeXml(sessionPanel.selectedSection)}">
              <input type="hidden" id="statsShownCheckBox" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS %>" value="${sessionPanel.isStatsShown}">
              <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES %>" value="${sessionPanel.isMissingResponsesShown}">
            </form>
            <br>
            <div>
              <f:feedbackSessionPublishButton buttonType="btn ${sessionPanel.feedbackSessionPublishButton.buttonType}"
                  publishButton="${sessionPanel.feedbackSessionPublishButton}"
                  showTooltip="true" />
            </div>
            <br>
            <div>
              <button id="button-print" type="input" class="btn btn-primary btn-block">
                Print View
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
      <div class="row">
        <span class="help-block align-center">
          Non-English characters not displayed properly in the downloaded file?
          <span class="btn-link"
              data-toggle="modal"
              data-target="#fsResultsTableWindow"
              id="btn-display-table">Click here.</span>
        </span>
      </div>
    </div>
  </div>
</div>

<r:copyPasteModal courseId="${sessionPanel.courseId}"
    feedbackSession="${sessionPanel.feedbackSessionName}"
    selectedSection="${sessionPanel.selectedSection}" />
