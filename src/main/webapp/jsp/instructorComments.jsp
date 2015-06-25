<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/comments" prefix="comments" %>
<c:set var="jsIncludes">
    <link href="/stylesheets/omniComment.css" rel="stylesheet">
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/feedbackResponseComments.js"></script>
    <script src="/js/omniComment.js"></script>
</c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Comments from Instructors" jsIncludes="${jsIncludes}">
    <div class="inner-container">
        <div class="row">
            <div class="col-sm-6 instructor-header-bar">
                <comments:search instructorSearchLink="${data.instructorSearchLink}"/>
            </div>
        </div>
    </div>
    <br>
    <t:statusMessage />
    <comments:filter displayArchive="${data.displayArchive}" instructorCommentsLink="${data.instructorCommentsLink}" comments="${data.comments}" feedbackSessions="${data.feedbackSessions}"/>
    <c:choose>
    <c:when test="${not empty data.coursePaginationList}">
        <comments:pagination previousPageLink="${data.previousPageLink}" coursePaginationList="${data.coursePaginationList}"
         viewingDraft="${data.viewingDraft}" currentCourseId="${data.courseId}" 
         instructorCommentsLink="${data.instructorCommentsLink}" nextPageLink="${data.nextPageLink}"/>
        <div class="well well-plain">
            <div class="row">
                <h4 class="col-sm-9 text-color-primary">
                    <strong>
                        ${data.viewingDraft ? "Drafts" : data.courseName}
                    </strong>
                </h4>
                <comments:commentsNotifyPanel courseId="${data.courseId}" numberOfPendingComments="${data.numberOfPendingComments}" />
            </div>
            <div id="no-comment-panel" style="${empty data.comments && empty data.feedbackSessions ? '' : 'display:none;'}">
                <br>
                <div class="panel">
                    <div class="panel-body">
                        You don't have any comment in this course.
                    </div>
                </div>
            </div>
            <c:set var="panelIdx" value="0" />
            <c:if test="${not empty data.comments}"> <%--check student comments starts--%> 
                <c:set var="panelIdx" value="${panelIdx + 1}" />
                <div id="panel_display-${panelIdx}">
                    <br>
                    <comments:commentsForStudentsPanel courseId="${data.courseId}" commentsForStudentsTables="${data.commentsForStudentsTables}" viewingDraft="${data.viewingDraft}" />
                </div>
            </c:if> <%--check student comments ends --%>
            <c:set var="fsIdx" value="0" />
            <c:forEach items="${data.feedbackSessions}" var="fs" varStatus="fsIdx"> <%--FeedbackSession loop starts--%>
                <c:set var="fsName" value="${fs.sessionName}" />
                <c:set var="panelIdx" value="${panelIdx + 1}"/>
                <div id="panel_display-${panelIdx}">
                    <br>
                    <comments:commentsFromFeedbackSessions courseId="${data.courseId}" fsName="${fsName}" fsIdx="${fsIdx.index + 1}" panelIdx="${panelIdx}" />
                </div>
            </c:forEach> <%-- FeedbackSession loop ends --%>
        </div>
        <comments:pagination previousPageLink="${data.previousPageLink}" coursePaginationList="${data.coursePaginationList}"
         viewingDraft="${data.viewingDraft}" currentCourseId="${data.courseId}" 
         instructorCommentsLink="${data.instructorCommentsLink}" nextPageLink="${data.nextPageLink}"/>
    </c:when>
    <c:otherwise>
        <div id="statusMessage" class="alert alert-warning">
            There is no comment to display
        </div>
    </c:otherwise>
    </c:choose>
</ti:instructorPage>
