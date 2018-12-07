<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseStudentDetails / instructorStudentRecords - Student Information" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="studentInfoTable" type="teammates.ui.template.StudentInfoTable" required="true" %>
<%@ tag import="teammates.common.util.Const" %>
<div class="panel panel-primary">
  <div class="panel-body fill-plain">
    <div class="form form-horizontal" id="studentInfomationTable">
      <div class="form-group">
        <div class="col-sm-12">
          <h4 class="text-bold">Enrollment Details</h4>
        </div>
      </div>
      <div class="form-group student-info-row">
        <div class="col-xs-4 text-right">
          <label>Course</label>
        </div>
        <div class="col-xs-8" id="<%=Const.ParamsNames.COURSE_ID%>">
          <a href="${studentInfoTable.courseDetailsLink}">
            <span>${fn:escapeXml(studentInfoTable.courseId)}</span>
          </a>
        </div>
      </div>
      <c:if test="${studentInfoTable.hasSection}">
        <div class="form-group student-info-row">
          <div class="col-xs-4 text-right">
            <label>Section Name</label>
          </div>
          <div class="col-xs-8" id="<%= Const.ParamsNames.SECTION_NAME %>">
            <span>${fn:escapeXml(studentInfoTable.section)}</span>
          </div>
        </div>
      </c:if>
      <div class="form-group student-info-row">
        <div class="col-xs-4 text-right">
          <label>Team Name</label>
        </div>
        <div class="col-xs-8" id="<%= Const.ParamsNames.TEAM_NAME %>">
          <span>${fn:escapeXml(studentInfoTable.team)}</span>
        </div>
      </div>
      <div class="form-group student-info-row">
        <div class="col-xs-4 text-right">
          <label>Official Email</label>
        </div>
        <div class="col-xs-8" id="<%= Const.ParamsNames.STUDENT_EMAIL %>">
          <span>${fn:escapeXml(studentInfoTable.email)}</span>
        </div>
      </div>
      <div class="form-group student-info-row">
        <div class="col-xs-4 text-right">
          <label>Comments</label>
        </div>
        <div class="col-xs-8" id="<%= Const.ParamsNames.COMMENTS %>">
          <span>${fn:escapeXml(studentInfoTable.comments)}</span>
        </div>
      </div>
    </div>
  </div>
</div>
