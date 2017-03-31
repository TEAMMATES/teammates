/* global Const:false BootboxWrapper:false StatusType:false */
/**
 * Contains functions common to the student pages.
 */
function bindLinksInUnregisteredPage(selector) {
    $(document).on('click', selector, (e) => {
        e.preventDefault();
        const $clickedLink = $(e.target);

        const header = Const.ModalDialogHeader.UNREGISTERED_STUDENT;
        const messageText = Const.ModalDialogText.UNREGISTERED_STUDENT;
        function okCallback() {
            window.location = $clickedLink.attr('href');
        }

        BootboxWrapper.showModalConfirmation(header, messageText, okCallback, null,
                BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.INFO);
    });
}

/*
export default {
    bindLinksInUnregisteredPage,
};
*/
/* exported bindLinksInUnregisteredPage */
