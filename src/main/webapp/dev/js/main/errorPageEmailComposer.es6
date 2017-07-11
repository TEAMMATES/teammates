import {
    clearStatusMessages,
    setStatusMessageToForm,
} from '../common/statusMessage.es6';

import {
    StatusType,
} from '../common/const.es6';

const ERROR_STATUS_MESSAGE = 'Failed to record the error message. Please email our support team at '
        + '<a href="mailto:teammates@comp.nus.edu.sg">teammates@comp.nus.edu.sg</a>.';

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
            url: '/page/errorFeedbackSubmit',
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
