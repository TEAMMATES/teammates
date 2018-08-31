<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourse - Soft-deleted courses table" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="softDeletedCourses" type="teammates.ui.template.SoftDeletedCoursesTable" required="true" %>

<c:set var="tableHeaderClass" value="background-color-medium-gray text-color-gray font-weight-normal" />
<table class="table table-bordered table-striped margin-0" id="tableSoftDeletedCourses">
  <thead class="${tableHeaderClass}">
    <tr>
      <th id="btn_sortid" class="button-sort-none toggle-sort">
        Course ID<span class="icon-sort unsorted"></span>
      </th>
      <th id="btn_sortname" class="button-sort-none toggle-sort">
        Course Name<span class="icon-sort unsorted"></span>
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
  <c:forEach items="${softDeletedCourses.rows}" var="softDeletedCourse" varStatus="i">
    <tr>
      <td id="softdeletedcourseid${i.index}">${softDeletedCourse.courseId}</td>
      <td id="softdeletedcoursename${i.index}">${softDeletedCourse.courseName}</td>
      <td
          id="softdeletedcoursecreateddate${i.index}"
          data-date-stamp="${softDeletedCourse.createdAtDateStamp}"
          data-toggle="tooltip"
          data-original-title="${softDeletedCourse.createdAtFullDateTimeString}">
          ${softDeletedCourse.createdAtDateString}
      </td>
      <td
          id="softdeletedcoursedeleteddate${i.index}"
          data-date-stamp="${softDeletedCourse.deletedAtDateStamp}"
          data-toggle="tooltip"
          data-original-title="${softDeletedCourse.deletedAtFullDateTimeString}">
          ${softDeletedCourse.deletedAtDateString}
      </td>
      <td class="align-center no-print">
        <c:forEach items="${softDeletedCourse.actions}" var="button">
          <a ${button.attributesToString}>
              ${button.content}
          </a>
        </c:forEach>
      </td>
    </tr>
  </c:forEach>
</table>
