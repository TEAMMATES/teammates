import {
    Const,
    BootstrapContextualColors,
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
        const $clickedLink = $(e.currentTarget);

        const { header, text } = Const.ModalDialog.UNREGISTERED_STUDENT;
        function okCallback() {
            window.location = $clickedLink.attr('href');
        }

        showModalConfirmation(header, text, okCallback, null, null, null, BootstrapContextualColors.INFO);
    });
}

export {
    bindLinksInUnregisteredPage,
};
