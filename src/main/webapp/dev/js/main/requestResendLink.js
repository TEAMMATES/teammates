import {
    setStatusMessage,
} from '../common/statusMessage';
import {
    BootstrapContextualColors,
} from '../common/const';

function validateEmail(event) {
    const emailRegex = new RegExp(['^(([^<>()[\\]\\\\.,;:\\s@"]+(\\.[^<>()[\\]\\\\.,;:\\s@"]+)*)|(".+"))@',
        '((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$'].join(''));
    const isValidEmail = emailRegex.test($('#email').val());

    if (isValidEmail) {
        setStatusMessage('Email Sent Successfully', BootstrapContextualColors.SUCCESS);
        return true;
    }

    alert('Invalid Email Address');
    event.preventDefault();
    setStatusMessage('Invalid Email Address', BootstrapContextualColors.DANGER);
    return false;
}

$(document).ready(() => {
    $('#submitButton').on('click', (event) => {
        validateEmail(event);
    });
});

window.validateEmail = validateEmail;
