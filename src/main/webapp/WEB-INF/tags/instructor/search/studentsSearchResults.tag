<%@ tag description="instructorSearch.tag - Search students" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/search" prefix="search" %>
<%@ attribute name="searchStudentsTables" type="java.util.Collection" required="true" %>

<br>
<div class="panel panel-primary">
    <div class="panel-heading">
        <strong><jsp:doBody/></strong>
        <div class='display-icon pull-right'>
            <span class="glyphicon ${'glyphicon-chevron-up'} pull-right"></span>
        </div>
    </div>

    <div class="panel-collapse collapse in">
        <c:forEach items="${searchStudentsTables}" var="searchStudentsTable" varStatus="i">         
            <search:searchStudentsTable studentTable="${searchStudentsTable}" courseIdx="${i.index}"/>
        </c:forEach>
    </div>
</div>