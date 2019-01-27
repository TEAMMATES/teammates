<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseEdit - Course Info Panel" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ attribute name="editCourseButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="deleteCourseButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="course" type="teammates.common.datatransfer.attributes.CourseAttributes" required="true" %>

<div class="panel panel-primary">
  <div class="panel-heading">
    <strong>Course:</strong>

    <div class="pull-right">
      <a ${editCourseButton.attributesToString}>
        ${editCourseButton.content}
      </a>

      <a ${deleteCourseButton.attributesToString}>
        ${deleteCourseButton.content}
      </a>
    </div>
  </div>

  <div class="panel-body fill-plain">
    <form action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_SAVE%>" method="post" id="formEditcourse" class="form form-horizontal">
      <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${course.id}">
      <input type="hidden" name="<%=Const.ParamsNames.SESSION_TOKEN%>" value="${data.sessionToken}">
      <input type="hidden" name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" value="${data.account.googleId}">

      <div class="form-group">
        <label class="col-sm-3 control-label">Course ID:</label>
        <div class="col-sm-3">
          <input type="text" class="form-control"
              name="<%=Const.ParamsNames.COURSE_ID%>" id="<%=Const.ParamsNames.COURSE_ID%>"
              value="${course.id}"
              data-toggle="tooltip" data-placement="top" title="Identifier of the course, e.g.CS3215-Sem1."
              maxlength="<%=FieldValidator.COURSE_ID_MAX_LENGTH%>" tabindex="1" disabled>
        </div>
      </div>

      <div class="form-group">
        <label class="col-sm-3 control-label">Course Name:</label>
        <div class="col-sm-9">
          <input type="text" class="form-control"
              name="<%=Const.ParamsNames.COURSE_NAME%>" id="<%=Const.ParamsNames.COURSE_NAME%>"
              value="${fn:escapeXml(course.name)}"
              data-toggle="tooltip" data-placement="top" title="The name of the course, e.g. Software Engineering."
              maxlength="<%=FieldValidator.COURSE_NAME_MAX_LENGTH%>" tabindex="2" disabled>
        </div>
      </div>

      <div class="form-group">
        <label class="col-xs-12 col-sm-3 control-label">Time Zone:</label>
        <div class="col-xs-12 col-sm-9">
          <ti:timeZoneInput nameId="<%=Const.ParamsNames.COURSE_TIME_ZONE%>"
              selectedTimeZone="${course.timeZone.id}"
              tooltip="The time zone for the course. You should not need to change this as it is auto-detected based on your
                  device settings.<br><br> TEAMMATES automatically adjusts to match the current time offset in your area,
                  including clock changes due to daylight saving time."
              isDisabled="true">
          </ti:timeZoneInput>
        </div>
      </div>

      <div class="form-group">
        <div class=" col-sm-12 align-center">
          <input type="submit" class="btn btn-primary" id="btnSaveCourse" name="btnSaveCourse"
              style="display:none;" value="Save Changes">
        </div>
      </div>

      <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
    </form>
  </div>
</div>
