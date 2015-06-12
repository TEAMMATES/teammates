<%@ tag description="instructorSearch.tag - Search students" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/search" prefix="search" %>

<br>
<div class="panel panel-primary">
    <div class="panel-heading">
        <strong><jsp:doBody></jsp:doBody></strong>
    </div>
    
    <div class="panel-body">
        <c:set var="courseIdx" value="${-1}"/>
        
        <c:forEach items="${data.searchStudentsTables}" var="searchStudentsTable">
            <c:set var="studentIdx" value="${0}"/>
            <c:set var="courseIdx" value="${courseIdx + 1}"/>
            
            <search:searchStudentsTable studentIdx="${studentIdx}" studentTable="${searchStudentsTable}" courseIdx="${courseIdx}"/>
        </c:forEach>
    </div>
</div>