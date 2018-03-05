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

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;
window.toggleExcludingSelfResultsForRubricStatistics = toggleExcludingSelfResultsForRubricStatistics;

$(document).ready(() => {
    prepareInstructorPages();
    prepareInstructorFeedbackResultsPage();
    const $sectionPanelHeadings = $('.ajax_auto');
    $sectionPanelHeadings.click(seeMoreRequest);
});
