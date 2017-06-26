import {
    bindLinksInUnregisteredPage,
} from '../common/student.es6';

import {
    toggleAdditionalQuestionInfo,
} from '../common/ui.es6';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;

$(document).ready(() => {
    bindLinksInUnregisteredPage('[data-unreg].navLinks');
});
