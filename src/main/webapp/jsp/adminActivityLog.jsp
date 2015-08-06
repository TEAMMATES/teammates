<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/activity" prefix="activity" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/administrator.js"></script>
    <script type="text/javascript" src="/js/adminActivityLog.js"></script>
</c:set>

<ta:adminPage bodyTitle="Admin Activity Log" pageTitle="TEAMMATES - Administrator" jsIncludes="${jsIncludes}">
    <activity:filterPanel excludedLogRequestURIs="${data.excludedLogRequestURIs}" actionListAsHtml="${data.actionListAsHtml}"
                            ifShowAll="${data.ifShowAll}" ifShowTestData="${data.ifShowTestData}" offset="${data.offset}"
                            filterQuery="${data.filterQuery}" />

    <c:if test="${not empty data.queryMessage}">
        <div class="alert alert-danger" id="queryMessage">
            <span class="glyphicon glyphicon-warning-sign"></span>
            ${data.queryMessage}
        </div>
    </c:if>

    <br> <br>

    <activity:activityLogTable logs="${data.logs}" />
    
    <t:statusMessage doNotFocusToStatus="${true}" />

    <br>

    <a href="#" class="back-to-top-left"><span class="glyphicon glyphicon-arrow-up"></span>&nbsp;Top</a> 
    
    <a href="#" class="back-to-top-right">Top&nbsp;<span class="glyphicon glyphicon-arrow-up"></span></a>
    
    <br> <br>

</ta:adminPage>