import {
    Const,
    StatusType,
} from './const.es6';

import {
    showModalConfirmation,
} from './bootboxWrapper.es6';

/**
 * Contains functions common to the student pages.
 */
function bindLinksInUnregisteredPage(selector) {
    $(document).on('click', selector, (e) => {
        e.preventDefault();
        const $clickedLink = $(e.target);

        const header = Const.ModalDialog.UNREGISTERED_STUDENT.header;
        const messageText = Const.ModalDialog.UNREGISTERED_STUDENT.text;
        function okCallback() {
            window.location = $clickedLink.attr('href');
        }

        showModalConfirmation(header, messageText, okCallback, null, null, null, StatusType.INFO);
    });
}

export {
    bindLinksInUnregisteredPage,
};
