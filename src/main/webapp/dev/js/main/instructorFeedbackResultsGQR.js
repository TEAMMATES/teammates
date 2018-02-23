import {
    prepareInstructorPages,
} from '../common/instructor';

import {
    prepareInstructorFeedbackResultsPage,
    seeMoreRequest,
    toggleShowingStaticsForRubricsQuestionExcludingSelf
} from '../common/instructorFeedbackResults';

import {
    toggleAdditionalQuestionInfo,
} from '../common/ui';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;
window.toggleShowingStaticsForRubricsQuestionExcludingSelf = toggleShowingStaticsForRubricsQuestionExcludingSelf;


$(document).ready(() => {
    prepareInstructorPages();
    prepareInstructorFeedbackResultsPage();
    const $sectionPanelHeadings = $('.ajax_auto');
    $sectionPanelHeadings.click(seeMoreRequest);
});
