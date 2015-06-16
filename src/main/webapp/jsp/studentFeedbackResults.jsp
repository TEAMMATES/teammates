<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackResults" prefix="feedbackResults" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
</c:set>

<ts:studentPage bodyTitle="Feedback Results - Student" pageTitle="TEAMMATES - Feedback Results" jsIncludes="${jsIncludes}">
    <ts:registerMessage googleId="${data.account.googleId}" registerMessage="${data.registerMessage}"/>    
    <feedbackResults:feedbackSessionDetailsPanel feedbackSession="${data.bundle.feedbackSession}"/>
    <t:statusMessage/>
    <br>
    
    <!-- For every question -->
    <c:forEach items="${data.feedbackResultsQuestionsWithResponses}" var="questionWithResponses">
        <feedbackResults:questionWithResponses questionWithResponses="${questionWithResponses}"/>
    </c:forEach>
    
    <c:if test="${empty data.feedbackResultsQuestionsWithResponses}">
        <div class="col-sm-12" style="color: red">
            There are currently no responses for you for this feedback session.
        </div>
    </c:if>
</ts:studentPage>
