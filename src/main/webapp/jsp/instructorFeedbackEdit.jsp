<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" 
           uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbackEdit" prefix="feedbackEdit" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="feedbacks" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>

<c:set var="jsIncludes">
    <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css" media="screen">
    
    <script type="text/javascript" src="/js/datepicker.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbacks.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit.js"></script>
</c:set>

<c:set var="EMPTY_FEEDBACK_SESSION_MESSAGE">
 <%= Const.StatusMessages.FEEDBACK_QUESTION_EMPTY %>
</c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Edit Feedback Session" jsIncludes="${jsIncludes}">
    
    <feedbacks:feedbackSessionsForm fsForm="${data.fsForm}" />
     
    <br>
    <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
    <ti:copyModal editCopyActionLink="${data.editCopyActionLink}" />
    
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
    <shared:confirmationModal/>
    <script>
        // helper functions
        function getQuestionId(elem) {
            var containingForm = $(elem).closest('form');
            return containingForm.attr('id').split('form_editquestion-')[1];
        }
        function getContainingForm(elem) {
            return containingForm = $(elem).closest('form');
        }

        // event handlers
        function setFeedbackPathDropdownText(text, elem) {
            var containingForm = getContainingForm(elem);
            var feedbackPathDropdown = containingForm.find('.feedback-path-dropdown');
            feedbackPathDropdown.find('button').html(text);
        }
        function hideFeedbackPathOthers(elem) {
            var containingForm = getContainingForm(elem);
            containingForm.find('.feedback-path-others').hide();
        }
        function showFeedbackPathOthers(elem) {
            var containingForm = getContainingForm(elem);
            containingForm.find('.feedback-path-others').show();
        }

        // attaching event handlers
        $('.others-button').on('click', function() {
            showFeedbackPathOthers(this);
        });

        $('.feedback-path-dropdown > ul > li > ul > li > a').on('click', function() {
            setFeedbackPathDropdownText(this.dataset.feedbackPathDescription, this);
            hideFeedbackPathOthers(this);
        });
    </script>
    <style>
        .feedback-path-dropdown {
            width: 100%;
        }
        .feedback-path-others {
            display: none;
        }
    </style>

</ti:instructorPage>
