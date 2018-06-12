/* global moment:false */

$(document).ready(() => {

    const $success_button = $('#button_confirm');

    const timezoneParameter = '&instructortimezone=' + moment.tz.guess();
    const oldLinkWithoutTimezone = $success_button.attr('href');
    const newLink = oldLinkWithoutTimezone + timezoneParameter;

    $success_button.attr('href', newLink);
});
