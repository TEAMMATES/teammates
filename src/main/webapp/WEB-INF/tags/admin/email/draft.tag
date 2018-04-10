<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="adminEmail.jsp - Draft email table" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/email" prefix="adminEmail" %>
<%@ attribute name="draftEmailTable" required="true" type="teammates.ui.template.AdminDraftEmailTable" %>

<div id="adminEmailDraft">
  <div class="panel panel-info">
    <div class="panel-heading">
      <strong>
        <span id="draftEmailsCount">
          <c:choose>
            <c:when test="${draftEmailTable.numEmailsDraft > 0}">
              Email Drafts: ${draftEmailTable.numEmailsDraft}
            </c:when>
            <c:otherwise>
              No Email Draft
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
          <c:forEach items="${draftEmailTable.rows}" var="email">
            <adminEmail:draftEmail draftEmail="${email}"/>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</div>
