import {
    prepareInstructorPages,
} from '../common/instructor';

import {
    prepareInstructorFeedbackResultsPage,
    seeMoreRequest,
    toggleExcludingSelfResultsForRubricStatistics,
} from '../common/instructorFeedbackResults';

import {
    toggleAdditionalQuestionInfo,
} from '../common/ui';

import {
    enableHoverToDisplayEditOptions,
    registerResponseCommentCheckboxEvent,
    registerResponseCommentsEvent,
} from '../common/feedbackResponseComments';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;
window.toggleExcludingSelfResultsForRubricStatistics = toggleExcludingSelfResultsForRubricStatistics;

$(document).ready(() => {
    prepareInstructorPages();
    prepareInstructorFeedbackResultsPage();
    enableHoverToDisplayEditOptions();
    registerResponseCommentCheckboxEvent();
    registerResponseCommentsEvent();
    const $sectionPanelHeadings = $('.ajax_auto');
    $sectionPanelHeadings.click(seeMoreRequest);
});
