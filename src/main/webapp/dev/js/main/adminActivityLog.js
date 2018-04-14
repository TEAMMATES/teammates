import {
    bindBackToTopButtons,
} from '../common/administrator';

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

function submitLocalTimeAjaxRequest(time, googleId, role, entry) {
    const params = `logUnixTimeMillis=${time}&logRole=${role}&logGoogleId=${googleId}`;

    const localTimeDisplay = $(entry).parent().children()[1];

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
                    $(localTimeDisplay).html(`<mark>${data.logLocalTime}</mark>`);
                }

                setStatusMessage(data.statusForAjax, BootstrapContextualColors.INFO);
            }, 500);
        },
    });
}

function setFormErrorMessage(button, msg) {
    button.after(`&nbsp;&nbsp;&nbsp;${msg}`);
}

function updateInfoForRecentActionButton() {
    const isShowAll = $('#ifShowAll').val();
    $('.ifShowAll_for_person').val(isShowAll);

    const isShowTestData = $('#ifShowTestData').val();
    $('.ifShowTestData_for_person').val(isShowTestData);
}

/**
 * Highlights default/search keywords in log messages.
 */
function highlightKeywordsInLogMessages() {
    const allLogMessages = $('.log-message');
    // highlight search keywords
    const searchKeywords = $('#query-keywords-for-info').val();
    const searchKeywordsList = searchKeywords.split(',');
    allLogMessages.highlight(searchKeywordsList);

    // highlight default keywords
    const defaultKeywords = $('#query-keywords-default-for-info').val();
    const defaultKeywordsList = defaultKeywords.split(',');
    allLogMessages.highlight(defaultKeywordsList, {
        element: 'b',
        className: ' ',
    });
}

function submitFormAjax(searchTimeOffset) {
    $('input[name=searchTimeOffset]').val(searchTimeOffset);

    const formObject = $('#ajaxLoaderDataForm');
    const formData = formObject.serialize();
    const $button = $('#button_older');
    const $logsTable = $('#activity-logs-table > tbody');

    $.ajax({
        type: 'POST',
        url: `/admin/adminActivityLogPage?${formData}`,
        beforeSend() {
            addLoadingIndicator($button, '');
        },
        error() {
            setFormErrorMessage($button, 'Failed to load older logs. Please try again.');
            removeLoadingIndicator($button, 'Retry');
        },
        success(data) {
            const $data = $(data);
            $logsTable.append($data.find('#activity-logs-table > tbody').html());
            updateInfoForRecentActionButton();
            highlightKeywordsInLogMessages();
            setStatusMessage($data.find('#status-message').html(), BootstrapContextualColors.INFO);
        },
    });
}

$(document).ready(() => {
    $('#filterReference').toggle();
    bindBackToTopButtons('.back-to-top-left, .back-to-top-right');
    highlightKeywordsInLogMessages();

    $(document).on('click', '#button_older', () => {
        const nextEndTimeToSearch = $('#button_older').attr('data-next-end-time-to-search');
        submitFormAjax(nextEndTimeToSearch);
    });

    $('#btn-toggle-reference').on('click', () => {
        toggleReference();
    });

    $(document).on('click', '.log-entry', (e) => {
        const $entry = $(e.currentTarget);
        const logTime = $entry.data('logtime');
        const googleId = $entry.data('googleid');
        const displayedRole = $entry.data('displayedrole');
        submitLocalTimeAjaxRequest(logTime, googleId, displayedRole, $entry);
    });
});
