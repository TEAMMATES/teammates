<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/sessions" prefix="adminSessions" %>
<c:set var="jsIncludes">
    <link type="text/css" href="/stylesheets/datepicker.css" rel="stylesheet"/>
    <script type="text/javascript" src="/js/administrator.js"></script>
    <script type="text/javascript" src="/js/adminSessions.js"></script>
    <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
    <script type="text/javascript" src="/js/date.js"></script>
    <script type="text/javascript" src="/js/datepicker.js"></script>
</c:set>
<ta:adminPage bodyTitle="Ongoing Sessions" pageTitle="TEAMMATES - Administrator Sessions" jsIncludes="${jsIncludes}">
    <h1>
        <small> 
            Total: ${data.totalOngoingSessions}
            <br> 
            ${data.rangeStartString}&nbsp;&nbsp;
            <span class="glyphicon glyphicon-resize-horizontal"></span>&nbsp;&nbsp;${data.rangeEndString}
            &nbsp;${data.timeZoneAsString}
        </small>
        <br> 
        <a href="#" class="btn btn-info" onclick="openAllSections(${data.tableCount})">Open All</a> 
        <a href="#" class="btn btn-warning" onclick="closeAllSections(${data.tableCount})">Collapse All</a>
    </h1>
    <br>
    <adminSessions:filter filter="${data.filter}"/>
    <t:statusMessage/>
    <c:forEach items="${data.institutionPanels}" var="institutionPanel" varStatus="i">
        <adminSessions:institutionPanel institutionPanel="${institutionPanel}" tableIndex="${i.count}" showAll="${data.showAll}" />
    </c:forEach>
    <a href="#" class="back-to-top-left"><span class="glyphicon glyphicon-arrow-up"></span>&nbsp;Top</a> 
    <a href="#" class="back-to-top-right">Top&nbsp;<span class="glyphicon glyphicon-arrow-up"></span></a>
</ta:adminPage>