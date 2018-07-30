<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseEdit - Panel Heading of Instructor List" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ attribute name="instructorPanel" type="teammates.ui.template.CourseEditInstructorPanel" required="true" %>

<div id="accessControlEditDivForInstr${instructorPanel.index}">
  <c:if test="${instructorPanel.accessControlDisplayed}">
    <div class="form-group">
      <div class="col-sm-3">
        <label class="control-label pull-right">Access-level</label>
      </div>

      <div class="col-sm-9">
        <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>"
            id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor${instructorPanel.index}"
            value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER%>">
        &nbsp;Co-owner: Can do everything&nbsp;
        <a href="javascript:;" class="view-role-details" data-role="<%= Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER %>">
          View Details
        </a>
        <br>

        <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>"
            id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor${instructorPanel.index}"
            value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER%>">
        &nbsp;Manager: Can do everything except for deleting/restoring the course&nbsp;
        <a href="javascript:;" class="view-role-details" data-role="<%= Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER %>">
          View Details
        </a>
        <br>

        <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>"
            id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor${instructorPanel.index}"
            value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER%>">
        &nbsp;Observer: Can only view information(students, submissions, comments etc.).&nbsp;Cannot edit/delete/submit anything.&nbsp;
        <a href="javascript:;" class="view-role-details" data-role="<%= Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER %>">
          View Details
        </a>
        <br>

        <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>"
            id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor${instructorPanel.index}"
            value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR%>">
        &nbsp;Tutor: Can view student details, give/view comments, submit/view responses for sessions&nbsp;
        <a href="javascript:;" class="view-role-details" data-role="<%= Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR %>">
          View Details
        </a>
        <br>

        <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>"
            id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor${instructorPanel.index}"
            value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM%>">
        &nbsp;Custom: No access by default. Any access needs to be granted explicitly.
        <br>
      </div>
    </div>

    <course:courseEditTunePermissionsDivForInstructor instructorPanel="${instructorPanel}"/>
  </c:if>
</div>
