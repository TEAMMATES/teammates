<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="adminEmail.jsp - Sent email table" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/email" prefix="adminEmail" %>
<%@ attribute name="sentEmailTable" required="true" type="teammates.ui.template.AdminSentEmailTable" %>

<div id="adminEmailSent">
  <div class="panel panel-success">
    <div class="panel-heading">
      <strong>
        <span id="sentEmailsCount">
          <c:choose>
            <c:when test="${sentEmailTable.numEmailsSent > 0}">
              Emails Sent: ${sentEmailTable.numEmailsSent}
            </c:when>
            <c:otherwise>
              No Sent Email
            </c:otherwise>
          </c:choose>
        </span>
      </strong>
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
          <c:forEach items="${sentEmailTable.rows}" var="email">
            <adminEmail:sentEmail sentEmail="${email}"/>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</div>
