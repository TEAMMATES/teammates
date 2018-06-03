<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseStudentDetails / instructorStudentRecords - Student Information" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="studentInfoTable" type="teammates.ui.template.StudentInfoTable" required="true" %>
<%@ tag import="teammates.common.util.Const" %>
<div class="well well-plain">
  <div class="form form-horizontal" id="studentInfomationTable">
    <div class="form-group student-info-row">
      <div class="col-sm-3 text-right">
        <label>Course</label>
      </div>
      <div class="col-sm-9" id="<%=Const.ParamsNames.STUDENT_NAME%>">
        <a href="${studentInfoTable.courseDetailsLink}">
          <span>${fn:escapeXml(studentInfoTable.courseId)}</span>
        </a>
      </div>
    </div>
    <div class="form-group student-info-row">
      <div class="col-sm-3 text-right">
        <label>Student Name</label>
      </div>
      <div class="col-sm-9" id="<%=Const.ParamsNames.STUDENT_NAME%>">
        <span>${fn:escapeXml(studentInfoTable.name)}</span>
      </div>
    </div>
    <div class="form-group student-info-row">
      <div class="col-sm-3 text-right">
        <label>Section Name</label>
      </div>
      <div class="col-sm-9" id="<%= Const.ParamsNames.SECTION_NAME %>">
        <span>${fn:escapeXml(studentInfoTable.section)}</span>
      </div>
    </div>
    <div class="form-group student-info-row">
      <div class="col-sm-3 text-right">
        <label>Team Name</label>
      </div>
      <div class="col-sm-9" id="<%= Const.ParamsNames.TEAM_NAME %>">
        <span>${fn:escapeXml(studentInfoTable.team)}</span>
      </div>
    </div>
    <div class="form-group student-info-row">
      <div class="col-sm-3 text-right">
        <label>Official Email</label>
      </div>
      <div class="col-sm-9" id="<%= Const.ParamsNames.STUDENT_EMAIL %>">
        <span>${fn:escapeXml(studentInfoTable.email)}</span>
      </div>
    </div>
    <div class="form-group student-info-row">
      <div class="col-sm-3 text-right">
        <label>Comments</label>
      </div>
      <div class="col-sm-9" id="<%= Const.ParamsNames.COMMENTS %>">
        <span>${fn:escapeXml(studentInfoTable.comments)}</span>
      </div>
    </div>
  </div>
</div>
