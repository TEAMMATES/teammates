'use strict';
$(document).ready(function() {
    StudentCommon.bindLinksInUnregisteredPage('[data-unreg].navLinks');
});

/**
 * Contains functions common to the student pages.
 */
var StudentCommon = {

    bindLinksInUnregisteredPage: function(selector) {
        $(document).on('click', selector, function(e) {
            e.preventDefault();
            var $clickedLink = $(e.target);

            var header = Const.ModalDialogHeader.UNREGISTERED_STUDENT;
            var messageText = Const.ModalDialogText.UNREGISTERED_STUDENT;
            var okCallback = function() {
                window.location = $clickedLink.attr('href');
            };

            BootboxWrapper.showModalConfirmation(header, messageText, okCallback, null,
                    BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.INFO);
        });
    }

};
