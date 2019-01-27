<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResultsBottom - Users with No Response Panel" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ attribute name="noResponsePanel" type="teammates.ui.template.InstructorFeedbackResultsNoResponsePanel" required="true" %>
<c:choose>
  <c:when test="${not empty noResponsePanel.emails}">
    <div class="panel-body padding-0">
      <table class="table table-striped table-bordered margin-0">
        <thead class="background-color-medium-gray text-color-gray font-weight-normal">
          <tr>
            <th id="button_sortFromTeam" class="button-sort-ascending toggle-sort" style="width: 30%;">
              Team<span class="icon-sort unsorted"></span>
            </th>
            <th id="button_sortTo" class="button-sort-none toggle-sort" style="width: 30%;">
              Name<span class="icon-sort unsorted"></span>
            </th>
            <th class="action-header">
              Actions
            </th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${noResponsePanel.emails}" var="email">
            <tr>
              <c:choose>
                <c:when test="${not empty noResponsePanel.instructorStatus[email] && noResponsePanel.instructorStatus[email] == 'true'}">
                  <td><i>${fn:escapeXml(noResponsePanel.teams[email])}</i></td>
                </c:when>
                <c:otherwise>
                  <td>${fn:escapeXml(noResponsePanel.teams[email])}</td>
                </c:otherwise>
              </c:choose>
              <td>${fn:escapeXml(noResponsePanel.names[email])}</td>
              <td class="action-button-item">
                <c:if test="${not empty noResponsePanel.moderationButtons[email]}">
                  <results:moderationButton moderationButton="${noResponsePanel.moderationButtons[email]}"/>
                </c:if>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </c:when>
  <c:otherwise>
    <div class="panel-body">
      All students have responded to some questions in this session.
    </div>
  </c:otherwise>
</c:choose>
