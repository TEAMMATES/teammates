<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" 
           uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbackEdit" prefix="feedbackEdit" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="feedbacks" %>

<c:set var="jsIncludes">
    <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css" media="screen">
    
    <script type="text/javascript" src="/js/datepicker.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbacks.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit.js"></script>
</c:set>

<c:set var="onload">
    onload="readyFeedbackEditPage();
    bindUncommonSettingsEvents();
    updateUncommonSettingsInfo();
    hideUncommonPanels();"
</c:set>

<c:set var="EMPTY_FEEDBACK_SESSION_MESSAGE">
 <%= Const.StatusMessages.FEEDBACK_QUESTION_EMPTY %>
</c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Edit Feedback Session" jsIncludes="${jsIncludes}" bodyOnload="${onload}">
    
    <feedbacks:feedbackSessionsForm fsForm="${data.fsForm}" />
     
    <br>
    <t:statusMessage />
    <ti:copyModal />
    
    <c:if test="${empty data.qnForms}">
        <br>
        <div class="align-center bold" id="empty_message">${EMPTY_FEEDBACK_SESSION_MESSAGE}</div>
        <br/>
    </c:if>
     <br/>
    <c:forEach items="${data.qnForms}" var="question">
        <feedbackEdit:questionEditForm fqForm="${question}" numQn="${fn:length(data.qnForms)}"/>
    </c:forEach>
    
    <feedbackEdit:newQuestionForm fqForm="${data.newQnForm}" nextQnNum="${fn:length(data.qnForms) + 1}"/>
    <feedbackEdit:copyQuestionModal copyQnForm="${data.copyQnForm}" />
    
    <br/>
    <br/>
    <feedbackEdit:previewSessionForm previewForm="${data.previewForm}" />
    
    <br>
    <br>
</ti:instructorPage>