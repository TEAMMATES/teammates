import {
    Const,
    StatusType,
} from './const';

import {
    showModalConfirmation,
} from './bootboxWrapper';

/**
 * Contains functions common to the student pages.
 */
function bindLinksInUnregisteredPage(selector) {
    $(document).on('click', selector, (e) => {
        e.preventDefault();
        const $clickedLink = $(e.target);

        const { header, text } = Const.ModalDialog.UNREGISTERED_STUDENT;
        function okCallback() {
            window.location = $clickedLink.attr('href');
        }

        showModalConfirmation(header, text, okCallback, null, null, null, StatusType.INFO);
    });
}

export {
    bindLinksInUnregisteredPage,
};
