import {
    BootstrapContextualColors,
} from './const';

import {
    scrollToElement,
} from './scrollTo';

const DIV_STATUS_MESSAGE = '#statusMessagesToUser';

function buildNewStatusMessageDiv(message, bootstrapContextualColor) {
    const $newStatusMessageDivContent = $('<div></div>');

    // Default the status type to info if any invalid status is passed in
    const contextualColor = BootstrapContextualColors.isValidType(bootstrapContextualColor)
            ? bootstrapContextualColor : BootstrapContextualColors.INFO;

    $newStatusMessageDivContent.addClass(
            `overflow-auto alert alert-${contextualColor} icon-${contextualColor} statusMessage`);
    $newStatusMessageDivContent.html(message);

    return $newStatusMessageDivContent;
}

/**
 * Populates the status div with the message and the message status.
 * Default message type is info.
 *
 * @param message the text message to be shown to the user
 * @param {BootstrapContextualColors} bootstrapContextualColor the contextual color to apply to the status messsage
 * @return created status message div
 */
function populateStatusMessageDiv(message, bootstrapContextualColor) {
    const $statusMessageDivToUser = $(DIV_STATUS_MESSAGE);
    const $statusMessageDivContent = buildNewStatusMessageDiv(message, bootstrapContextualColor);
    $statusMessageDivToUser.empty();
    $statusMessageDivToUser.append($statusMessageDivContent);
    return $statusMessageDivToUser;
}

/**
 * Appends a new message to the status div, leaving existing messages intact.
 * Default message type is info.
 *
 * @param message the text message to be shown to the user
 * @param {BootstrapContextualColors} bootstrapContextualColor the contextual color to apply to the status messsage
 */
function appendNewStatusMessage(message, bootstrapContextualColor) {
    const $statusMessagesDivToUser = $(DIV_STATUS_MESSAGE);
    $statusMessagesDivToUser.append(buildNewStatusMessageDiv(message, bootstrapContextualColor));
    $statusMessagesDivToUser.show();
    scrollToElement($statusMessagesDivToUser[0], { offset: -window.innerHeight / 2 });
}

/**
 * Sets a status message and the message status.
 * Default message type is info.
 *
 * @param message the text message to be shown to the user
 * @param {BootstrapContextualColors} bootstrapContextualColor the contextual color to apply to the status messsage
 */
function setStatusMessage(message, bootstrapContextualColor) {
    if (message === '' || message === undefined || message === null) {
        return;
    }
    const $statusMessageDivToUser = populateStatusMessageDiv(message, bootstrapContextualColor);
    $statusMessageDivToUser.show();
    scrollToElement($statusMessageDivToUser[0], { offset: -window.innerHeight / 2 });
}

/**
 * Sets a status message and the message status to a given form.
 * Default message type is info.
 *
 * @param message the text message to be shown to the user
 * @param {BootstrapContextualColors} bootstrapContextualColor the contextual color to apply to the status messsage
 * @param form form which should own the status
 */
function setStatusMessageToForm(message, bootstrapContextualColor, form) {
    if (message === '' || message === undefined || message === null) {
        return;
    }
    // Copy the statusMessage and prepend to form
    const $copyOfStatusMessagesToUser = populateStatusMessageDiv(message, bootstrapContextualColor).clone().show();
    $(DIV_STATUS_MESSAGE).remove();
    $(form).prepend($copyOfStatusMessagesToUser);
    const opts = {
        offset: -window.innerHeight / 8,
        duration: 1000,
    };
    scrollToElement($copyOfStatusMessagesToUser[0], opts);
}

/**
 * Appends the status messages panels into the current list of panels of status messages.
 * @param  messages the list of status message panels to be added (not just text)
 *
 */
function appendStatusMessage(messages) {
    const $statusMessagesToUser = $(DIV_STATUS_MESSAGE);

    $statusMessagesToUser.append($(messages));
    $statusMessagesToUser.show();
}

/**
 * Clears the status message div tag and hides it
 */
function clearStatusMessages() {
    const $statusMessagesToUser = $(DIV_STATUS_MESSAGE);

    $statusMessagesToUser.empty();
    $statusMessagesToUser.hide();
}

export {
    appendNewStatusMessage,
    appendStatusMessage,
    clearStatusMessages,
    setStatusMessage,
    setStatusMessageToForm,
};
