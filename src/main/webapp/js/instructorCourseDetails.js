$(document).ready(function() {
    if ($('#button_sortstudentsection').length) {
        toggleSort($('#button_sortstudentsection'));
    } else {
        toggleSort($('#button_sortstudentteam'));
    }
    
    // auto select the html table when modal is shown
    $('#studentTableWindow').on('shown.bs.modal', function() {
        selectElementContents(document.getElementById('detailsTable'));
    });

    attachEventToRemindStudentsButton();
    attachEventToSendInviteLink();
    attachEventToDeleteStudentLink();
});

function submitFormAjax() {

    var formObject = $('#csvToHtmlForm');
    var formData = formObject.serialize();
    var content = $('#detailsTable');
    var ajaxStatus = $('#ajaxStatus');
    
    $.ajax({
        type: 'POST',
        url: '/page/instructorCourseDetailsPage?' + formData,
        beforeSend: function() {
            content.html("<img src='/images/ajax-loader.gif'/>");
        },
        error: function() {
            ajaxStatus.html('Failed to load student table. Please try again.');
            content.html('<button class="btn btn-info" onclick="submitFormAjax()"> retry</button>');
        },
        success: function(data) {
            setTimeout(function() {
                if (data.isError) {
                    ajaxStatus.html(data.errorMessage);
                    content.html('<button class="btn btn-info" onclick="submitFormAjax()"> retry</button>');
                } else {
                    var table = data.studentListHtmlTableAsString;
                    content.html('<small>' + table + '</small>');
                }

                setStatusMessage(data.statusForAjax);
            }, 500);
        }
    });
}

function attachEventToRemindStudentsButton() {
    $('#button_remind').on('click', function(event) {
        var $clickedButton = $(event.target);
        var messageText = 'Usually, there is no need to use this feature because TEAMMATES sends an automatic '
                          + 'invite to students at the opening time of each session. Send a join request to '
                          + 'all yet-to-join students in ' + $clickedButton.data('courseId') + ' anyway?';
        var okCallback = function() {
            window.location = $clickedButton.attr('href');
        };

        BootboxWrapper.showModalConfirmation('Confirm sending join requests', messageText, okCallback, null,
                BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.INFO);
    });
}

function attachEventToSendInviteLink() {
    $('.course-student-remind-link').on('click', function(event) {
        event.preventDefault();

        var $clickedLink = $(event.target);
        var messageText = 'Usually, there is no need to use this feature because TEAMMATES sends an automatic '
                          + 'invite to students at the opening time of each session. Send a join request anyway?';
        var okCallback = function() {
            window.location = $clickedLink.attr('href');
        };

        BootboxWrapper.showModalConfirmation('Confirm sending join request', messageText, okCallback, null,
                BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.INFO);
    });
}

/**
 * function that select the whole table
 * @param el
 */
function selectElementContents(el) {
    var body = document.body;
    var range;
    if (document.createRange && window.getSelection) {
        range = document.createRange();
        var sel = window.getSelection();
        sel.removeAllRanges();
        try {
            range.selectNodeContents(el);
            sel.addRange(range);
        } catch (e) {
            range.selectNode(el);
            sel.addRange(range);
        }
    } else if (body.createTextRange) {
        range = body.createTextRange();
        range.moveToElementText(el);
        range.select();
    }
}

var isShowCommentBox = false;
