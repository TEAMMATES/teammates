import {
    BootstrapContextualColors,
} from '../common/const';

import {
    setStatusMessage,
} from '../common/statusMessage';

import {
    addLoadingIndicator,
    removeLoadingIndicator,
} from '../common/ui';

/**
 * Contains functions to be used to display email logs in `/adminEmailLog`
 */

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

function handler() {
    $(this).nextAll('.email-log-content-sanitized').first().toggle();
    $(this).nextAll('.email-log-content-unsanitized').first().toggle();
}

function bindClickAction() {
    $('body').unbind('click', handler).on('click', '.email-log-header', handler);
}

function setFormErrorMessage(button, msg) {
    button.after(`&nbsp;&nbsp;&nbsp;${msg}`);
}

/**
 * Highlights search keywords for different fields in email log messages.
 */
function highlightKeywordsInEmailLogMessages() {
    $('.email-receiver').highlight($('#query-keywords-for-receiver').val().split(','));
    $('.email-subject').highlight($('#query-keywords-for-subject').val().split(','));
    $('.email-content').highlight($('#query-keywords-for-content').val().split(','));
}

function submitFormAjax(offset) {
    $('input[name=offset]').val(offset);
    const formObject = $('#ajaxLoaderDataForm');
    const formData = formObject.serialize();
    const $button = $('#button_older');
    const $logsTable = $('#email-logs-table > tbody');

    $.ajax({
        type: 'POST',
        url: `/admin/adminEmailLogPage?${formData}`,
        beforeSend() {
            addLoadingIndicator($button, '');
        },
        error() {
            setFormErrorMessage($button, 'Failed to load older logs. Please try again.');
            removeLoadingIndicator($button, 'Retry');
        },
        success(data) {
            const $data = $(data);
            $logsTable.append($data.find('#email-logs-table > tbody').html());
            bindClickAction();
            highlightKeywordsInEmailLogMessages();
            setStatusMessage($data.find('#status-message').html(), BootstrapContextualColors.INFO);
        },
    });
}

$(document).ready(() => {
    bindClickAction();
    highlightKeywordsInEmailLogMessages();
    $('#filterReference').toggle();

    $(document).on('click', '#button_older', () => {
        const nextEndTimeToSearch = $('#button_older').attr('data-next-end-time-to-search');
        submitFormAjax(nextEndTimeToSearch);
    });

    $('#btn-toggle-reference').on('click', () => {
        toggleReference();
    });
});
