function getAppendedResponseRateData(data) {
    var appendedResponseStatus = $(data).find('#responseStatus').html();
    $(data).remove();
    return appendedResponseStatus;
}

$(document).ready(function() {
    var responseRateRequest = function(e) {
        var panelHeading = $(this);
        var displayIcon = $(this).children('.display-icon');
        var formObject = $(this).children('form');
        var panelCollapse = $(this).parent().children('.panel-collapse');
        var formData = formObject.serialize();
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: $(formObject[0]).attr('action') + '?' + formData,
            beforeSend: function() {
                displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">')
                // submitButton.html('<img src="/images/ajax-loader.gif">');
            },
            error: function() {

            },
            success: function(data) {
                $(panelCollapse[0]).html(getAppendedResponseRateData(data));
                $(panelHeading).removeClass('ajax_response_rate_submit');
                $(panelHeading).off('click');
                displayIcon.html('<span class="glyphicon glyphicon-chevron-down pull-right"></span>')
                $(panelHeading).click(toggleSingleCollapse);
                $(panelHeading).trigger('click');
            }
        });
    };

    //ajax_response_rate_submit requires the user to click on it to load the noResponsePanel,
    //ajax_response_rate_auto automatically loads the noResponsePanel when the page is loaded
    $responseRatePanel = $('.ajax_response_rate_submit,.ajax_response_rate_auto');
    $responseRatePanel.click(responseRateRequest);
    $('.ajax_response_rate_auto').click();
});
