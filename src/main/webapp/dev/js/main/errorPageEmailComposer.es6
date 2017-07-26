import {
    clearStatusMessages,
    setStatusMessageToForm,
} from '../common/statusMessage.es6';

import {
    StatusType,
} from '../common/const.es6';

const SUPPORT_EMAIL = $('#error-feedback-email-composer-recipient-email').prop('value');
const ERROR_STATUS_MESSAGE = 'Failed to record the error message. Please email our support team at '
        + `<a href="mailto:${SUPPORT_EMAIL}">${SUPPORT_EMAIL}</a>.`;
const USER_REPORT_SUBMIT_ACTION_URI = '/page/errorFeedbackSubmit';

function displayWarningStatus($form) {
    setStatusMessageToForm(ERROR_STATUS_MESSAGE,
            StatusType.DANGER,
            $form);
}

function hideErrorReportForm($form) {
    $form.children().not('#statusMessagesToUser').hide();
}

$(document).ready(() => {
    $('#error-feedback-form').on('submit', (event) => {
        event.preventDefault();
        const $form = $(event.target);
        // Use Ajax to submit form data
        $.ajax({
            url: USER_REPORT_SUBMIT_ACTION_URI,
            type: 'POST',
            data: $form.serialize(),
            beforeSend() {
                clearStatusMessages();
                const $sendFeedbackButton = $('.btn-success');
                $sendFeedbackButton.prop('disabled', true);
                $sendFeedbackButton.html("<img height='25' width='25' src='/images/ajax-preload.gif'/>");
            },
            success(result) {
                hideErrorReportForm($form);
                if (typeof result.statusMessagesToUser !== 'undefined') {
                    setStatusMessageToForm(result.statusMessagesToUser[0].text,
                            result.statusMessagesToUser[0].color.toLowerCase(),
                            $form);
                } else {
                    displayWarningStatus($form);
                }
            },
            error() {
                hideErrorReportForm($form);
                displayWarningStatus($form);
            },
        });
    });
});
