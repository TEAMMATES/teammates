<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseStudentDetailsEdit - Student Information with Editable Fields" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ attribute name="studentInfoTable" type="teammates.ui.template.StudentInfoTable" required="true" %>
<%@ attribute name="newEmail" required="true" %>
<%@ attribute name="openOrPublishedEmailSentForTheCourse" required="true" %>
<%@ attribute name="sessionToken" required="true" %>
<%@ tag import="teammates.common.util.Const" %>
<div class="panel panel-primary">
  <div class="panel-body fill-plain">
    <form action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_SAVE%>" method="post" id="instructor-student-edit-form" class="form form-horizontal">
      <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${studentInfoTable.course}">
      <input type="hidden" name="<%=Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK %>" value="false">
      <input type="hidden" id="<%=Const.ParamsNames.OPEN_OR_PUBLISHED_EMAIL_SEND_CHECK %>" name="openorpublishedemailsent" value="${openOrPublishedEmailSentForTheCourse}">
      <input type="hidden" name="<%=Const.ParamsNames.SESSION_TOKEN%>" value="${sessionToken}">
      <div class="form-group">
        <label class="col-sm-1 control-label">Student Name:</label>
        <div class="col-sm-11">
          <input class="form-control" name="<%=Const.ParamsNames.STUDENT_NAME%>"
              id="<%=Const.ParamsNames.STUDENT_NAME%>"
              value="${fn:escapeXml(studentInfoTable.name)}">
        </div>
      </div>
      <c:if test="${studentInfoTable.hasSection}">
        <div class="form-group">
          <label class="col-sm-1 control-label">Section Name:</label>
          <div class="col-sm-11">
            <input class="form-control" name="<%=Const.ParamsNames.SECTION_NAME%>"
                id="<%=Const.ParamsNames.SECTION_NAME%>"
                value="${fn:escapeXml(studentInfoTable.section)}">
          </div>
        </div>
      </c:if>
      <div class="form-group">
        <label class="col-sm-1 control-label">Team Name:</label>
        <div class="col-sm-11">
          <input class="form-control" name="<%=Const.ParamsNames.TEAM_NAME%>"
              id="<%=Const.ParamsNames.TEAM_NAME%>"
              value="${fn:escapeXml(studentInfoTable.team)}">
        </div>
      </div>
      <div class="form-group">
        <label class="col-sm-1 control-label">
          E-mail Address:
          <input type="hidden" name="<%=Const.ParamsNames.STUDENT_EMAIL%>"
              id="<%=Const.ParamsNames.STUDENT_EMAIL%>"
              value="${studentInfoTable.email}">
        </label>
        <div class="col-sm-11">
          <input class="form-control" name="<%=Const.ParamsNames.NEW_STUDENT_EMAIL%>"
              id="<%=Const.ParamsNames.NEW_STUDENT_EMAIL%>"
              value="${newEmail}">
        </div>
      </div>
      <div class="form-group">
        <label class="col-sm-1 control-label">Comments:</label>
        <div class="col-sm-11">
          <textarea class="form-control" rows="6" name="<%=Const.ParamsNames.COMMENTS%>"
              id="<%=Const.ParamsNames.COMMENTS%>"><c:out value="${studentInfoTable.comments}"/></textarea>
        </div>
      </div>
      <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
      <br>
      <div class="align-center">
        <input type="submit" class="btn btn-primary" id="button_submit" value="Save Changes">
      </div>
      <br>
      <br>
      <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
    </form>
  </div>
</div>
