/* global moment:false */

function updateJoinLinkWithTimezone() {
    const $successButton = $('#button_confirm');

    const timezoneParameter = '&instructortimezone='.concat(moment.tz.guess());
    const oldLinkWithoutTimezone = $successButton.attr('href');
    const newLink = oldLinkWithoutTimezone + timezoneParameter;

    $successButton.attr('href', newLink);
}

$(document).ready(() => {
    updateJoinLinkWithTimezone();
});
