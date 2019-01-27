import {
    enableHoverToDisplayEditOptions,
    registerResponseCommentCheckboxEvent,
    registerResponseCommentsEvent,
} from '../common/feedbackResponseComments';

import {
    bindStudentPhotoHoverLink,
    bindStudentPhotoLink,
    prepareInstructorPages,
} from '../common/instructor';

import {
    displayAjaxRetryMessageForPanelHeading,
    prepareInstructorFeedbackResultsPage,
    showHideStats,
    toggleExcludingSelfResultsForRubricStatistics,
} from '../common/instructorFeedbackResults';

import {
    toggleAdditionalQuestionInfo,
    toggleSingleCollapse,
} from '../common/ui';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;
window.toggleExcludingSelfResultsForRubricStatistics = toggleExcludingSelfResultsForRubricStatistics;

$(document).ready(() => {
    prepareInstructorPages();
    prepareInstructorFeedbackResultsPage();
    enableHoverToDisplayEditOptions();
    registerResponseCommentCheckboxEvent();
    registerResponseCommentsEvent();

    const isPanelSetAsEmptyByBackend = function ($panelBody) {
        return $panelBody.find('.no-response').length !== 0;
    };

    const displayAsEmptyPanel = function ($panelBody) {
        $panelBody.parents('.panel.panel-info').removeClass('panel-info').addClass('panel-default');
    };

    const seeMoreRequest = function (e) {
        const $panelHeading = $(this);
        if ($('#show-stats-checkbox').is(':checked')) {
            $panelHeading.find('[id^="showStats-"]').val('on');
        } else {
            $panelHeading.find('[id^="showStats-"]').val('off');
        }

        const displayIcon = $panelHeading.find('.display-icon');
        const formObject = $panelHeading.children('form');
        const panelCollapse = $panelHeading.parent().children('.panel-collapse');
        const panelBody = $(panelCollapse[0]).children('.panel-body');
        const formData = formObject.serialize();
        e.preventDefault();
        $.ajax({
            type: 'POST',
            cache: false,
            url: `${$(formObject[0]).attr('action')}?${formData}`,
            beforeSend() {
                displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">');
            },
            error() {
                displayAjaxRetryMessageForPanelHeading(displayIcon);
            },
            success(data) {
                const appendedQuestion = $(data).find('#questionBody-0').html();
                const $panelBody = $(panelBody[0]);
                $(data).remove();
                if (typeof appendedQuestion === 'undefined') {
                    $panelBody.removeClass('padding-0');
                    $panelBody.html('There are too many responses for this question. '
                                    + 'Please view the responses one section at a time.');
                } else {
                    if (appendedQuestion.indexOf('resultStatistics') === -1) {
                        $panelBody.removeClass('padding-0');
                    }
                    $panelBody.html(appendedQuestion);
                }

                // bind the show picture onclick events
                bindStudentPhotoLink($panelBody.find('.profile-pic-icon-click > .student-profile-pic-view-link'));
                // bind the show picture onhover events
                bindStudentPhotoHoverLink($panelBody.find('.profile-pic-icon-hover'));

                $panelHeading.removeClass('ajax_submit ajax_auto');

                displayIcon.html('<span class="glyphicon glyphicon-chevron-down pull-right"></span>');

                $panelHeading.off('click');
                $panelHeading.click(toggleSingleCollapse);
                $panelHeading.trigger('click');

                if (isPanelSetAsEmptyByBackend($panelBody)) {
                    displayAsEmptyPanel($panelBody);
                }

                showHideStats();
            },
        });
    };

    const $questionPanelHeadings = $('.ajax_submit,.ajax_auto');
    $questionPanelHeadings.click(seeMoreRequest);
});
