<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourse - Recovery courses table" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="recoveryCourses" type="teammates.ui.template.RecoveryCoursesTable" required="true" %>

<c:set var="tableHeaderClass" value="background-color-medium-gray text-color-gray font-weight-normal" />
<table class="table table-bordered table-striped margin-0" id="tableRecoveryCourses">
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
  <c:forEach items="${recoveryCourses.rows}" var="recoveryCourse" varStatus="i">
    <tr>
      <td id="recoverycourseid${i.index}">${recoveryCourse.courseId}</td>
      <td id="recoverycoursename${i.index}">${recoveryCourse.courseName}</td>
      <td
          id="recoverycoursecreateddate${i.index}"
          data-date-stamp="${recoveryCourse.createdAtDateStamp}"
          data-toggle="tooltip"
          data-original-title="${recoveryCourse.createdAtFullDateTimeString}">
          ${recoveryCourse.createdAtDateString}
      </td>
      <td
          id="recoverycoursedeleteddate${i.index}"
          data-date-stamp="${recoveryCourse.deletedAtDateStamp}"
          data-toggle="tooltip"
          data-original-title="${recoveryCourse.deletedAtFullDateTimeString}">
          ${recoveryCourse.deletedAtDateString}
      </td>
      <td class="align-center no-print">
        <c:forEach items="${recoveryCourse.actions}" var="button">
          <a ${button.attributesToString}>
              ${button.content}
          </a>
        </c:forEach>
      </td>
    </tr>
  </c:forEach>
</table>
