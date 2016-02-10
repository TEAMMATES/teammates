<%@ tag description="adminHome.jsp - Admin home create instructor account single line form" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="instructorDetailsSingleLine" required="true" %>

<div class="well well-plain">
    <div>
        <label class="label-control">Instructor Details: (Name | Email | Institution)</label>
        <textarea class="form-control addInstructorFormControl" rows="5" type="text" id="addInstructorDetailsSingleLine">${instructorDetailsSingleLine}</textarea>
    </div><br/>
    
    <div>
        <button class="btn btn-primary addInstructorFormControl addInstructorBtn" id="btnAddInstructorDetailsSingleLineForm" onclick="addInstructorFromFirstFormByAjax()">Add Instructor</button>
    </div>
</div>