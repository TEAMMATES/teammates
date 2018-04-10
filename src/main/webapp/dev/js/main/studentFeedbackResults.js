import {
    bindLinksInUnregisteredPage,
} from '../common/student';

import {
    toggleAdditionalQuestionInfo,
} from '../common/ui';

import {
    toggleExcludingSelfResultsForRubricStatistics,
} from '../common/instructorFeedbackResults';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;
window.toggleExcludingSelfResultsForRubricStatistics = toggleExcludingSelfResultsForRubricStatistics;

$(document).ready(() => {
    bindLinksInUnregisteredPage('[data-unreg].navLinks');
});
