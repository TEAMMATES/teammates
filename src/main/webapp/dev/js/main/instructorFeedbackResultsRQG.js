import {
    prepareInstructorPages,
} from '../common/instructor';

import {
    prepareInstructorFeedbackResultsPage,
    seeMoreRequest,
} from '../common/instructorFeedbackResults';

import {
    toggleAdditionalQuestionInfo,
} from '../common/ui';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;

$(document).ready(() => {
    prepareInstructorPages();
    prepareInstructorFeedbackResultsPage();
    const $sectionPanelHeadings = $('.ajax_auto');
    $sectionPanelHeadings.click(seeMoreRequest);
});
