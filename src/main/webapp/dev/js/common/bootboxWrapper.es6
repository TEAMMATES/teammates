/* global bootbox:false */

import {
    StatusType,
} from './const.es6';

/**
 * Wrapper for Bootbox.js (available at http://bootboxjs.com/)
 * "Bootbox.js is a small JavaScript library which allows you to create programmatic dialog boxes using
 *  Bootstrap modals"
 */

const DEFAULT_OK_TEXT = 'OK';
const DEFAULT_CANCEL_TEXT = 'Cancel';
const DEFAULT_YES_TEXT = 'Yes';
const DEFAULT_NO_TEXT = 'No';

function applyStyleForModal(modal, statusType) {
    modal.find('.modal-header')
        .addClass(`alert-${statusType}`)
    .find('.modal-title')
        .addClass(`icon-${statusType}`);
}

/**
 * Custom alert dialog to replace default alert() function
 * Required params: titleText and messageText
 * Optional params: okButtonText (defaults to "OK")
 *                  statusType (defaults to StatusType.DEFAULT)
 */
function showModalAlert(titleText, messageText, okButtonText = DEFAULT_OK_TEXT, statusType = StatusType.DEFAULT) {
    const modal = bootbox.dialog({
        title: titleText,
        message: messageText,
        buttons: {
            okay: {
                label: okButtonText,
                className: `modal-btn-ok btn-${statusType}`,
            },
        },
    });
    applyStyleForModal(modal, statusType);
}

/**
 * Custom confirmation dialog to replace default confirm() function
 * Required params: titleText, messageText and okCallback
 * Optional params: cancelCallBack (defaults to null)
 *                  okButtonText (defaults to "OK")
 *                  cancelButtonText (defaults to "Cancel")
 *                  statusType (defaults to StatusType.DEFAULT)
 */
function showModalConfirmation(titleText, messageText, okCallback, cancelCallback = null,
                                okButtonText = DEFAULT_OK_TEXT, cancelButtonText = DEFAULT_CANCEL_TEXT,
                                statusType = StatusType.DEFAULT) {
    const modal = bootbox.dialog({
        title: titleText,
        message: messageText,
        buttons: {
            cancel: {
                label: cancelButtonText,
                className: 'modal-btn-cancel btn-default',
                callback: cancelCallback,
            },
            ok: {
                label: okButtonText,
                className: `modal-btn-ok btn-${statusType}`,
                callback: okCallback,
            },
        },
    });
    applyStyleForModal(modal, statusType);
}

/**
 * Custom confirmation dialog to replace default confirm() function
 * Required params: titleText, messageText, yesButtonCallback and noButtonCallback
 * Optional params: cancelButtonCallBack (defaults to null)
 *                  yesButtonText (defaults to "Yes")
 *                  noButtonText (defaults to "No")
 *                  canelButtonText (defaults to "Cancel")
 *                  statusType (defaults to StatusType.DEFAULT)
 */
function showModalConfirmationWithCancel(titleText, messageText, yesButtonCallback, noButtonCallback,
                                    cancelButtonCallback = null, yesButtonText = DEFAULT_YES_TEXT,
                                    noButtonText = DEFAULT_NO_TEXT, cancelButtonText = DEFAULT_CANCEL_TEXT,
                                    statusType = StatusType.DEFAULT) {
    const modal = bootbox.dialog({
        title: titleText,
        message: messageText,
        buttons: {
            yes: {
                label: yesButtonText,
                className: `modal-btn-ok btn-${statusType}`,
                callback: yesButtonCallback,
            },
            no: {
                label: noButtonText,
                className: `modal-btn-ok btn-${statusType}`,
                callback: noButtonCallback,
            },
            cancel: {
                label: cancelButtonText,
                className: 'modal-btn-cancel btn-default',
                callback: cancelButtonCallback,
            },
        },
    });
    applyStyleForModal(modal, statusType);
}

export {
    showModalAlert,
    showModalConfirmation,
    showModalConfirmationWithCancel,
};
