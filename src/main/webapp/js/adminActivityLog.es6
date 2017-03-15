/* global setStatusMessage:false StatusType:false bindBackToTopButtons:false */

$(document).ready(() => {
    $('#filterReference').toggle();
    bindBackToTopButtons('.back-to-top-left, .back-to-top-right');
});

function toggleReference() {
    $('#filterReference').toggle('slow');

    const button = $('#detailButton').attr('class');

    if (button === 'glyphicon glyphicon-chevron-down') {
        $('#detailButton').attr('class', 'glyphicon glyphicon-chevron-up');
        $('#referenceText').text('Hide Reference');
    } else {
        $('#detailButton').attr('class', 'glyphicon glyphicon-chevron-down');
        $('#referenceText').text('Show Reference');
    }
}

function setFormErrorMessage(button, msg) {
    button.after(`&nbsp;&nbsp;&nbsp;${msg}`);
}

function submitLocalTimeAjaxRequest(time, googleId, role, entry) {
    const params = `logTimeInAdminTimeZone=${time
                  }&logRole=${role
                  }&logGoogleId=${googleId}`;

    const link = $(entry);
    const localTimeDisplay = $(entry).parent().children()[1];

    const originalTime = $(link).html();

    $.ajax({
        type: 'POST',
        url: `/admin/adminActivityLogPage?${params}`,
        beforeSend() {
            $(localTimeDisplay).html("<img src='/images/ajax-loader.gif'/>");
        },
        error() {
            $(localTimeDisplay).html('Loading error, please retry');
        },
        success(data) {
            setTimeout(() => {
                if (data.isError) {
                    $(localTimeDisplay).html('Loading error, please retry');
                } else {
                    $(link).parent().html(`${originalTime}<mark><br>${data.logLocalTime}</mark>`);
                }

                setStatusMessage(data.statusForAjax, StatusType.INFO);
            }, 500);
        },
    });
}

function updateInfoForRecentActionButton() {
    const isShowAll = $('#ifShowAll').val();
    $('.ifShowAll_for_person').val(isShowAll);

    const isShowTestData = $('#ifShowTestData').val();
    $('.ifShowTestData_for_person').val(isShowTestData);
}

function submitFormAjax(searchTimeOffset) {
    $('input[name=searchTimeOffset]').val(searchTimeOffset);

    const formObject = $('#ajaxLoaderDataForm');
    const formData = formObject.serialize();
    const button = $('#button_older');
    let lastLogRow = $('#logsTable tr:last');

    $.ajax({
        type: 'POST',
        url: `/admin/adminActivityLogPage?${formData}`,
        beforeSend() {
            button.html("<img src='/images/ajax-loader.gif'/>");
        },
        error() {
            setFormErrorMessage(button, 'Failed to load older logs. Please try again.');
            button.html('Retry');
        },
        success(data) {
            setTimeout(() => {
                if (data.isError) {
                    setFormErrorMessage(button, data.errorMessage);
                } else {
                    // Inject new log row
                    const logs = data.logs;
                    $.each(logs, (i, value) => {
                        lastLogRow.after(value.logInfoAsHtml);
                        lastLogRow = $('#logsTable tr:last');
                    });

                    updateInfoForRecentActionButton();
                }

                setStatusMessage(data.statusForAjax, StatusType.INFO);
            }, 500);
        },
    });
}

/*
export default {
    toggleReference,
    submitLocalTimeAjaxRequest,
    submitFormAjax,
};
*/
/* exported toggleReference, submitLocalTimeAjaxRequest, submitFormAjax */
