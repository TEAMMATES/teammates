<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorStudentList - Student filter box" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="filterBox" type="teammates.ui.template.InstructorStudentListFilterBox" required="true" %>
<div id="moreOptionsDiv" class="well well-plain"<c:if test="${empty filterBox.courses}"> style="display:none;"</c:if>>
  <div class="row">
    <div class="col-md-3">
      <div class="checkbox">
        <input id="displayArchivedCourses_check" type="checkbox"<c:if test="${filterBox.displayArchive}"> checked=""</c:if>>
        <label for="displayArchivedCourses_check">Display Archived Courses</label>
      </div>
    </div>
  </div>
  <form class="form-horizontal" role="form">
    <div class="row">
      <div class="col-sm-3">
        <div class="text-color-primary">
          <strong>Courses</strong>
        </div>
        <br>
        <div class="checkbox">
          <input type="checkbox" value="" id="course_all">
          <label for="course_all"><strong>Select all</strong></label>
        </div>
        <br>
        <c:forEach items="${filterBox.courses}" var="course" varStatus="i">
          <div class="checkbox">
            <input id="course_check-${i.index}" type="checkbox">
            <label for="course_check-${i.index}">
              [${course.courseId}] : ${fn:escapeXml(course.courseName)}
            </label>
          </div>
        </c:forEach>
      </div>
      <div class="col-sm-3">
        <div class="text-color-primary">
          <strong>Sections</strong>
        </div>
        <br>
        <div class="checkbox" style="display:none;">
          <input type="checkbox" value="" id="section_all">
          <label for="section_all"><strong>Select all</strong></label>
        </div>
        <br>
        <div id="sectionChoices">
        </div>
      </div>
      <div class="col-sm-3">
        <div class="text-color-primary">
          <strong>Teams</strong>
        </div>
        <br>
        <div class="checkbox" style="display:none;">
          <input id="team_all" type="checkbox">
          <label for="team_all"><strong>Select All</strong></label>
        </div>
        <br>
        <div id="teamChoices">
        </div>
      </div>
      <div class="col-sm-3">
        <div class="text-color-primary">
          <strong>Emails</strong>
        </div>
        <br>
        <div class="checkbox" style="display:none;">
          <input id="show_email" type="checkbox" checked>
          <label for="show_email"><strong>Show Emails</strong></label>
          <button id="copy-email-button" class="btn btn-default btn-xs">Copy Emails</button>
        </div>
        <br>
        <div id="emails">
        </div>
      </div>
    </div>
  </form>
</div>
