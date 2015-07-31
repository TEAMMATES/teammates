<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/courseStudentDetails" prefix="ticsd" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorStudentRecords.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
    <script type="text/javascript">
        var showCommentBox = "${data.showCommentBox}";
    </script>
</c:set>
<c:set var="bodyTitle">${data.studentName}'s Records<small class="muted"> - ${data.courseId}</small></c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor" jsIncludes="${jsIncludes}" bodyTitle="${bodyTitle}">
    <t:statusMessage />
    <div class="container-fluid">
        <c:if test="${not empty data.studentProfile}">
            <ticsd:studentProfile student="${data.studentProfile}"/>
            <ti:moreInfo student="${data.studentProfile}" />
        </c:if>
        <div class="row">
            <div class="col-md-12">
                <shared:commentsPanel commentsForStudentsTables="${data.commentsForStudentTable}" courseId="${data.courseId}" forRecordsPage="${true}" />
                <br>
                <c:forEach items="${data.sessionNames}" var="fsName" varStatus="fbIndex">
                    <div class="well well-plain student_feedback"
                         id="studentFeedback-${fbIndex.index}" 
                         onclick="loadFeedbackSession('${data.courseId}', '${data.studentEmail}', '${data.googleId}','${fsName}', this)">
                        <div class="text-primary">
                            <h2 id="feedback_name-${fbIndex.index}">
                                <strong>Feedback Session : ${fsName}</strong>
                            </h2>
                        </div>
                        <div class="placeholder-img-loading"></div>
                        <div id="target-feedback-${fbIndex.index}">
                        </div>
                    </div>
                    <br>                    
                </c:forEach>
            </div>
        </div>
    </div>
</ti:instructorPage>