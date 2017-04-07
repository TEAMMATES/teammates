/* global prepareDatepickers:false linkAjaxForResponseRate:false readyFeedbackPage:false richTextEditorBuilder:false
setStatusMessage:false, appendStatusMessage:false, clearStatusMessages:false, bindEventsAfterAjax:false, StatusType:false
loadSessionsByAjax:false prepareRemindModal:false prepareInstructorPages:false
*/

let isSessionsAjaxSending = false;
let oldStatus = null;

const ajaxRequest = function (e) {
    e.preventDefault();

    if (isSessionsAjaxSending) {
        return;
    }

    const formData = $(this).serialize();
    $.ajax({
        type: 'POST',
        cache: false,
        url: `${$(this).attr('action')}?${formData}`,
        beforeSend() {
            isSessionsAjaxSending = true;
            $('#sessionList').html('<img height="75" width="75" class="margin-center-horizontal" '
                                   + 'src="/images/ajax-preload.gif"/>');
        },
        error() {
            isSessionsAjaxSending = false;
            $('#sessionList').html('');
            $('#loadSessionsFailErrorMsg').on('click', loadSessionsByAjax);
            const msg = 'Failed to load sessions. '
                    + 'Please <a href="#" id="loadSessionsFailErrorMsg">click here</a> to retry.';
            setStatusMessage(msg, StatusType.DANGER);

            if (oldStatus !== null && oldStatus !== undefined && oldStatus !== '') {
                appendStatusMessage(oldStatus);
            }
        },
        success(data) {
            clearStatusMessages();
            appendStatusMessage(oldStatus);

            const appendedModalBody = $(data).find('#copySessionsBody').html();
            const appendedSessionTable = $(data).find('#sessionList').html();

            $('#button_copy').text('Copy from previous feedback sessions');
            $('#copySessionsBody').html(appendedModalBody);
            $('#sessionList').removeClass('align-center')
                             .html(appendedSessionTable);
            bindEventsAfterAjax();
        },
    });
};

$(document).ready(() => {
    prepareInstructorPages();

    oldStatus = $('.statusMessage').clone();
    $('#ajaxForSessions').submit(ajaxRequest);

    prepareDatepickers();
    linkAjaxForResponseRate();

    prepareRemindModal();

    if (typeof richTextEditorBuilder !== 'undefined') {
        /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
        richTextEditorBuilder.initEditor('#instructions', {
            inline: true,
            readonly: false,
            fixed_toolbar_container: '#richtext-toolbar-container',
        });
        /* eslint-enable camelcase */
    }

    readyFeedbackPage();
});
