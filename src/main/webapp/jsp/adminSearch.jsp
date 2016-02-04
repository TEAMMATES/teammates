<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/search" prefix="adminSearch" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/adminSearch.js"></script>
</c:set>

<ta:adminPage bodyTitle="Admin Search" pageTitle="TEAMMATES - Administrator" jsIncludes="${jsIncludes}">
    <adminSearch:searchPageInput searchKey="${data.searchKey}"/>
    
    <c:if test="${not empty data.instructorResultList}">
        <adminSearch:instructorResultsTable instructorResultsTable="${data.instructorTable}"/>
    </c:if>
    
    <c:if test="${not empty data.studentResultList}">
        <adminSearch:studentResultsTable studentResultsTable="${data.studentTable}"/>
    </c:if>
    
    <t:statusMessage/>
</ta:adminPage>
<!-- Formatting searchKey for highlight searchKey plugin ( ['string1','string2',...] )-->
<c:set var="splittedSearchKey" value="${fn:split(data.searchKey, ' ')}" />		<!-- splitting by space	 -->
<c:set var="formattedSearchKey" value='${fn:join(splittedSearchKey, \'","\')}' />				<!-- Joining with comma within single quotes -->
<c:set var='formattedSearchKey' value='["${formattedSearchKey}"]' />									<!-- Surrounding string with single quote + square bracket -->

<script type="text/javascript">
 $( document ).ready(function() {
	 $("body").highlight(${formattedSearchKey});
 });
 </script>
