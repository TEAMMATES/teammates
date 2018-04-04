import {
    setStatusMessageToForm,
} from '../common/statusMessage';
import {
    BootstrapContextualColors,
} from '../common/const';

function validateEmail(event) {
    const emailRegex = new RegExp(['^(([^<>()[\\]\\\\.,;:\\s@"]+(\\.[^<>()[\\]\\\\.,;:\\s@"]+)*)|(".+"))@',
        '((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$'].join(''));
    const isValidEmail = emailRegex.test($('#email').val());
    const $form = $('#requestForm');
    const $statusMessage = $('#statusMessagesToUser');
    $statusMessage.html('<img src="/images/ajax-loader.gif">');
    $statusMessage.css('display', 'block');

    if (isValidEmail) {
        setStatusMessageToForm('Email Sent Successfully', BootstrapContextualColors.SUCCESS, $form);
        return true;
    }

    $statusMessage.html('Invalid Email Address.');
    event.preventDefault();
    setStatusMessageToForm('Invalid Email Address', BootstrapContextualColors.DANGER, $form);
    return false;
}

$(document).ready(() => {
    $('#submitButton').on('click', (event) => {
        validateEmail(event);
    });
});

window.validateEmail = validateEmail;
