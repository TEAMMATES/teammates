<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="feedbacks" %>
<c:set var="jsIncludes">
    <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css" media="screen">
    
    <script src="/js/date.js" type="text/javascript"></script>
    <script type="text/javascript" src="/js/datepicker.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackAjaxRemindModal.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbacksAjax.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbacks.js"></script>
</c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Add New Feedback Session" jsIncludes="${jsIncludes}" bodyOnload="onload=\"readyFeedbackPage();\"">
    
    <c:if test="${!data.usingAjax}">
        <feedbacks:feedbackSessionsForm fsForm="${data.newFsForm}"/>
       <feedbacks:loadSessionsTableByAjaxForm fsList="${data.fsList}" />
    </c:if>
    <br>
    <t:statusMessage />
    <br>
    <div id="sessionList" class="align-center">
        <c:if test="${data.usingAjax}"> 
            <feedbacks:feedbackSessionsTable fsList = "${data.fsList}" />
        </c:if>
    </div>

    <ti:remindParticularStudentsModal />
    <feedbacks:copyFromModal copyFromModal="${data.copyFromModal}" />
    <ti:copyModal />
    
</ti:instructorPage>