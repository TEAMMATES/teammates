<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/home" prefix="adminHome" %>

<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/adminHome.js"></script>
</c:set>

<ta:adminPage title="Add New Instructor" jsIncludes="${jsIncludes}">
  <adminHome:adminCreateInstructorAccountWithOneBoxForm/>
  <adminHome:adminCreateInstructorAccountForm
      instructorName="${data.instructorName}" instructorEmail="${data.instructorEmail}" instructorInstitution="${data.instructorInstitution}"/>

  <div class="panel panel-primary" hidden id="addInstructorResultPanel">
    <div class="panel-heading">
      <strong>Result</strong>
    </div>
    <div class="table-responsive">
      <table class="table table-striped table-hover" id="addInstructorResultTable">
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Institution</th>
            <th>Status</th>
            <th>Message</th>
          </tr>
        </thead>
        <tbody>
        </tbody>
      </table>
    </div>
  </div>
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
</ta:adminPage>
