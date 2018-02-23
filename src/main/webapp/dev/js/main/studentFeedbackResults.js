import {
    bindLinksInUnregisteredPage,
} from '../common/student';

import {
    toggleAdditionalQuestionInfo,
} from '../common/ui';

import {
    switchExcludingSelfResultsForRubricStatistics,
} from '../common/instructorFeedbackResults';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;
window.switchExcludingSelfResultsForRubricStatistics = switchExcludingSelfResultsForRubricStatistics;

$(document).ready(() => {
    bindLinksInUnregisteredPage('[data-unreg].navLinks');
});
