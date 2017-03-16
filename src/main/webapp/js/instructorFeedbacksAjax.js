'use strict';

var isSessionsAjaxSending = false;
var oldStatus = null;

$(document).ready(function() {
    oldStatus = $('.statusMessage').clone();
    $('#ajaxForSessions').submit(ajaxRequest);
});

var ajaxRequest = function(e) {
    e.preventDefault();

    if (isSessionsAjaxSending) {
        return;
    }

    var formData = $(this).serialize();
    $.ajax({
        type: 'POST',
        cache: false,
        url: $(this).attr('action') + '?' + formData,
        beforeSend: function() {
            isSessionsAjaxSending = true;
            $('#sessionList').html('<img height="75" width="75" class="margin-center-horizontal" '
                                   + 'src="/images/ajax-preload.gif"/>');
        },
        error: function() {
            isSessionsAjaxSending = false;
            $('#sessionList').html('');
            var msg = 'Failed to load sessions. Please <a href="#" onclick="loadSessionsByAjax()">click here</a> to retry.';
            setStatusMessage(msg, StatusType.DANGER);

            if (oldStatus !== null && oldStatus !== undefined && oldStatus !== '') {
                appendStatusMessage(oldStatus);
            }
        },
        success: function(data) {
            clearStatusMessages();
            appendStatusMessage(oldStatus);

            var appendedModalBody = $(data).find('#copySessionsBody').html();
            var appendedSessionTable = $(data).find('#sessionList').html();

            $('#button_copy').text('Copy from previous feedback sessions');
            $('#copySessionsBody').html(appendedModalBody);
            $('#sessionList').removeClass('align-center')
                             .html(appendedSessionTable);
            bindEventsAfterAjax();
        }
    });
};
