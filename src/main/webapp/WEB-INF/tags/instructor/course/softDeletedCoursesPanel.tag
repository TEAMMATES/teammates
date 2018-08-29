<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourse - Soft-deleted courses table panel" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<%@ tag import="teammates.common.util.Const" %>

<h2 class="text-muted">
  <span class="glyphicon glyphicon-trash"></span> Deleted courses
</h2>
<div class="panel">
  <div class="panel-heading ajax_submit fill-default">
    <div class="pull-right margin-left-7px">
      <span class="glyphicon ajax_submit glyphicon-chevron-down"></span>
    </div>
    <a class="btn btn-default btn-xs pull-right pull-down margin-left-7px course-delete-all-link color-negative<c:if test="${not data.instructorAllowedToModify}"> disabled</c:if>"
       id="btn-course-deleteall"
       href="${data.instructorCourseDeleteAllSoftDeletedCoursesLink}"
       title="<%= Const.Tooltips.COURSE_DELETE_ALL %>"
       data-toggle="tooltip"
       data-placement="top">
      <span class="glyphicon glyphicon-remove"></span>
      <strong>Delete All</strong>
    </a>
    <a class="btn btn-default btn-xs pull-right pull-down<c:if test="${not data.instructorAllowedToModify}"> disabled</c:if>"
       id="btn-course-restoreall"
       href="${data.instructorCourseRestoreAllSoftDeletedCoursesLink}"
       title="<%= Const.Tooltips.COURSE_RESTORE_ALL %>"
       data-toggle="tooltip"
       data-placement="top">
      <span class="glyphicon glyphicon-ok"></span>
      <strong>Restore All</strong>
    </a>
    <strong class="ajax_submit">
      Recycle Bin
    </strong>
  </div>
  <div class="panel-collapse collapse">
    <div class="panel-body padding-0">
      <course:softDeletedCoursesTable softDeletedCourses="${data.softDeletedCourses}"/>
    </div>
  </div>
</div>
