<%@ tag description="instructorStudentList - Students table per course" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="course" type="teammates.ui.template.InstructorStudentListStudentsTableCourse" required="true" %>
<%@ attribute name="index" required="true" %>
<c:set var="PANEL_TYPE" value="${course.courseArchived ? 'panel-default' : 'panel-info'}" />
<div class="panel ${PANEL_TYPE}">
  <div class="panel-heading ajax_submit">
    <form style="display:none;"
        id="seeMore-${index}"
        class="seeMoreForm-${index}"
        action="<%= Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_AJAX_PAGE %>">
      <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${course.courseId}">
      <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${course.googleId}">
      <input type="hidden" id="numStudents-${index}" value="0">
    </form>
    <div class="pull-right margin-left-7px">
      <span class="glyphicon glyphicon-chevron-down"></span>
    </div>
    <a class="btn btn-info btn-xs pull-right pull-down course-enroll-for-test<c:if test="${not course.instructorAllowedToModify}"> disabled</c:if>"
        id="enroll-${index}"
        href="${course.instructorAllowedToModify ? course.instructorCourseEnrollLink : 'javascript:;'}"
        title="<%= Const.Tooltips.COURSE_ENROLL %>"
        data-toggle="tooltip"
        data-placement="top">
      <span class="glyphicon glyphicon-list"></span> Enroll
    </a>
    <div class='display-icon pull-right'>
    </div>
    <strong>[${course.courseId}] : </strong>${fn:escapeXml(course.courseName)}
  </div>
  <div class="panel-collapse collapse">
    <div class="panel-body padding-0">
    </div>
  </div>
</div>
