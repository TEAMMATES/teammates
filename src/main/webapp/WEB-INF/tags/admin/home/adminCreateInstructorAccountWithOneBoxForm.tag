<%@ tag description="adminHome.jsp - Admin home create instructor account single line form" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="instructorDetailsSingleLine" required="true" %>

<div class="well well-plain">
    <form method="post" action="<%=Const.ActionURIs.ADMIN_INSTRUCTORACCOUNT_ADD%>" name="form_addinstructoraccount">
        <div>
            <label class="label-control">Instructor Details:</label>
            <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_DETAILS_SINGLE_LINE%>" value="${instructorDetailsSingleLine}">
        </div><br/>
        
        <div>
            <input id="btnAddInstructorDetailsSingleLineForm" class="btn btn-primary" type="submit" value="Add Instructor">
        </div>
    </form>
</div>