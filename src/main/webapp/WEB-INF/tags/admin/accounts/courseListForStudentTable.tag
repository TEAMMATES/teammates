<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Course List Table for a student in Account Details Page" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="studentCourseListTable" type="java.util.Collection" required="true" %>

<div class="panel panel-primary">
  <div class="panel-heading">
    <br>
  </div>

  <table class="table table-striped data-table">
    <thead>
      <tr>
        <th width="70%">Course</th>
        <th>Options</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach items="${studentCourseListTable}" var="row">
        <tr>
          <td>[${row.courseDetails.id}] ${row.courseDetails.name}</td>
          <td>
            <a ${row.removeFromCourseButton.attributesToString}>
              ${row.removeFromCourseButton.content}
            </a>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</div>
