'use strict';

var retryTimes = 0;
var numOfEntriesPerPage = 50;

$(document).ready(function() {
    bindClickAction();
    highlightKeywordsInEmailLogMessages();
    $('#filterReference').toggle();
});

function toggleReference() {
    $('#filterReference').toggle('slow');

    var button = $('#detailButton').attr('class');

    if (button === 'glyphicon glyphicon-chevron-down') {
        $('#detailButton').attr('class', 'glyphicon glyphicon-chevron-up');
        $('#referenceText').text('Hide Reference');
    } else {
        $('#detailButton').attr('class', 'glyphicon glyphicon-chevron-down');
        $('#referenceText').text('Show Reference');
    }
}

function bindClickAction() {
    $('body').unbind('click', handler).on('click', '.email-log-header', handler);
}

var handler = function() {
    $(this).nextAll('.email-log-content-sanitized').first().toggle();
    $(this).nextAll('.email-log-content-unsanitized').first().toggle();
};

function submitFormAjax(offset) {
    $('input[name=offset]').val(offset);
    var formObject = $('#ajaxLoaderDataForm');
    var formData = formObject.serialize();
    var button = $('#button_older');
    var lastLogRow = $('#emailLogsTable tr:last');

    $.ajax({
        type: 'POST',
        url: '/admin/adminEmailLogPage?' + formData,
        beforeSend: function() {
            button.html("<img src='/images/ajax-loader.gif'/>");
        },
        error: function() {
            setFormErrorMessage(button, 'Failed to load older logs. Please try again.');
            button.html('Retry');
        },
        success: function(data) {
            setTimeout(function() {
                if (data.isError) {
                    setFormErrorMessage(button, data.errorMessage);
                } else {
                    // Inject new log row
                    var logs = data.logs;
                    $.each(logs, function(i, value) {
                        lastLogRow.after(value.logInfoAsHtml);
                        lastLogRow = $('#emailLogsTable tr:last');
                        bindClickAction();
                        clickOlderButtonIfNeeded();
                    });
                }

                setStatusMessage(data.statusForAjax, StatusType.INFO);

            }, 500);
        }
    });
}

function setFormErrorMessage(button, msg) {
    button.after('&nbsp;&nbsp;&nbsp;' + msg);
}

/**
 * Highlights search keywords for different fields in email log messages.
 */
function highlightKeywordsInEmailLogMessages() {
    $('.email-receiver').highlight($('#query-keywords-for-receiver').val().split(','));
    $('.email-subject').highlight($('#query-keywords-for-subject').val().split(','));
    $('.email-content').highlight($('#query-keywords-for-content').val().split(','));
}

