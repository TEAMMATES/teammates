<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourse - Course table" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="archivedCourses" type="teammates.ui.template.ArchivedCoursesTable" required="true" %>
<%@ attribute name="activeCourses" type="teammates.ui.template.ActiveCoursesTable" required="true" %>

<h2 class="text-muted">
  <span class="glyphicon glyphicon-floppy-disk"></span> Archived courses
</h2>
<table class="table table-bordered table-striped" id="tableArchivedCourses">
  <thead>
    <tr class="fill-info">
      <th id="button_sortid" class="button-sort-none toggle-sort">
        Course ID<span class="icon-sort unsorted"></span>
      </th>
      <th id="button_sortname" class="button-sort-none toggle-sort">
        Course Name<span class="icon-sort unsorted"></span>
      </th>
      <th id="button_sortcoursecreateddate" data-toggle-sort-comparator="sortDate" data-toggle-sort-extractor="dateStampExtractor" class="button-sort-none toggle-sort">
        Creation Date<span class="icon-sort unsorted"></span>
      </th>
      <th class="align-center no-print">Action(s)</th>
    </tr>
  </thead>
  <c:forEach items="${archivedCourses.rows}" var="archivedCourse" varStatus="i">
    <tr>
      <td id="courseid${i.index + fn:length(activeCourses.rows)}">${archivedCourse.courseId}</td>
      <td id="coursename${i.index + fn:length(activeCourses.rows)}">${archivedCourse.courseName}</td>
      <td
        id="coursecreateddate${i.index + fn:length(activeCourses.rows)}"
        data-date-stamp="${archivedCourse.createdAtDateStamp}"
        data-toggle="tooltip"
        data-original-title="${archivedCourse.createdAtFullDateTimeString}">
          ${archivedCourse.createdAtDateString}
        </td>
      <td class="align-center no-print">
        <c:forEach items="${archivedCourse.actions}" var="button">
          <a ${button.attributesToString}>
            ${button.content}
          </a>
        </c:forEach>
      </td>
    </tr>
  </c:forEach>
</table>
