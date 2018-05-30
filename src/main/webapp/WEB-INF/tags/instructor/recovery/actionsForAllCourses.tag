<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorRecovery - Restore and delete all courses buttons" pageEncoding="UTF-8" %>
<div class="row" style="text-align: right">
  <a class="btn btn-info btn-md" id="restoreAllCourses" href="${data.instructorRecoveryRestoreAllLink}">
    Restore All Courses
  </a>
  &nbsp;&nbsp;
  <a class="btn btn-danger btn-md course-delete-all-link" id="deleteAllCourses" href="${data.instructorRecoveryDeleteAllLink}">
    Delete All Courses
  </a>
  &nbsp;&nbsp;&nbsp;
</div>
