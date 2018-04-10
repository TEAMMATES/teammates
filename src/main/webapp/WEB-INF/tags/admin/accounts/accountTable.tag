<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Account Table in Admin Account Management Page" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="accounts" type="java.util.Collection" required="true" %>

<div class="panel panel-primary">
  <div class="panel-heading">
    <strong>Instructor List</strong>
    <strong class="pull-right">
      <span id="currentPageEntryCount">1</span>&nbsp;/&nbsp;<span id="totalEntryCount">10</span>
    </strong>
  </div>
  <div class="table-responsive">
    <table class="table table-striped data-table">
      <thead>
        <tr>
          <th width="10%">Account Info</th>
          <th width="5%">Instructor for</th>
          <th width="20%" class="button-sort-ascending toggle-sort-and-relabel">
            Institute <span class="icon-sort unsorted" id="button_sort_institute"></span>
          </th>
          <th width="30%" class="button-sort-ascending toggle-sort-and-relabel">
            Create At <span class="icon-sort unsorted" id="button_sort_createat"></span>
          </th>
          <th width="5%">Options</th>
        </tr>
      </thead>

      <tbody>

        <c:forEach items="${accounts}" var="row">
          <tr class="accountEntry">
            <td>
              <span class="bold">Google ID: </span>
              <a href="${row.instructorHomePageViewLink}" target="_blank" rel="noopener noreferrer">${row.account.googleId}</a>
              <br>
              <span class="bold">Name: </span>${row.account.name}
              <br>
              <span class="bold">Email: </span>${row.account.email}
            </td>
            <td id="courses_${row.account.googleId}">
              <c:choose>
                <c:when test="${not empty row.instructorList}">
                  Total Courses: ${fn:length(row.instructorList)}<br>
                  <c:forEach items="${row.instructorList}" var="instructor">
                    --- ${instructor.courseId}<br>
                  </c:forEach>
                </c:when>
                <c:otherwise>
                  No Courses found
                </c:otherwise>
              </c:choose>
            </td>
            <td id="${row.account.googleId}_institude">${row.account.institute}</td>
            <td id="${row.account.googleId}_createAt">${row.createdAt}</td>
            <td>
              <a class="btn btn-link btn-xs" id="${row.account.googleId}_details"
                  href="${row.adminViewAccountDetailsLink}" target="_blank" rel="noopener noreferrer">
                <span class="glyphicon glyphicon-info-sign"></span> View Details
              </a>
              &nbsp;&nbsp;&nbsp;&nbsp;
              <a class="btn btn-link btn-xs" id="${row.account.googleId}_delete"
                  href="${row.adminDeleteInstructorStatusLink}" role="button">
                <span class="glyphicon glyphicon-remove"></span> Delete Instructor Status
              </a>
              <a class="admin-delete-account-link btn btn-link btn-xs" id="${row.account.googleId}_deleteAccount"
                  href="${row.adminDeleteAccountLink}" data-google-id="${row.account.googleId}">
                <span class="glyphicon glyphicon-trash"></span> Delete Entire Account
              </a>

              <form method="post" target="_blank" action="<%=Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE%>">
                <button type="submit"  id="${row.account.googleId}_recentActions" class="btn btn-link btn-xs">
                  <span class="glyphicon glyphicon-zoom-in"></span>
                  View Recent Actions
                </button>
                <input type="hidden" name="filterQuery" value="person:${row.account.googleId}">
              </form>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
</div>
