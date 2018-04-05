import {
    setStatusMessageToForm,
} from '../common/statusMessage';
import {
    BootstrapContextualColors,
} from '../common/const';

$(document).ready(() => {
    $('#requestForm').submit(() => {
        const $form = $('#requestForm');
        const $statusMessage = $('#statusMessagesToUser');
        $statusMessage.html('<img src="/images/ajax-loader.gif">');
        $statusMessage.css('display', 'block');

        $.ajax({
            url: '/requestResendLinkSuccess.jsp',
            type: 'POST',
            dataType: 'json',
            data: $('#requestForm').serialize(),
            success: (data) => {
                if (data.isValid) {
                    setStatusMessageToForm(data.message, BootstrapContextualColors.SUCCESS, $form);
                    $('#message').hide();
                    $('#email').hide();
                    $('#recaptcha').hide();
                    $('#submitButton').hide();
                } else {
                    $statusMessage.html(data.message);
                    setStatusMessageToForm(data.message, BootstrapContextualColors.DANGER, $form);
                }
            },
        });
        return false;
    });
});
