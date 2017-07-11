import {
    clearStatusMessages,
    setStatusMessageToForm,
} from '../common/statusMessage.es6';

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
                setStatusMessageToForm(result.statusMessagesToUser[0].text,
                        result.statusMessagesToUser[0].color.toLowerCase(),
                        $form);
                $form.children().not('#statusMessagesToUser').hide();
            },
        });
    });
});
