<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="adminHome.jsp - Admin home create instructor account form" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="instructorName" required="true" %>
<%@ attribute name="instructorEmail" required="true" %>
<%@ attribute name="instructorInstitution" required="true" %>

<div class="well well-plain">
  <div>
    <label class="label-control">Adding a Single Instructor</label>
  </div>
  <br>
  <div>
    <label class="label-control">Name:</label>
    <input class="form-control addInstructorFormControl" type="text" id="instructorName" value="${instructorName}">
  </div>
  <br>
  <div>
    <label class="label-control">Email: </label>
    <input class="form-control addInstructorFormControl" type="text" id="instructorEmail" value="${instructorEmail}">
  </div>
  <br>
  <div>
    <label class="label-control">Institution: </label>
    <input class="form-control addInstructorFormControl" type="text" id="instructorInstitution" value="${instructorInstitution}">
  </div>
  <br>

  <div>
    <button class="btn btn-primary addInstructorFormControl addInstructorBtn" id="btnAddInstructor">Add Instructor</button>
  </div>
</div>
