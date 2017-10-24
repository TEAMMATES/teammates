import {
    bindLinksInUnregisteredPage,
} from '../common/student';

import {
    toggleAdditionalQuestionInfo,
} from '../common/ui';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;

$(document).ready(() => {
    bindLinksInUnregisteredPage('[data-unreg].navLinks');
});
