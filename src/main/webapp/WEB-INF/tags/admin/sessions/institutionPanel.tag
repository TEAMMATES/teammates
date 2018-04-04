<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Admin sessions - institution panel" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/sessions" prefix="adminSessions" %>
<%@ attribute name="institutionPanel" type="teammates.ui.template.InstitutionPanel" required="true"%>
<%@ attribute name="tableIndex" required="true"%>
<%@ attribute name="showAll" required="true"%>
<div class="panel panel-primary institution-panel">
  <ul class="nav nav-pills nav-stacked">
    <li id="pill_${tableIndex}" class="active">
      <a href="javascript:;" class="toggle-content" data-index="${tableIndex}">
        <span class="badge pull-right" id="badge_${tableIndex}" style="display: none">
          ${fn:length(institutionPanel.feedbackSessionRows)}
        </span>
        <strong>${institutionPanel.institutionName}</strong>
      </a>
    </li>
  </ul>
  <div class="table-responsive" id="table_${tableIndex}">
    <table class="table table-striped data-table">
      <thead>
        <tr>
          <th>Status</th>
          <th class="button-sort-none toggle-sort">
            [Course ID] Session Name &nbsp; <span class="icon-sort unsorted"></span>
          </th>
          <th>Response Rate</th>
          <th class="button-sort-none toggle-sort" data-toggle-sort-comparator="sortDate"
              data-toggle-sort-extractor="dateStampExtractor">Start Time&nbsp;<span class="icon-sort unsorted"></span>
          </th>
          <th class="button-sort-none toggle-sort" data-toggle-sort-comparator="sortDate"
              data-toggle-sort-extractor="dateStampExtractor">End Time&nbsp; <span class="icon-sort unsorted"></span>
          </th>
          <th class="button-sort-none">
            Creator<span class="icon-sort unsorted"></span>
          </th>
        </tr>
      </thead>

      <tbody>
        <c:forEach items="${institutionPanel.feedbackSessionRows}" var="feedbackSessionRow">
          <c:if test="${showAll or (not feedbackSessionRow.endsWithTmt)}">
            <adminSessions:feedbackSessionRow feedbackSessionRow="${feedbackSessionRow}" />
          </c:if>
        </c:forEach>
      </tbody>
    </table>
  </div>
</div>
