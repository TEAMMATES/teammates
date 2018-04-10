<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResultsTop - Filter Panel" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="r" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="filterPanel" type="teammates.ui.template.InstructorFeedbackResultsFilterPanel" required="true" %>

<div style="margin-bottom: 10px">
  <div class="well well-plain">
    <div class="row">
      <div class="col-md-5">
        <div data-toggle="tooltip" title="View results in different formats">
          <div class="row">
            <label class="col-md-offset-1 col-md-3">View:</label>
            <div class="col-md-8">
              <c:if test="${filterPanel.sortType == 'question'}">
                Group by - Question
              </c:if>
              <c:if test="${filterPanel.sortType == 'giver-recipient-question'}">
                Group by - Giver > Recipient > Question
              </c:if>
              <c:if test="${filterPanel.sortType == 'recipient-giver-question'}">
                Group by - Recipient > Giver > Question
              </c:if>
              <c:if test="${filterPanel.sortType == 'giver-question-recipient'}">
                Group by - Giver > Question > Recipient
              </c:if>
              <c:if test="${empty filterPanel.sortType or filterPanel.sortType == 'recipient-question-giver'}">
                Group by - Recipient > Question > Giver
              </c:if>
            </div>
          </div>
        </div>
        <c:if test="${not empty filterPanel.sections}">
          <br>
          <div class="row">
            <div data-toggle="tooltip" title="View results by sections">
              <label class="col-md-offset-1 col-md-3">Section:</label>
              <div class="col-md-8">
                <c:if test="${filterPanel.allSectionsSelected}">
                  All
                </c:if>
                <c:forEach items="${filterPanel.sections}" var="section">
                  <c:if test="${filterPanel.selectedSection == section}">
                    ${fn:escapeXml(section)}
                  </c:if>
                </c:forEach>
                <c:if test="${filterPanel.noneSectionSelected}">
                  <%=Const.NO_SPECIFIC_SECTION%>
                </c:if>
              </div>
            </div>
          </div>
        </c:if>
      </div>
      <div class="col-md-offset-4 pull-right col-md-3 margin-bottom-15px">
        <label>Additional settings:</label>
        <ul>
          <li>
            <div data-toggle="tooltip" title="Group results in the current view by team">
              <div <c:if test="${not filterPanel.groupedByTeam}"> class="text-strike"</c:if>>
                Group by Teams
              </div>
            </div>
          </li>
          <li>
            <div data-toggle="tooltip" title="Show statistics">
              <div <c:if test="${not filterPanel.statsShown}"> class="text-strike"</c:if>>
                Show Statistics
              </div>
            </div>
          </li>
          <li>
            <div data-toggle="tooltip" title="Indicate missing responses">
              <div <c:if test="${not filterPanel.missingResponsesShown}"> class="text-strike"</c:if>>
                Indicate Missing Responses
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <div class="row">
      <div class="col-md-offset-10 col-md-2">
        <button id="editBtn" type="button" class="btn btn-primary btn-block"
            data-toggle="modal" data-target="#editModal">
          Edit View
        </button>
      </div>
    </div>
  </div>

  <r:filterEditModal filterPanel="${filterPanel}" />

</div>
