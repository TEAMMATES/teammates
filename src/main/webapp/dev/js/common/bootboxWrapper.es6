/* global bootbox:false */

import { StatusType } from './const.es6';

/**
 * Wrapper for Bootbox.js (available at http://bootboxjs.com/)
 * "Bootbox.js is a small JavaScript library which allows you to create programmatic dialog boxes using
 *  Bootstrap modals"
 */

const DEFAULT_OK_TEXT = 'OK';
const DEFAULT_CANCEL_TEXT = 'Cancel';
const DEFAULT_YES_TEXT = 'Yes';
const DEFAULT_NO_TEXT = 'No';

/**
 * Custom alert dialog to replace default alert() function
 * Required params: titleText and messageText
 * Optional params: okButtonText (defaults to "OK")
 *                  color (defaults to StatusType.DEFAULT)
 */
function showModalAlert(titleText, messageText, okButtonText, color) {
    bootbox.dialog({
        title: titleText,
        message: messageText,
        buttons: {
            okay: {
                label: okButtonText || DEFAULT_OK_TEXT,
                className: `modal-btn-ok btn-${color}` || StatusType.DEFAULT,
            },
        },
    })
    // applies bootstrap color to title background
    .find('.modal-header').addClass(`alert-${color}` || StatusType.DEFAULT);
}

/**
 * Custom confirmation dialog to replace default confirm() function
 * Required params: titleText, messageText and okCallback
 * Optional params: cancelCallBack (defaults to null)
 *                  okButtonText (defaults to "OK")
 *                  cancelButtonText (defaults to "Cancel")
 *                  color (defaults to StatusType.INFO)
 */
function showModalConfirmation(titleText, messageText, okCallback, cancelCallback,
                                okButtonText, cancelButtonText, color) {
    bootbox.dialog({
        title: titleText,
        message: messageText,
        buttons: {
            cancel: {
                label: cancelButtonText || DEFAULT_CANCEL_TEXT,
                className: 'modal-btn-cancel btn-default',
                callback: cancelCallback || null,
            },
            ok: {
                label: okButtonText || DEFAULT_OK_TEXT,
                className: `modal-btn-ok btn-${color}` || StatusType.DEFAULT,
                callback: okCallback,
            },
        },
    })
    // applies bootstrap color to title background
    .find('.modal-header').addClass(`alert-${color}` || StatusType.DEFAULT);
}

/**
 * Custom confirmation dialog to replace default confirm() function
 * Required params: titleText, messageText, yesButtonCallback and noButtonCallback
 * Optional params: cancelButtonCallBack (defaults to null)
 *                  yesButtonText (defaults to "Yes")
 *                  noButtonText (defaults to "No")
 *                  canelButtonText (defaults to "Cancel")
 *                  color (defaults to StatusType.INFO)
 */
function showModalConfirmationWithCancel(titleText, messageText, yesButtonCallback, noButtonCallback,
                                    cancelButtonCallback, yesButtonText, noButtonText, cancelButtonText, color) {
    bootbox.dialog({
        title: titleText,
        message: messageText,
        buttons: {
            yes: {
                label: yesButtonText || DEFAULT_YES_TEXT,
                className: `modal-btn-ok btn-${color}` || StatusType.DEFAULT,
                callback: yesButtonCallback,
            },
            no: {
                label: noButtonText || DEFAULT_NO_TEXT,
                className: `modal-btn-ok btn-${color}` || StatusType.DEFAULT,
                callback: noButtonCallback,
            },
            cancel: {
                label: cancelButtonText || DEFAULT_CANCEL_TEXT,
                className: 'modal-btn-cancel btn-default',
                callback: cancelButtonCallback || null,
            },
        },
    })
    // applies bootstrap color to title background
    .find('.modal-header').addClass(`alert-${color}` || StatusType.DEFAULT);
}

export {
    showModalAlert,
    showModalConfirmation,
    showModalConfirmationWithCancel,
};
