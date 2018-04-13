<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseEdit - Panel Heading of Instructor List" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>

<%@ attribute name="instructorPanel" type="teammates.ui.template.CourseEditInstructorPanel" required="true" %>

<div class="panel-body">
  <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_EDIT_SAVE%>"
      id="formEditInstructor${instructorPanel.index}" name="formEditInstructors" class="form form-horizontal" >
    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${instructorPanel.instructor.courseId}">
    <c:if test="${not empty instructorPanel.instructor.googleId}" >
      <input type="hidden" name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" value="${instructorPanel.instructor.googleId}">
    </c:if>
    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
    <input type="hidden" name="<%=Const.ParamsNames.SESSION_TOKEN%>" value="${data.sessionToken}">

    <div id="instructorTable${instructorPanel.index}">

      <div class="form-group">
        <label class="col-sm-3 control-label">Google ID:</label>
        <div class="col-sm-9">
          <c:choose>
            <c:when test="${not empty instructorPanel.instructor.googleId}">
              <input class="form-control immutable"
                  type="text" id="<%=Const.ParamsNames.INSTRUCTOR_ID%>${instructorPanel.index}"
                  value="${instructorPanel.instructor.googleId}"
                  maxlength="<%=FieldValidator.GOOGLE_ID_MAX_LENGTH%>" tabindex="3" disabled>
            </c:when>
            <c:otherwise>
              <p class="form-control-static">
                <span class="text-warning">
                  Not available. Instructor is yet to join this course.
                </span>
              </p>
            </c:otherwise>
          </c:choose>
        </div>
      </div>

      <div class="form-group">
        <label class="col-sm-3 control-label">Name:</label>
        <div class="col-sm-9">
          <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_NAME%>"
              id="<%=Const.ParamsNames.INSTRUCTOR_NAME%>${instructorPanel.index}" value="${instructorPanel.instructor.name}"
              data-toggle="tooltip" data-placement="top" title="Enter the name of the instructor."
              maxlength="<%=FieldValidator.PERSON_NAME_MAX_LENGTH%>" tabindex="4" disabled>
        </div>
      </div>

      <div class="form-group">
        <label class="col-sm-3 control-label">Email:</label>
        <div class="col-sm-9">
          <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>"
              id="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>${instructorPanel.index}" value="${instructorPanel.instructor.email}"
              data-toggle="tooltip" data-placement="top" title="Enter the Email of the instructor."
              maxlength="<%=FieldValidator.EMAIL_MAX_LENGTH%>" tabindex="5" disabled
              <c:if test="${empty instructorPanel.instructor.googleId}">
                readonly
              </c:if> >
        </div>
      </div>

      <div class="form-group">
        <label class="col-sm-3 control-label">
          <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT%>" value="true"
              <c:if test="${instructorPanel.instructor.displayedToStudents}">
                checked
              </c:if>
              data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.INSTRUCTOR_DISPLAYED_TO_STUDENT%>"
              disabled>
          Display to students as:
        </label>

        <div class="col-sm-9">
          <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME%>"
              <c:choose>
                <c:when test="${instructorPanel.instructor.displayedToStudents}">
                  placeholder="E.g.Co-lecturer, Teaching Assistant" value="${fn:escapeXml(instructorPanel.instructor.displayedName)}"
                </c:when>
                <c:otherwise>
                  placeholder="(This instructor will NOT be displayed to students)"
                </c:otherwise>
              </c:choose>
              data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.INSTRUCTOR_DISPLAYED_AS%>"
              disabled>
        </div>
      </div>

      <div id="accessControlInfoForInstr${instructorPanel.index}">
        <div class="form-group">
          <label class="col-sm-3 control-label">Access Level:</label>
          <div class="col-sm-9">
            <p class="form-control-static">
              <span>${fn:escapeXml(instructorPanel.instructor.role)}</span>
              <c:if test="${not instructorPanel.instructor.customRole}">
                <a href="javascript:;" class="view-role-details" data-role="${fn:escapeXml(instructorPanel.instructor.role)}">
                  &nbsp;View Details
                </a>
              </c:if>
            </p>
          </div>
        </div>
      </div>

      <course:courseEditAccessControlEditDivForInstr instructorPanel="${instructorPanel}"/>

      <div class="form-group">
        <div class="align-center">
          <input id="btnSaveInstructor${instructorPanel.index}" type="submit" class="btn btn-primary"
              style="display:none;" value="Save changes" tabindex="6">
        </div>
      </div>
    </div>
  </form>
</div>
