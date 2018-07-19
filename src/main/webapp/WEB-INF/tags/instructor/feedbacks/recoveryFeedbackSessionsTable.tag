<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - recovery feedback sessions table/list" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="recoveryFsList" type="teammates.ui.template.RecoveryFeedbackSessionsTable" required="true" %>

<c:set var="tableHeaderClass" value="background-color-medium-gray text-color-gray font-weight-normal" />
<table class="table table-bordered table-striped margin-0" id="tableRecoveryFeedbackSessions">
  <thead class="${tableHeaderClass}">
  <tr>
    <th id="btn_sortid" class="button-sort-none toggle-sort">
      Course ID<span class="icon-sort unsorted"></span>
    </th>
    <th id="btn_sortname" class="button-sort-none toggle-sort">
      Session Name<span class="icon-sort unsorted"></span>
    </th>
    <th id="btn_sortcoursecreateddate" data-toggle-sort-comparator="sortDate" data-toggle-sort-extractor="dateStampExtractor" class="button-sort-none toggle-sort">
      Creation Date<span class="icon-sort unsorted"></span>
    </th>
    <th id="btn_sortcoursedeleteddate" data-toggle-sort-comparator="sortDate" data-toggle-sort-extractor="dateStampExtractor" class="button-sort-none toggle-sort">
      Deletion Date<span class="icon-sort unsorted"></span>
    </th>
    <th class="align-center no-print">Action(s)</th>
  </tr>
  </thead>
  <c:forEach items="${recoveryFsList.rows}" var="recoverySession" varStatus="i">
    <tr>
      <td id="recoverycourseid${i.index}">${recoverySession.courseId}</td>
      <td id="recoverysessionname${i.index}">${recoverySession.sessionName}</td>
      <td
          id="recoverysessioncreateddate${i.index}"
          data-date-stamp="${recoverySession.createdTimeDateStamp}"
          data-toggle="tooltip"
          data-original-title="${recoverySession.createdTimeFullDateTimeString}">
          ${recoverySession.createdTimeDateString}
      </td>
      <td
          id="recoverysessiondeleteddate${i.index}"
          data-date-stamp="${recoverySession.deletedTimeDateStamp}"
          data-toggle="tooltip"
          data-original-title="${recoverySession.deletedTimeFullDateTimeString}">
          ${recoverySession.deletedTimeDateString}
      </td>
      <td class="align-center no-print">
        <c:forEach items="${recoverySession.actions}" var="button">
          <a ${button.attributesToString}>
              ${button.content}
          </a>
        </c:forEach>
      </td>
    </tr>
  </c:forEach>
</table>
