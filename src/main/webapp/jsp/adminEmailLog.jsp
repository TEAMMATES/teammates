<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/email/log" prefix="adminEmailLog" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/administrator.js"></script>
    <script type="text/javascript" src="/js/adminEmailLog.js"></script>
</c:set>

<ta:adminPage bodyTitle="Admin Email Log" pageTitle="TEAMMATES - Administrator" jsIncludes="${jsIncludes}">
    <adminEmailLog:filterPanel filterQuery="${data.filterQuery}"/>
    
    <form id="ajaxLoaderDataForm">
        <input type="hidden" name="offset" value="">
        
        
        <input type="hidden" id="filterQuery" name="filterQuery" value="${data.filterQuery}">
    </form>
    
    <c:if test="${not empty data.queryMessage}">
        <div class="alert alert-danger" id="queryMessage">
            <span class="glyphicon glyphicon-warning-sign"></span>
            <c:out value=" ${data.queryMessage}"/>
        </div>
    </c:if>

    <br>
    <br>
    
    <adminEmailLog:emailLogTable logs="${data.logs}" shouldShowAll="${data.shouldShowAll}"/>                       
    <t:statusMessage doNotFocusToStatus="${true}" statusMessagesToUser="${data.statusMessagesToUser}" />
</ta:adminPage>