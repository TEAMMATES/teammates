<%@ tag description="adminHome.jsp - Admin home create instructor account single line form" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="instructorDetailsSingleLine" required="true" %>

<div class="well well-plain">
    <div>
        <label class="label-control">Adding Multiple Instructors</label>
        <div class="text-muted">
            <span class="glyphicon glyphicon-exclamation-sign glyphicon-primary"></span> Add Instructor Details in the format (Name | Email | Institution)
        </div>
        <br>
        <textarea class="form-control addInstructorFormControl" rows="5" type="text" id="addInstructorDetailsSingleLine">${instructorDetailsSingleLine}</textarea>
    </div><br/>
    
    <div>
        <button class="btn btn-primary addInstructorFormControl addInstructorBtn" id="btnAddInstructorDetailsSingleLineForm" onclick="addInstructorFromFirstFormByAjax()">Add Instructor</button>
    </div>
</div>