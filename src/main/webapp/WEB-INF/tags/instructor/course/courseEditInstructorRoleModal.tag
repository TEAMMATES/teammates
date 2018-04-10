<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseEdit - Instructor Role Modal" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>

<div class="modal fade" id="tunePermissionsDivForInstructorAll" role="dialog" aria-labelledby="instructorRoleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
        </button>
        <h4 class="model-title" id="instructorRoleModalLabel">Permissions for Co-owner</h4>
      </div>

      <div class="modal-body">
        <div class="row">
          <div class="col-sm-6">
            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE%>"
                value="true" checked disabled> Edit/Delete Course
          </div>

          <div class="col-sm-6">
            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR%>"
                value="true" checked disabled> Add/Edit/Delete Instructors
          </div>

          <div class="col-sm-6">
            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION%>"
                value="true" checked disabled> Create/Edit/Delete Sessions
          </div>

          <div class="col-sm-6">
            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT%>"
                value="true" checked disabled> Enroll/Edit/Delete Students
          </div>

          <div class="col-sm-6">
            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS%>"
                value="true" checked disabled> View Students' Details<br>
          </div>

          <div class="col-sm-6">
            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS%>"
                value="true" checked disabled> Sessions: Submit Responses and Add Comments<br>
            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS%>"
                value="true" checked disabled> Sessions: View Responses and Comments<br>
            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS%>"
                value="true" checked disabled> Sessions: Edit/Delete Responses/Comments by others<br>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
