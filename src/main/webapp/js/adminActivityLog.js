$(document).ready(function() {
    $('#filterReference').toggle();
    AdminCommon.bindBackToTopButtons();
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
    var button = $('#button_older');
    var lastLogRow = $('#logsTable tr:last');
    
    $.ajax({
        type: 'POST',
        url: '/admin/adminActivityLogPage?' + formData,
        beforeSend: function() {
            button.html("<img src='/images/ajax-loader.gif'/>");
        },
        error: function() {
            setFormErrorMessage(olderButton, 'Failed to load older logs. Please try again.');
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
                        lastLogRow = $('#logsTable tr:last');
                    });
                    
                    updateInfoForRecentActionButton();
                }

                setStatusMessage(data.statusForAjax, StatusType.INFO);
            }, 500);
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
