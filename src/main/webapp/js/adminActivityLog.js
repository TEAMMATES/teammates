'use strict';

$(document).ready(function() {
    $('#filterReference').toggle();
    AdminCommon.bindBackToTopButtons('.back-to-top-left, .back-to-top-right');
    highlightKeywordsInLogMessages();
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

function submitLocalTimeAjaxRequest(time, googleId, role, entry) {
    var params = 'logTimeInAdminTimeZone=' + time
                 + '&logRole=' + role
                 + '&logGoogleId=' + googleId;

    var link = $(entry);
    var localTimeDisplay = $(entry).parent().children()[1];

    var originalTime = $(link).html();

    $.ajax({
        type: 'POST',
        url: '/admin/adminActivityLogPage?' + params,
        beforeSend: function() {
            $(localTimeDisplay).html("<img src='/images/ajax-loader.gif'/>");
        },
        error: function() {
            $(localTimeDisplay).html('Loading error, please retry');
        },
        success: function(data) {
            setTimeout(function() {
                if (data.isError) {
                    $(localTimeDisplay).html('Loading error, please retry');
                } else {
                    $(link).parent().html(originalTime + '<mark><br>' + data.logLocalTime + '</mark>');
                }

                setStatusMessage(data.statusForAjax, StatusType.INFO);
            }, 500);
        }
    });
}

function submitFormAjax(searchTimeOffset) {
    $('input[name=searchTimeOffset]').val(searchTimeOffset);

    var formObject = $('#ajaxLoaderDataForm');
    var formData = formObject.serialize();
    var $button = $('#button_older');
    var $logsTable = $('#activity-logs-table > tbody');

    $.ajax({
        type: 'POST',
        url: '/admin/adminActivityLogPage?' + formData,
        beforeSend: function() {
            addLoadingIndicator($button, '');
        },
        error: function() {
            setFormErrorMessage($button, 'Failed to load older logs. Please try again.');
            removeLoadingIndicator($button, 'Retry');
        },
        success: function(data) {
            var $data = $(data);
            $logsTable.append($data.find('#activity-logs-table > tbody').html());
            updateInfoForRecentActionButton();
            highlightKeywordsInLogMessages();
            setStatusMessage($data.find('#status-message').html(), StatusType.INFO);
        }
    });
}

function setFormErrorMessage(button, msg) {
    button.after('&nbsp;&nbsp;&nbsp;' + msg);
}

function updateInfoForRecentActionButton() {
    var isShowAll = $('#ifShowAll').val();
    $('.ifShowAll_for_person').val(isShowAll);

    var isShowTestData = $('#ifShowTestData').val();
    $('.ifShowTestData_for_person').val(isShowTestData);
}

/**
 * Highlights default/search keywords in log messages.
 */
function highlightKeywordsInLogMessages() {
    var allLogMessages = $('.log-message');
    // highlight search keywords
    var searchKeywords = $('#query-keywords-for-info').val();
    var searchKeywordsList = searchKeywords.split(',');
    allLogMessages.highlight(searchKeywordsList);

    // highlight default keywords
    var defaultKeywords = $('#query-keywords-default-for-info').val();
    var defaultKeywordsList = defaultKeywords.split(',');
    allLogMessages.highlight(defaultKeywordsList, {
        element: 'b',
        className: ' '
    });
}
