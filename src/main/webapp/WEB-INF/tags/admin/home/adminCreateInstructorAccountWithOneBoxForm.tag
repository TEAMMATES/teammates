<%@ tag description="adminHome.jsp - Admin home create instructor account form" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="instructorDetails" required="true" %>

<div class="well well-plain">
    <form method="post" action="<%=Const.ActionURIs.ADMIN_INSTRUCTORACCOUNT_ADD%>" name="form_addinstructoraccount">
        <div>
            <label class="label-control">Instructor Details:</label>
            <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_DETAILS%>" value="${instructorDetails}">
        </div><br/>
        
        <div>
            <input id="btnAddInstructorDetailsForm" class="btn btn-primary" type="submit" value="Add Instructor">
        </div>
    </form>
</div>