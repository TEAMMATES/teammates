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
    <link rel="stylesheet" media="screen" href="/bower_components/handsontable/dist/handsontable.full.css">
    <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css" media="screen">
    
    <script src="/bower_components/handsontable/dist/handsontable.full.js"></script>
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

        /// feedback path related
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
        function showFeedbackPathShowDetails(elem) {
            var containingForm = getContainingForm(elem);
            containingForm.find('.feedback-path-show-details').show();
        }

        // attaching event handlers
        $('.feedback-path-dropdown > ul > li > ul > li > a').on('click', function() {
            setFeedbackPathDropdownText(this.dataset.feedbackPathDescription, this);
            hideFeedbackPathOthers(this);
			showFeedbackPathShowDetails(this);
        });
        $('.feedback-path-others-menu-option').on('click', function() {
            setFeedbackPathDropdownText(this.dataset.feedbackPathDescription, this);
            showFeedbackPathOthers(this);
        });

        /// visibility related
        // event handlers
        function setVisibilityDropdownText(text, elem) {
            var containingForm = getContainingForm(elem);
            var visibilityDropdown = containingForm.find('.visibility-dropdown');
            visibilityDropdown.find('button').html(text);
        }
        function hideVisibilityOthers(elem) {
            var containingForm = getContainingForm(elem);
            containingForm.find('.visibility-others').hide();
        }
        function showVisibilityPreview(elem) {
            var containingForm = getContainingForm(elem);
            containingForm.find('.visibility-preview').show();
        }
        function showVisibilityOthers(elem) {
            var containingForm = getContainingForm(elem);
            containingForm.find('.visibility-others').show();
            containingForm.find('.visibilityOptions').show();
            containingForm.find('.visibility-show-details').find('a').html('Details / Customize further <<');
        }
        function toggleVisibilityOthers(elem) {
            var containingForm = getContainingForm(elem);
            containingForm.find('.visibility-others').toggle();
            containingForm.find('.visibilityOptions').toggle();
            toggleShowDetailsArrows(elem);
        }
        function showVisibilityShowDetails(elem) {
            var containingForm = getContainingForm(elem);
            containingForm.find('.visibility-show-details').show();
        }
        function toggleShowDetailsArrows(elem) {
            console.log($(elem).html());
            if ($(elem).html() == 'Details / Customize further &lt;&lt;') {
                $(elem).html('Details / Customize further >>');
            } else {
                $(elem).html('Details / Customize further <<');
            }
        }
        function flashVisibilityDropdownText(elem) {
            var containingForm = getContainingForm(elem);
            var visibilityDropdown = containingForm.find('.visibility-dropdown');
            visibilityDropdown.effect('highlight', {}, 1000);
        }
        function visibilityOptionIsNotYetSetToOther(elem) {
            var containingForm = getContainingForm(elem);
            var visibilityDropdown = containingForm.find('.visibility-dropdown');
            return visibilityDropdown.find('button').html() !== 'Other options:';
        }

        // attaching event handlers
        $('.visibility-dropdown > ul > li > a').on('click', function() {
            setVisibilityDropdownText(this.dataset.visibilityDescription, this);
            showVisibilityPreview(this);
            showVisibilityShowDetails(this);
        });
        $('.visibility-others-menu-option').on('click', function() {
            showVisibilityOthers(this);
        });
        $('.visibility-show-details > div > a').on('click', function() {
            toggleVisibilityOthers(this);
        });
        $('.visibilityCheckbox').on('click', function(){
            if (visibilityOptionIsNotYetSetToOther(this)) {
                setVisibilityDropdownText('Other options:', this);
                flashVisibilityDropdownText(this);
            }
        });

        /// brute-force functions for prototyping
        function setVisibilityOptionsForShoutingIntoTheVoid(elem) {
            var containingForm = getContainingForm(elem);
			// checkboxes
			containingForm.find('.visibility-others .visibilityCheckbox').prop('checked', false);

			// preview text
			containingForm.find('.visibility-preview > div > ul').html('<li>The response, recipient identity and giver\'s identity will only be visible to the giver</li>');
		}
		$('.visibility-dropdown > ul > li:first-child > a').on('click', function() {
			setVisibilityOptionsForShoutingIntoTheVoid(this);
		});

        function setVisibilityOptionsForCompletelyTransparent(elem) {
            var containingForm = getContainingForm(elem);
			// checkboxes
			containingForm.find('.visibility-others .visibilityCheckbox').prop('checked', true);

			// preview text
			containingForm.find('.visibility-preview > div > ul').html('<li>The response, recipient identity and giver\'s identity will be visible to everyone in course</li>');
		}
		$('.visibility-dropdown > ul > li:nth-last-child(4) > a').on('click', function() {
			setVisibilityOptionsForCompletelyTransparent(this);
		});

		function rewordVisibilityOptionsForStudentsToTeammates(elem) {
            var containingForm = getContainingForm(elem);
			containingForm.find('.dataTable > tbody > tr:nth-child(2) > td:first-child > div').html('Recipient (Each teammate)');
			containingForm.find('.dataTable > tbody > tr:nth-child(2) > td:first-child > div').html('Recipient (Each teammate)');
			containingForm.find('.dataTable > tbody > tr:nth-child(3)').hide();
		}
		$('.feedback-path-dropdown > ul > li:nth-child(2) > ul > li:nth-child(4) > a').on('click', function() {
			rewordVisibilityOptionsForStudentsToTeammates(this);
		});
    </script>
    <style>
        .feedback-path-dropdown,
        .visibility-dropdown {
            width: 100%;
            margin-bottom: 0.6em;
        }
        .feedback-path-others,
		.feedback-path-show-details,
        .visibility-preview,
        .visibility-others,
        .visibility-show-details {
            display: none;
        }
        .visibility-others {
            margin-bottom: -10px;
        }
        .visibility-show-details {
            padding-bottom: 15px;
        }
        [id^="button_question_submit-"] {
            margin-top: 15px;
        }
    </style>

</ti:instructorPage>
