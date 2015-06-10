<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<c:set var="jsIncludes">
        <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css" media="screen">
        
        <script type="text/javascript" src="/js/date.js"></script>
        <script type="text/javascript" src="/js/datepicker.js"></script>
        <script type="text/javascript" src="/js/instructor.js"></script>
        <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
        <script type="text/javascript" src="/js/instructorFeedbackAjaxRemindModal.js"></script>
        <script type="text/javascript" src="/js/instructorFeedbacksAjax.js"></script>
        <script type="text/javascript" src="/js/instructorFeedbacks.js"></script>
        <script type="text/javascript" src="/js/instructorFeedbackAjaxRemindModal.js"></script>
</c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Add New Feedback Session" jsIncludes="${jsIncludes}">
    
    <c:if test="${ !data.usingAjax }">
        <ti:feedbackSessionsNewForm newForm="${ data.newForm }"/>
    </c:if>
    <br>
    <t:statusMessage />
    <br>
    <div id="sessionList" class="align-center">
        <c:if test="${ data.usingAjax }"> 
            <ti:feedbackSessionsTable fsList = "${ data.fsList }" />
        </c:if>
    </div>

    <ti:remindModal />
    <ti:copyFromModal fsList="${ data.fsList }" newForm="${ data.newForm }" />
    <ti:copyModal />
    
</ti:instructorPage>