<%@ tag description="instructorCourseEdit - Instructor Role Modal" %>
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
                                value="true" checked="checked" disabled="disabled" /> Edit/Delete Course
                    </div>
                    
                    <div class="col-sm-6">
                        <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR%>"
                                value="true" checked="checked" disabled="disabled" /> Add/Edit/Delete Instructors
                    </div>
                    
                    <div class="col-sm-6">
                        <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION%>"
                                value="true" checked="checked" disabled="disabled" /> Create/Edit/Delete Sessions
                    </div>
                    
                    <div class="col-sm-6">
                        <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT%>"
                                value="true" checked="checked" disabled="disabled" /> Enroll/Edit/Delete Students
                    </div>
                    
                    <div class="col-sm-6">
                        <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS%>"
                                value="true" checked="checked" disabled="disabled" /> View Students' Details<br>
                        <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS%>"
                                value="true" checked="checked" disabled="disabled" /> Give Comments for Students<br>
                        <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS%>"
                                value="true" checked="checked" disabled="disabled" /> View Others' Comments on Students<br>
                        <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS%>"
                                value="true" checked="checked" disabled="disabled" /> Edit/Delete Others' Comments on Students<br>
                    </div>
                    
                    <div class="col-sm-6">
                        <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS%>"
                                value="true" checked="checked" disabled="disabled" /> Sessions: Submit Responses and Add Comments<br>
                        <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS%>"
                                value="true" checked="checked" disabled="disabled" /> Sessions: View Responses and Comments<br>
                        <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS%>"
                                value="true" checked="checked" disabled="disabled" /> Sessions: Edit/Delete Responses/Comments by others<br>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>