<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResultsTop - Filter Panel Edit Modal" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="filterPanel" type="teammates.ui.template.InstructorFeedbackResultsFilterPanel" required="true" %>

<div id="editModal" class="modal fade" role="dialog">
  <div class="modal-dialog">
    <form method="post" action="${filterPanel.resultsLink}">
      <div class="modal-content">
        <div class="modal-header alert-info">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">Edit View</h4>
        </div>
        <div class="modal-body">
          <div class="row">
            <div class="col-md-7">
              <div class="form-group">
                <label for="viewSelect" class="control-label">
                  View:
                </label>
                <div data-toggle="tooltip" title="View results in different formats">
                  <select id="viewSelect" class="form-control" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>">
                    <option value="<%=Const.FeedbackSessionResults.QUESTION_SORT_TYPE%>"<c:if test="${filterPanel.sortType == 'question'}"> selected</c:if>>
                      Group by - Question
                    </option>
                    <option value="<%=Const.FeedbackSessionResults.GRQ_SORT_TYPE%>"<c:if test="${filterPanel.sortType == 'giver-recipient-question'}"> selected</c:if>>
                      Group by - Giver > Recipient > Question
                    </option>
                    <option value="<%=Const.FeedbackSessionResults.RGQ_SORT_TYPE%>"<c:if test="${filterPanel.sortType == 'recipient-giver-question'}"> selected</c:if>>
                      Group by - Recipient > Giver > Question
                    </option>
                    <option value="<%=Const.FeedbackSessionResults.GQR_SORT_TYPE%>"<c:if test="${filterPanel.sortType == 'giver-question-recipient'}"> selected</c:if>>
                      Group by - Giver > Question > Recipient
                    </option>
                    <option value="<%=Const.FeedbackSessionResults.RQG_SORT_TYPE%>"<c:if test="${empty filterPanel.sortType or filterPanel.sortType == 'recipient-question-giver'}"> selected</c:if>>
                      Group by - Recipient > Question > Giver
                    </option>
                  </select>
                </div>
              </div>
              <c:if test="${not empty filterPanel.sections}">
                <div data-toggle="tooltip" title="View results by sections">
                  <div class="form-group">
                    <label for="sectionSelect" class="control-label">
                      Section:
                    </label>
                    <select id="sectionSelect" class="form-control" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION%>">
                      <option value="All"<c:if test="${filterPanel.allSectionsSelected}"> selected</c:if>>
                        All
                      </option>
                      <c:forEach items="${filterPanel.sections}" var="section">
                        <option value="${fn:escapeXml(section)}"<c:if test="${filterPanel.selectedSection == section}"> selected</c:if>>
                          ${fn:escapeXml(section)}
                        </option>
                      </c:forEach>
                      <option value="None"<c:if test="${filterPanel.noneSectionSelected}"> selected</c:if>>
                        <%=Const.NO_SPECIFIC_SECTION%>
                      </option>
                    </select>
                  </div>
                </div>
              </c:if>
            </div>
            <div class="col-md-5">
              <label class="control-label">
                Additional settings:
              </label>
              <div data-toggle="tooltip" title="Group results in the current view by team">
                <div class="checkbox">
                  <label<c:if test="${filterPanel.sortType == 'question'}"> class="text-strike"</c:if>>
                    <input type="checkbox" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>"
                        id="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>"
                        <c:if test="${filterPanel.groupedByTeam}">checked=""</c:if>
                        <c:if test="${filterPanel.sortType != 'question'}">class="checkbox-group-by-team"</c:if>> Group by Teams
                  </label>
                </div>
              </div>
              <div data-toggle="tooltip" title="Show statistics">
                <div class="checkbox">
                  <label<c:if test="${filterPanel.sortType == 'recipient-giver-question' or filterPanel.sortType == 'giver-recipient-question'}"> class="text-strike"</c:if>>
                    <input type="checkbox" id="show-stats-checkbox"
                        name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS%>"
                    <c:if test="${filterPanel.statsShown}"> checked=""</c:if>> Show Statistics
                  </label>
                </div>
              </div>
              <div data-toggle="tooltip" title="Indicate missing responses">
                <div class="checkbox">
                  <input type="checkbox"
                      id="indicate-missing-responses-checkbox"
                      value="true"
                      name="<%=Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES%>"
                      <c:if test="${filterPanel.missingResponsesShown}"> checked=""</c:if>>
                  Indicate Missing Responses
                </div>
              </div>
            </div>
          </div>

          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
              value="${filterPanel.feedbackSessionName}">
          <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>"
              value="${filterPanel.courseId}">
          <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>"
              value="${data.account.googleId}">
        </div>

        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
          <button id="submitBtn" type="submit" class="btn btn-primary">Change View</button>
        </div>
      </div>
    </form>
  </div>
</div>
