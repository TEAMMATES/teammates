<%@ tag description="instructorStudentList - Students table per course" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="data" type="teammates.ui.template.InstructorStudentListStudentsTableCourse" required="true" %>
<%@ attribute name="index" required="true" %>
<c:choose>
    <c:when test="${data.courseArchived}">
        <c:set var="PANEL_TYPE" value="panel-default" />
    </c:when>
    <c:otherwise>
        <c:set var="PANEL_TYPE" value="panel-info" />
    </c:otherwise>
</c:choose>
<div class='panel ${PANEL_TYPE}'>
    <div class="panel-heading ajax_submit">
        <form style="display:none;"
              id="seeMore-${index}"
              class="seeMoreForm-${index}"
              action="<%= Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_AJAX_PAGE %>">
            <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${data.courseId}">
            <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.googleId}">
            <input type="hidden" id="numStudents-${index}" value="${data.numStudents}">
        </form>
        <a class="btn btn-info btn-xs pull-right pull-down course-enroll-for-test"
           id="enroll-${index}"
           href="${data.instructorCourseEnrollLink}"
           title="<%= Const.Tooltips.COURSE_ENROLL %>"
           data-toggle="tooltip"
           data-placement="top"
           <c:if test="${not data.instructorAllowedToModify}"> disabled="disabled"</c:if>>
            <span class="glyphicon glyphicon-list"></span> Enroll
        </a>
        <div class='display-icon pull-right'>
        </div>
        <strong>[${data.courseId}] : </strong>${data.courseName}
    </div>
    <div class="panel-collapse collapse">
        <div class="panel-body padding-0">
        </div>
    </div>
</div>