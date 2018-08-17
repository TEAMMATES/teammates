<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorHome - Course table" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/home" prefix="home" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="sessionRows" type="java.util.Collection" required="true" %>
<div class="panel-collapse collapse">
  <div class="panel-body padding-0">
    <table class="table-responsive table table-striped table-bordered margin-0">
      <thead>
        <tr>
          <th class="button_sortname button-sort-none toggle-sort">
            Session Name<span class="icon-sort unsorted"></span>
          </th>
          <th class="button_sortstartdate button-sort-none toggle-sort"
              data-toggle-sort-comparator="sortDate"
              data-toggle-sort-extractor="dateStampExtractor">Start Date<span class="icon-sort unsorted"></span></th>
          <th class="button_sortenddate button-sort-none toggle-sort"
              data-toggle-sort-comparator="sortDate"
              data-toggle-sort-extractor="dateStampExtractor">End Date<span class="icon-sort unsorted"></span></th>
          <th>Submissions</th>
          <th>Responses</th>
          <th>
            <span class="text-nowrap" title="<%= Const.Tooltips.FEEDBACK_SESSION_RESPONSE_RATE %>"
                data-toggle="tooltip" data-placement="top">Response Rate</span>
          </th>
          <th class="col-lg-4 no-print">Action(s)</th>
        </tr>
      </thead>
      <c:if test="${empty sessionRows}">
        <tr>
          <td>
            <span class="text-muted"> This course does not have any sessions yet.
            </span>
          </td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
        </tr>
      </c:if>
      <c:forEach items="${sessionRows}" var="sessionRow" varStatus="i">
        <tr id="session${i.index}">
          <td>
            ${sessionRow.name}
          </td>
          <td class="text-nowrap" data-date-stamp="${sessionRow.startTimeIso8601Utc}">
            <span title="${sessionRow.startTimeToolTip}" data-toggle="tooltip">${sessionRow.startTime}</span>
          </td>
          <td class="text-nowrap" data-date-stamp="${sessionRow.endTimeIso8601Utc}">
            <span title="${sessionRow.endTimeToolTip}" data-toggle="tooltip">${sessionRow.endTime}</span>
          </td>
          <td>
            <span title="${sessionRow.submissionsTooltip}" data-toggle="tooltip" data-placement="top">
              ${sessionRow.submissionStatus}
            </span>
          </td>
          <td>
            <span title="${sessionRow.publishedTooltip}" data-toggle="tooltip" data-placement="top">
              ${sessionRow.publishedStatus}
            </span>
          </td>
          <td class="session-response-for-test">
            <a oncontextmenu="return false;" href="${sessionRow.href}">Show</a>
          </td>
          <td class="no-print text-nowrap padding-right-25px">
            <home:feedbackSessionActions actions="${sessionRow.actions}" />
          </td>
        </tr>
      </c:forEach>
    </table>
  </div>
</div>
