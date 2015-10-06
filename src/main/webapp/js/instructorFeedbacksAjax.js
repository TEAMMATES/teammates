var isSessionsAjaxSending = false;
var oldStatus = null;

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
            $('#sessionList').html('<img height="75" width="75" class="margin-center-horizontal" src="/images/ajax-preload.gif"/>');
        },
        error: function() {
            isSessionsAjaxSending = false;
            $('#sessionList').html('');
            var msg = 'Failed to load sessions. Please <a href="#" onclick="loadSessionsByAjax()">click here</a> to retry.';
            if (oldStatus) {
                msg = oldStatus.text() + '<br>' + msg;
            }
            setStatusMessage(msg, true);
        },
        success: function(data) {
            $(DIV_STATUS_MESSAGE).replaceWith(oldStatus);
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

$(document).ready(function(){
    oldStatus = $(DIV_STATUS_MESSAGE).clone();
    $('#ajaxForSessions').submit(ajaxRequest);
});