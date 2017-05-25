<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" 
           uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbackEdit" prefix="feedbackEdit" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="feedbacks" %>

<c:set var="cssIncludes">
    <link rel="stylesheet" href="<%= FrontEndLibrary.HANDSONTABLE_CSS %>" type="text/css" media="screen">
    <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css" media="screen">
    <link rel="stylesheet" href="/stylesheets/customFeedbackPaths.css" type="text/css" media="screen">
</c:set>
<c:set var="jsIncludes">
    <script type="text/javascript" src="<%= FrontEndLibrary.TINYMCE %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.HANDSONTABLE %>"></script>
    <script type="text/javascript" src="/js/richTextEditor.js"></script>

    <script type="text/javascript" src="/js/datepicker.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbacks.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/feedbackPath.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/visibilityOptions.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/customFeedbackPaths.js"></script>

    <!-- javascript for behaviors of the various question types -->
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionMcq.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionMsq.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionNumScale.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionConstSum.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionContrib.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionRubric.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionRank.js"></script>
</c:set>

<c:set var="EMPTY_FEEDBACK_SESSION_MESSAGE">
 <%= Const.StatusMessages.FEEDBACK_QUESTION_EMPTY %>
</c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Edit Feedback Session" cssIncludes="${cssIncludes}" jsIncludes="${jsIncludes}">
    <feedbackEdit:customFeedbackPathsData sessionCreatorData="${data.creatorEmail}" studentsData="${data.studentsDataAsString}" instructorsData="${data.instructorsDataAsString}"/>
    <feedbacks:feedbackSessionsForm fsForm="${data.fsForm}" />
     
    <br>
    <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
    <ti:copyModal editCopyActionLink="${data.editCopyActionLink}" />
    
    <c:if test="${empty data.qnForms}">
        <br>
        <div class="align-center bold" id="empty_message">${EMPTY_FEEDBACK_SESSION_MESSAGE}</div>
        <br>
    </c:if>
     <br>
    <c:forEach items="${data.qnForms}" var="question">
        <feedbackEdit:questionEditForm fqForm="${question}" numQn="${fn:length(data.qnForms)}"/>
    </c:forEach>
    
    <feedbackEdit:newQuestionForm fqForm="${data.newQnForm}" nextQnNum="${fn:length(data.qnForms) + 1}"/>
    <feedbackEdit:copyQuestionModal feedbackSessionName="${data.fsForm.fsName}" courseId="${data.fsForm.courseId}"/>
    
    <br>
    <br>
    <feedbackEdit:previewSessionForm previewForm="${data.previewForm}" />
    
    <br>
    <br>
</ti:instructorPage>
