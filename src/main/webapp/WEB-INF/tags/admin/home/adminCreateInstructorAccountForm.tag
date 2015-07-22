<%@ tag description="adminHome.jsp - Admin home create instructor account form" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="instructorShortName" required="true" %>
<%@ attribute name="instructorName" required="true" %>
<%@ attribute name="instructorEmail" required="true" %>
<%@ attribute name="instructorInstitution" required="true" %>

<div class="well well-plain">
    <form method="post" action="<%=Const.ActionURIs.ADMIN_INSTRUCTORACCOUNT_ADD%>" name="form_addinstructoraccount">
        <div>
            <label class="label-control">Short Name:</label>
            <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_SHORT_NAME%>" value="${instructorShortName}">
        </div><br/>
        <div>
            <label class="label-control">Name:</label>
            <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_NAME%>" value="${instructorName}">
        </div><br/>
        <div>
            <label class="label-control">Email: </label>
            <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>" value="${instructorEmail}">
        </div><br/>
        <div>
            <label class="label-control">Institution: </label>
            <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_INSTITUTION%>" value="${instructorInstitution}">
        </div><br/>
        
        <div>
            <input id="btnAddInstructor" class="btn btn-primary" type="submit" value="Add Instructor">
        </div>
    </form>
</div>