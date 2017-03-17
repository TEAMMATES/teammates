/* global StatusType:false setStatusMessage:false */
/**
 * Contains functions to be used to display email logs in `/adminEmailLog`
 */

let retryTimes = 0;
const numOfEntriesPerPage = 50;

$(document).ready(() => {
    bindClickAction();
    clickOlderButtonIfNeeded();
    $('#filterReference').toggle();
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

function bindClickAction() {
    $('body').unbind('click', handler).on('click', '.log', handler);
}

function handler() {
    $(this).next('#small').toggle();
    $(this).next('#small').next('#big').toggle();
}

function clickOlderButtonIfNeeded() {
    if (retryTimes >= 20) {
        return;
    }

    const curNumOfEntries = $('#emailLogsTable tbody tr').length;

    if (curNumOfEntries < numOfEntriesPerPage) {
        if ($('#button_older').length) {
            $('#button_older').click();
            retryTimes += 1;
        }
    }
}

function submitFormAjax(offset) {
    $('input[name=offset]').val(offset);
    const formObject = $('#ajaxLoaderDataForm');
    const formData = formObject.serialize();
    const button = $('#button_older');
    let lastLogRow = $('#emailLogsTable tr:last');

    $.ajax({
        type: 'POST',
        url: `/admin/adminEmailLogPage?${formData}`,
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
                        lastLogRow = $('#emailLogsTable tr:last');
                        bindClickAction();
                        clickOlderButtonIfNeeded();
                    });
                }

                setStatusMessage(data.statusForAjax, StatusType.INFO);
            }, 500);
        },
    });
}

function setFormErrorMessage(button, msg) {
    button.after(`&nbsp;&nbsp;&nbsp;${msg}`);
}

/*
export default {
    toggleReference,
    submitFormAjax,
};
*/
/* exported toggleReference, submitFormAjax */
