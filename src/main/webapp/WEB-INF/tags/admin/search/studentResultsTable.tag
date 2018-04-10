<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="adminSearch.jsp - student results table" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/search" prefix="adminSearch" %>
<%@ attribute name="studentResultsTable" type="teammates.ui.template.AdminSearchStudentTable" required="true" %>

<div class="panel panel-primary">
  <div class="panel-heading">
    <strong>Students Found </strong>
    <span class="pull-right">
      <button class="btn btn-primary btn-xs" type="button" id="btn-disclose-all-students">Disclose All</button>
      <button class="btn btn-primary btn-xs" type="button" id="btn-collapse-all-students">Collapse All</button>
    </span>
  </div>

  <div class="table-responsive">
    <table class="table table-striped data-table" id="search_table">
      <thead>
        <tr>
          <th>Institute </th>
          <th>Course[Section](Team)</th>
          <th>Name</th>
          <th>Google ID[Details]</th>
          <th>Comments</th>
          <th>Options</th>

        </tr>
      </thead>

      <tbody>
        <c:forEach items="${studentResultsTable.studentRows}" var="student">
          <adminSearch:studentRow student="${student}"/>
        </c:forEach>
      </tbody>
    </table>
  </div>
</div>
