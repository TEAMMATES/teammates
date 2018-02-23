import {
    prepareInstructorPages,
} from '../common/instructor';

import {
    prepareInstructorFeedbackResultsPage,
    seeMoreRequest,
    switchExcludingSelfResultsForRubricStatistics,
} from '../common/instructorFeedbackResults';

import {
    toggleAdditionalQuestionInfo,
} from '../common/ui';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;
window.switchExcludingSelfResultsForRubricStatistics = switchExcludingSelfResultsForRubricStatistics;

$(document).ready(() => {
    prepareInstructorPages();
    prepareInstructorFeedbackResultsPage();
    const $sectionPanelHeadings = $('.ajax_auto');
    $sectionPanelHeadings.click(seeMoreRequest);
});
