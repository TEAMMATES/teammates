<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseDetails - Course Information" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseDetails" type="teammates.common.datatransfer.CourseDetailsBundle" required="true" %>
<%@ attribute name="instructors" type="java.util.Collection" required="true" %>

<div class="form-group">
  <label class="col-sm-3 control-label">Course ID:</label>
  <div class="col-sm-6" id="courseid">
    <p class="form-control-static">${courseDetails.course.id}</p>
  </div>
</div>

<div class="form-group">
  <label class="col-sm-3 control-label">Course name:</label>
  <div class="col-sm-6" id="coursename">
    <p class="form-control-static">${fn:escapeXml(courseDetails.course.name)}</p>
  </div>
</div>

<div class="form-group">
  <label class="col-sm-3 control-label">Sections:</label>
  <div class="col-sm-6" id="total_sections">
    <p class="form-control-static">${courseDetails.stats.sectionsTotal}</p>
  </div>
</div>

<div class="form-group">
  <label class="col-sm-3 control-label">Teams:</label>
  <div class="col-sm-6" id="total_teams">
    <p class="form-control-static">${courseDetails.stats.teamsTotal}</p>
  </div>
</div>

<div class="form-group">
  <label class="col-sm-3 control-label">Total students:</label>
  <div class="col-sm-6" id="total_students">
    <p class="form-control-static">${courseDetails.stats.studentsTotal}</p>
  </div>
</div>

<div class="form-group">
  <label class="col-sm-3 control-label">Instructors:</label>
  <div class="col-sm-6" id="instructors">
    <div class="form-control-static">
      <c:forEach items="${instructors}" var="instructor" varStatus="i">
        <c:choose>
          <c:when test="${empty instructor.role}">
            <%= Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER %>: ${instructor.name} (${instructor.email})
          </c:when>
          <c:otherwise>
            ${instructor.role}: ${instructor.name} (${instructor.email})
          </c:otherwise>
        </c:choose>
        <br>
        <br>
      </c:forEach>
    </div>
  </div>
</div>
