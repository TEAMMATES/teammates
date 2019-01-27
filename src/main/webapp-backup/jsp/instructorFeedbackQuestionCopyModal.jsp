<%@ page trimDirectiveWhitespaces="true" %>
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
      <th id="button_sortfsname" class="button-sort-none toggle-sort" style="width:17%;">
        Session Name <span class="icon-sort unsorted"></span>
      </th>
      <th id="button_sortfqtype" class="button-sort-none toggle-sort">
        Question Type <span class="icon-sort unsorted"></span>
      </th>
      <th id="button_sortfqtext" class="button-sort-none toggle-sort">
        Question Text <span class="icon-sort unsorted"></span>
      </th>
    </tr>
  </thead>
  <c:forEach items="${data.copyQnForm.questionRows}" var="row">
    <tr style="cursor:pointer;">
      <td><input type="checkbox"></td>
      <td>${row.courseId}</td>
      <td>${row.fsName}</td>
      <td>${row.qnType}</td>
      <td>${fn:escapeXml(row.qnText)}</td>
      <input type="hidden" value="${row.qnId}">
      <input type="hidden" class="courseid" value="${row.courseId}">
      <input type="hidden" class="fsname" value="${row.fsName}">
    </tr>
  </c:forEach>
</table>
