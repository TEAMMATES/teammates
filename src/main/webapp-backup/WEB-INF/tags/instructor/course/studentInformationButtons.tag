<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseDetails - Student Information Helper" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseDetails" type="teammates.common.datatransfer.CourseDetailsBundle" required="true" %>
<%@ attribute name="courseRemindButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="courseDeleteAllButton" type="teammates.ui.template.ElementTag" required="true" %>

<div class="form-group">
  <div class="align-center">
    <button type="submit" tabindex="1" value="Remind Students to Join" ${courseRemindButton.attributesToString}>
      <span class="glyphicon glyphicon-envelope"></span>
      Remind Students to Join
    </button>

    <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_LIST_DOWNLOAD%>" style="display:inline;">
      <button id="button_download" type="submit" class="btn btn-primary" value="Delete All Students"
          name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>">
        <span class="glyphicon glyphicon-download-alt"></span>
        Download Student List
      </button>
      <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
      <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${courseDetails.course.id}">
    </form>

    <button type="submit" value="Delete All Students" ${courseDeleteAllButton.attributesToString}>
      <span class="glyphicon glyphicon-trash"></span>
      Delete All Students
    </button>
    <div>
      <span class="help-block">
        Non-English characters not displayed properly in the downloaded file?
        <span class="btn-link" data-toggle="modal" data-target="#studentTableWindow" id="btn-display-table">
          Click here.
        </span>
      </span>
    </div>

    <form id="csvToHtmlForm">
      <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${courseDetails.course.id}">
      <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
      <input type="hidden" name="<%=Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED%>" value="true">
    </form>

    <course:studentTableModal />
  </div>
</div>
