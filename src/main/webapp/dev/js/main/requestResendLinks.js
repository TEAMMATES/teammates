import {
    setStatusMessageToForm,
} from '../common/statusMessage';

function hideComponents() {
    $('#message').hide();
    $('#email').hide();
    $('#recaptcha').hide();
    $('#submitButton').hide();
}

$(document).ready(() => {
    $('#requestForm').submit(() => {
        const $statusMessage = $('#statusMessagesToUser');
        $statusMessage.html('<img src="/images/ajax-loader.gif">');
        $statusMessage.css('display', 'block');

        $.ajax({
            url: '/page/resendLinks',
            type: 'POST',
            dataType: 'json',
            data: $('#requestForm').serialize(),
            success: (data) => {
                if (data.isValid) {
                    hideComponents();
                }
                setStatusMessageToForm(data.statusMessagesToUser[0].text,
                        data.statusMessagesToUser[0].color.toLowerCase(),
                        $('#requestForm'));
            },
        });
        return false;
    });
});
