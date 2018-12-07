/* global bootbox:false */

import {
    BootstrapContextualColors,
} from './const';

/**
 * Wrapper for Bootbox.js (available at http://bootboxjs.com/)
 * "Bootbox.js is a small JavaScript library which allows you to create programmatic dialog boxes using
 *  Bootstrap modals"
 */

const DEFAULT_OK_TEXT = 'OK';
const DEFAULT_CANCEL_TEXT = 'Cancel';
const DEFAULT_YES_TEXT = 'Yes';
const DEFAULT_NO_TEXT = 'No';

function applyStyleToModal(modal, bootstrapContextualColor) {
    modal.find('.modal-header').addClass(`alert-${bootstrapContextualColor || BootstrapContextualColors.DEFAULT}`)
            .find('.modal-title').addClass(`icon-${bootstrapContextualColor || BootstrapContextualColors.DEFAULT}`);
}

/**
 * Custom alert dialog to replace default alert() function
 * Required params: titleText and messageText
 * Optional params: okButtonText (defaults to "OK")
 *                  bootstrapContextualColor (defaults to BootstrapContextualColors.DEFAULT)
 */
function showModalAlert(titleText, messageText, okButtonText, bootstrapContextualColor) {
    const modal = bootbox.dialog({
        title: titleText,
        message: messageText,
        show: false,
        buttons: {
            okay: {
                label: okButtonText || DEFAULT_OK_TEXT,
                className: `modal-btn-ok btn-${bootstrapContextualColor || BootstrapContextualColors.DEFAULT}`,
            },
        },
    });
    applyStyleToModal(modal, bootstrapContextualColor);

    modal.modal('show');
}

/**
 * Custom confirmation dialog to replace default confirm() function
 * Required params: titleText, messageText and okCallback
 * Optional params: cancelCallBack (defaults to null)
 *                  okButtonText (defaults to "OK")
 *                  cancelButtonText (defaults to "Cancel")
 *                  bootstrapContextualColor (defaults to BootstrapContextualColors.INFO)
 *                  onHiddenCallback - triggers when the event `hidden.bs.modal` is triggered
 */
function showModalConfirmation(titleText, messageText, okCallback, cancelCallback,
        okButtonText, cancelButtonText, bootstrapContextualColor, onHiddenCallback) {
    const modal = bootbox.dialog({
        title: titleText,
        message: messageText,
        onEscape: cancelCallback || true,
        show: false,
        buttons: {
            cancel: {
                label: cancelButtonText || DEFAULT_CANCEL_TEXT,
                className: 'modal-btn-cancel btn-default',
                callback: cancelCallback || null,
            },
            ok: {
                label: okButtonText || DEFAULT_OK_TEXT,
                className: `modal-btn-ok btn-${bootstrapContextualColor || BootstrapContextualColors.DEFAULT}`,
                callback: okCallback,
            },
        },
    });

    if (onHiddenCallback) {
        modal.on('hidden.bs.modal', onHiddenCallback);
    }

    applyStyleToModal(modal, bootstrapContextualColor);

    modal.modal('show');
}

/**
 * Custom confirmation dialog to replace default confirm() function
 * Required params: titleText, messageText, yesButtonCallback and noButtonCallback
 * Optional params: cancelButtonCallBack (defaults to null)
 *                  yesButtonText (defaults to "Yes")
 *                  noButtonText (defaults to "No")
 *                  cancelButtonText (defaults to "Cancel")
 *                  bootstrapContextualColor (defaults to BootstrapContextualColors.INFO)
 *                  onHiddenCallback - triggers when the event `hidden.bs.modal` is triggered
 */
function showModalConfirmationWithCancel(titleText, messageText, yesButtonCallback, noButtonCallback,
        cancelButtonCallback, yesButtonText, noButtonText, cancelButtonText, bootstrapContextualColor,
        onHiddenCallback) {
    const modal = bootbox.dialog({
        title: titleText,
        message: messageText,
        onEscape: cancelButtonCallback || true,
        show: false,
        buttons: {
            yes: {
                label: yesButtonText || DEFAULT_YES_TEXT,
                className: `modal-btn-ok btn-${bootstrapContextualColor || BootstrapContextualColors.DEFAULT}`,
                callback: yesButtonCallback,
            },
            no: {
                label: noButtonText || DEFAULT_NO_TEXT,
                className: `modal-btn-ok btn-${bootstrapContextualColor || BootstrapContextualColors.DEFAULT}`,
                callback: noButtonCallback,
            },
            cancel: {
                label: cancelButtonText || DEFAULT_CANCEL_TEXT,
                className: 'modal-btn-cancel btn-default',
                callback: cancelButtonCallback || null,
            },
        },
    });

    if (onHiddenCallback) {
        modal.on('hidden.bs.modal', onHiddenCallback);
    }

    applyStyleToModal(modal, bootstrapContextualColor);

    modal.modal('show');
}

export {
    showModalAlert,
    showModalConfirmation,
    showModalConfirmationWithCancel,
};
