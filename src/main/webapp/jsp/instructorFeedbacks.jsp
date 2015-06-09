<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<c:set var="jsIncludes">
        <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
        <script type="text/javascript" src="/js/instructorFeedbacksAjax.js"></script>
        <script type="text/javascript" src="/js/instructorFeedbacks.js"></script>
        <script type="text/javascript" src="/js/instructorFeedbackAjaxRemindModal.js"></script>
</c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Add New Feedback Session" jsIncludes="${jsIncludes}">
    <t:statusMessage />


    <ti:feedbackSessionsNewForm courseToSelect="${data.courseIdForNewSession}" feedbackSessionType="${data.feedbackSessionType}" newFeedbackSession="${data.newFeedbackSession}" courses="${data.courses}"/>


</ti:instructorPage>