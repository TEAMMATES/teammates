<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/accounts" prefix="accounts" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/administrator.js"></script>
    <script type="text/javascript" src="/js/adminAccountManagement.js"></script>

</c:set>

<ta:adminPage bodyTitle="Instructor Account Management" pageTitle="TEAMMATES - Administrator Account Management" jsIncludes="${jsIncludes}">    <t:statusMessage/>
    <div id="pagination_top">
        <accounts:adminAccountManagementPagination />
    </div>

    <accounts:accountTable accounts="${data.accountTable}" />

    <div id="pagination_bottom">
        <accounts:adminAccountManagementPagination />
    </div>

    <a href="#" class="back-to-top-left"><span class="glyphicon glyphicon-arrow-up"></span>&nbsp;Top</a> 
    <a href="#" class="back-to-top-right">Top&nbsp;<span class="glyphicon glyphicon-arrow-up"></span></a>

</ta:adminPage>