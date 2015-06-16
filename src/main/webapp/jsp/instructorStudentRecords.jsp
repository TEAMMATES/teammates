<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/studentRecords" prefix="tisr" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorStudentRecords.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
</c:set>
<c:set var="extraScript">
    <script type="text/javascript">
        var showCommentBox = "${data.showCommentBox}";
    </script>
</c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor"
                   bodyTitle="${data.studentName}'s Records<small class=\"muted\"> - ${data.courseId}</small>"
                   jsIncludes="${jsIncludes}"
                   extraScript="${extraScript}"
                   bodyOnload="onload=\"readyStudentRecordsPage();\"">
    <t:statusMessage />
    <c:if test="${not empty data.studentProfile}">
        <tisr:moreInfoModal moreinfo="${data.moreInfoModal}" />
    </c:if>
    <div class="container-fluid">
        <c:if test="${not empty data.studentProfile}">
            <tisr:studentProfile profile="${data.studentProfile}" />
        </c:if>
        <div class="row">
            <div class="col-md-12">
                <tisr:commentsBox comments="${data.comments}" />
                <br>
                <c:forEach items="${data.sessions}" var="session" varStatus="i">
                    <tisr:feedbackSession session="${session}" index="${i.index}" />
                </c:forEach>
            </div>
        </div>
    </div>
</ti:instructorPage>
