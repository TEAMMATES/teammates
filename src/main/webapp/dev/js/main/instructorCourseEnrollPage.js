import {
    showModalAlert,
} from '../common/bootboxWrapper';

import {
    BootstrapContextualColors,
} from '../common/const';

import {
    prepareInstructorPages,
} from '../common/instructor';

function isUserTyping(str) {
    return str.indexOf('\t') === -1 && str.indexOf('|') === -1;
}

window.isUserTyping = isUserTyping;

const loadUpFunction = function () {
    const typingErrMsg = 'Please use | character ( shift+\\ ) to seperate fields, or copy from your existing spreadsheet.';
    let notified = false;

    const ENTER_KEYCODE = 13;
    let enrolTextbox = $('#enrollstudents');
    if (enrolTextbox.length) {
        [enrolTextbox] = enrolTextbox;
        $(enrolTextbox).keydown((e) => {
            const keycode = e.which || e.keyCode;
            if (keycode === ENTER_KEYCODE) {
                if (isUserTyping(e.currentTarget.value) && !notified) {
                    notified = true;
                    showModalAlert('Invalid separator', typingErrMsg, null, BootstrapContextualColors.WARNING);
                }
            }
        });
    }
};

if (window.addEventListener) {
    window.addEventListener('load', loadUpFunction);
} else {
    window.attachEvent('load', loadUpFunction);
}

$(document).ready(() => {
    prepareInstructorPages();
});

export {
    isUserTyping,
};
