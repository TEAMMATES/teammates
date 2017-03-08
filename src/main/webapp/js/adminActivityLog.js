'use strict';

$(document).ready(function() {
    $('#filterReference').toggle();
    AdminCommon.bindBackToTopButtons('.back-to-top-left, .back-to-top-right');

    $(document).on('click', '#button_older', function(e) {
        e.preventDefault();
        var nextEndTime = $(e.target).data('nextEndTime');
        getOlderLogEntriesByAjax(nextEndTime);
    });

    $(document).on('click', '#logsTable tbody a', function(e) {
        e.preventDefault();
        var data = $(e.target).data();
        submitLocalTimeAjaxRequest(data.time, data.googleId, data.role, this);
    });
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

/**
 * Converts the timestamp of a log entry into the admin's timezone via an Ajax request.
 *
 * @param {int} time of the log entry as seconds since epoch
 * @param {String} googleId of the logged in user
 * @param {String} role of the logged in user
 * @param {Object} entry the node in the DOM representing the original timestamp as a link
 */
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
            if (data.isError) {
                $(localTimeDisplay).html('Loading error, please retry');
            } else {
                $(link).parent().html(originalTime + '<mark><br>' + data.logLocalTime + '</mark>');
            }

            setStatusMessage(data.statusForAjax, StatusType.INFO);
        }
    });
}

/**
 * Sends an ajax request for older log entries matching the query.
 *
 * @param {int} searchTimeOffset
 */
function getOlderLogEntriesByAjax(searchTimeOffset) {
    $('input[name=searchTimeOffset]').val(searchTimeOffset);

    var formObject = $('#ajaxLoaderDataForm');
    var formData = formObject.serialize();
    var button = $('#button_older');

    $.ajax({
        type: 'POST',
        url: '/admin/adminActivityLogPage?' + formData,
        beforeSend: function() {
            button.html("<img src='/images/ajax-loader.gif'/>");
        },
        error: function() {
            setFormErrorMessage(button, 'Failed to load older logs. Please try again.');
            button.html('Retry');
        },
        success: function(data) {
            if (data.isError) {
                setFormErrorMessage(button, data.errorMessage);
            } else {
                // update log table with new entries
                updatePageWithNewLogsFromAjax(data, '#logsTable tbody');
                updateInfoForRecentActionButton();
            }

            setStatusMessage(data.statusForAjax, StatusType.INFO);
        }
    });
}

/**
 * Appends new log entries from ajax as children of the node specified by the selector
 *
 * @param {Object} response from the Ajax request
 * @param {String} selector the selector for the DOM node that log entries should be placed in
 */
function updatePageWithNewLogsFromAjax(response, selector) {
    var logs = response.logs;
    var $logContainer = $(selector);
    $.each(logs, function(i, value) {
        $logContainer.append(value.logInfoAsHtml);
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
