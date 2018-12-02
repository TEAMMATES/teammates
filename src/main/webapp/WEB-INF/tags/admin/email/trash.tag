<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="adminEmail.jsp - Trash email table" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/email" prefix="adminEmail" %>
<%@ attribute name="trashEmailTable" required="true" type="teammates.ui.template.AdminTrashEmailTable" %>

<div id="adminEmailTrash">
  <div class="panel panel-danger">
    <div class="panel-heading">
      <strong>
        <span id="trashEmailsCount">
          <c:choose>
            <c:when test="${trashEmailTable.numEmailsTrash > 0}">
              Trash Emails: ${trashEmailTable.numEmailsTrash}
            </c:when>
            <c:otherwise>
              No Trash Email
            </c:otherwise>
          </c:choose>
        </span>
      </strong>
      <%-- Empty trash button --%>
      <span class="pull-right">
        <a ${trashEmailTable.emptyTrashButton.attributesToString}>

          <strong>
            <span class="glyphicon glyphicon-floppy-remove">
            </span>&nbsp;Empty Trash
          </strong>
        </a>
      </span>
    </div>
    <div class="table-responsive">
      <table class="table table-hover">
        <thead>
          <tr>
            <th>Action</th>
            <th>Address Receiver</th>
            <th>Group Receiver</th>
            <th>Subject</th>
            <th class="button-sort-ascending toggle-sort">
              Date <span class="icon-sort unsorted" id="button_sort_date"></span>
            </th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${trashEmailTable.rows}" var="email">
            <adminEmail:trashEmail trashEmail="${email}"/>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</div>
