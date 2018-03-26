<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<table class="table-responsive table table-hover table-bordered margin-0" id="copyTableModal">
  <thead class="fill-primary">
  <tr>
    <th style="width:30px;">&nbsp;</th>
    <th id="button_sortid" class="button-sort-ascending toggle-sort" style="width:100px">
      Course ID <span class="icon-sort sorted-ascending"></span>
    </th>
    <th id="button_sortinstrname" class="button-sort-none toggle-sort" style="width:17%;">
      Instructor Name <span class="icon-sort unsorted"></span>
    </th>
    <th id="button_sortinstraccesslevel" class="button-sort-none toggle-sort" style="width:17%;">
      Instructor Access Level <span class="icon-sort unsorted"></span>
    </th>
    <th id="button_sortinstrdisname" class="button-sort-none toggle-sort">
      Instructor Displayed Name <span class="icon-sort unsorted"></span>
    </th>
    <th id="button_sortinstremail" class="button-sort-none toggle-sort">
      Instructor Email <span class="icon-sort unsorted"></span>
    </th>
  </tr>
  </thead>
  <c:forEach items="${data.copyInstructorForm.instructorRows}" var="row">
    <tr style="cursor:pointer;">
      <td><input type="checkbox"></td>
      <td>${row.courseId}</td>
      <td>${row.instructorName}</td>
      <td>${row.instructorAccessLevel}</td>
      <td>${row.instructorDisplayedName}</td>
      <td>${row.instructorEmail}</td>
      <input type="hidden" class="instructoremail" value="${row.instructorEmail}">
      <input type="hidden" class="courseid" value="${row.courseId}">
    </tr>
  </c:forEach>
</table>
