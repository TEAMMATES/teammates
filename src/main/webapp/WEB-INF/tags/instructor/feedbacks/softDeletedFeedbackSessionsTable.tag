<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - Soft-deleted feedback sessions table/list" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="softDeletedFsList" type="teammates.ui.template.SoftDeletedFeedbackSessionsTable" required="true" %>

<c:set var="tableHeaderClass" value="background-color-medium-gray text-color-gray font-weight-normal" />
<table class="table table-bordered table-striped margin-0" id="tableSoftDeletedFeedbackSessions">
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
  <c:forEach items="${softDeletedFsList.rows}" var="softDeletedSession" varStatus="i">
    <tr>
      <td id="softdeletedcourseid${i.index}">${softDeletedSession.courseId}</td>
      <td id="softdeletedsessionname${i.index}">${softDeletedSession.sessionName}</td>
      <td
          id="softdeletedsessioncreateddate${i.index}"
          data-date-stamp="${softDeletedSession.createdTimeDateStamp}"
          data-toggle="tooltip"
          data-original-title="${softDeletedSession.createdTimeFullDateTimeString}">
          ${softDeletedSession.createdTimeDateString}
      </td>
      <td
          id="softdeletedsessiondeleteddate${i.index}"
          data-date-stamp="${softDeletedSession.deletedTimeDateStamp}"
          data-toggle="tooltip"
          data-original-title="${softDeletedSession.deletedTimeFullDateTimeString}">
          ${softDeletedSession.deletedTimeDateString}
      </td>
      <td class="align-center no-print">
        <c:forEach items="${softDeletedSession.actions}" var="button">
          <a ${button.attributesToString}>
              ${button.content}
          </a>
        </c:forEach>
      </td>
    </tr>
  </c:forEach>
</table>
