<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/accounts" prefix="accounts" %>

<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/adminAccountManagement.js"></script>
</c:set>

<ta:adminPage title="Instructor Account Management" jsIncludes="${jsIncludes}">
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <div id="pagination_top">
    <accounts:adminAccountManagementPagination />
  </div>

  <accounts:accountTable accounts="${data.accountTable}" />

  <div id="pagination_bottom">
    <accounts:adminAccountManagementPagination />
  </div>

  <a href="javascript:;" class="back-to-top-left"><span class="glyphicon glyphicon-arrow-up"></span>&nbsp;Top</a>
  <a href="javascript:;" class="back-to-top-right">Top&nbsp;<span class="glyphicon glyphicon-arrow-up"></span></a>

</ta:adminPage>
