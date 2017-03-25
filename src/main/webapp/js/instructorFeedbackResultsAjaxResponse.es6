/* global displayAjaxRetryMessageForPanelHeading:false,
          toggleSingleCollapse:false
*/

$(document).ready(() => {
    const responseRateRequest = function (e) {
        const panelHeading = $(this);
        const displayIcon = $(this).children('.display-icon');
        const formObject = $(this).children('form');
        const panelCollapse = $(this).parent().children('.panel-collapse');
        const formData = formObject.serialize();
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: `${$(formObject[0]).attr('action')}?${formData}`,
            beforeSend() {
                displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">');
                // submitButton.html('<img src="/images/ajax-loader.gif">');
            },
            error() {
                displayAjaxRetryMessageForPanelHeading(displayIcon);
            },
            success(data) {
                $(panelCollapse[0]).html(getAppendedResponseRateData(data));
                $(panelHeading).removeClass('ajax-response-submit');
                $(panelHeading).off('click');
                displayIcon.html('<span class="glyphicon glyphicon-chevron-down pull-right"></span>');
                $(panelHeading).click(toggleSingleCollapse);
                $(panelHeading).trigger('click');
            },
        });
    };

    // ajax-response-submit requires the user to click on it to load the noResponsePanel,
    // ajax-response-auto automatically loads the noResponsePanel when the page is loaded
    const $responseRatePanel = $('.ajax-response-submit,.ajax-response-auto');
    $responseRatePanel.click(responseRateRequest);
    $('.ajax-response-auto').click();
});

function getAppendedResponseRateData(data) {
    const appendedResponseStatus = $(data).find('#responseStatus').html();
    $(data).remove();
    return appendedResponseStatus;
}
