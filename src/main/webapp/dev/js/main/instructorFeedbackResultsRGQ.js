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
    bindCollapseEvents,
    displayAjaxRetryMessageForPanelHeading,
    isEmptySection,
    prepareInstructorFeedbackResultsPage,
    removeSection,
    showHideStats,
    seeMoreRequest,
} from '../common/instructorFeedbackResults';

import {
    toggleAdditionalQuestionInfo,
    toggleSingleCollapse,
} from '../common/ui';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;

$(document).ready(() => {
    prepareInstructorPages();
    prepareInstructorFeedbackResultsPage();

    registerResponseCommentsEvent();
    registerResponseCommentCheckboxEvent();
    enableHoverToDisplayEditOptions();

    seeMoreRequest(e);

    const $sectionPanelHeadings = $('.ajax_auto');
    $sectionPanelHeadings.click(seeMoreRequest);
});
