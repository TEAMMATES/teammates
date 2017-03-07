'use strict';

var retryTimes = 0;
var numOfEntriesPerPage = 50;

$(document).ready(function() {
    bindClickAction();
    highlightKeywordsInEmailLogMessage();
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
    $('body').unbind('click', handler).on('click', '.log', handler);
}

var handler = function() {
    $(this).next('#small').toggle();
    $(this).next('#small').next('#big').toggle();
};

function submitFormAjax(offset) {
    $('input[name=offset]').val(offset);
    var formObject = $('#ajaxLoaderDataForm');
    var formData = formObject.serialize();
    var button = $('#button_older');
    var $logsTable = $('#emailLogsTable > tbody');

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
            var $data = $(data);
            $logsTable.append($data.find('#logs-table > tbody').html());
            bindClickAction();
            highlightKeywordsInEmailLogMessage();
            setStatusMessage($data.find('#status-message').html(), StatusType.INFO);
        }
    });
}

function setFormErrorMessage(button, msg) {
    button.after('&nbsp;&nbsp;&nbsp;' + msg);
}

/**
 * Highlights default/search keywords in eamil log message.
 */
function highlightKeywordsInEmailLogMessage() {
    $('.email-receiver').highlight($('#query-keywords-receiver').val().split(','));
    $('.email-subject').highlight($('#query-keywords-subject').val().split(','));
    $('.email-content').highlight($('#query-keywords-content').val().split(','));
}

