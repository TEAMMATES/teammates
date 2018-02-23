import {
    bindLinksInUnregisteredPage,
} from '../common/student';

import {
    toggleAdditionalQuestionInfo,
} from '../common/ui';

import {
    toggleShowingStaticsForRubricsQuestionExcludingSelf
} from '../common/instructorFeedbackResults';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;
window.toggleShowingStaticsForRubricsQuestionExcludingSelf = toggleShowingStaticsForRubricsQuestionExcludingSelf;

$(document).ready(() => {
    bindLinksInUnregisteredPage('[data-unreg].navLinks');
});
