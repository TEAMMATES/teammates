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
        var messageText = 'Usually, there is no need to use this feature because TEAMMATES sends an '
                          + 'automatic invite to students at the opening time of each session. Send a join '
                          + 'request to all yet-to-join students in ' + $clickedButton.data('courseId')
                          + ' anyway?';
        if (confirm(messageText)) {
            window.location = $clickedButton.attr('href');
        }
    });
}

function attachEventToSendInviteLink() {
    var messageText = 'Usually, there is no need to use this feature because TEAMMATES sends an automatic '
                      + 'invite to students at the opening time of each session. Send a join request anyway?';

    $('.course-student-remind-link').on('click', function(event) {
        if (!confirm(messageText)) {
            event.preventDefault();
        }
    });
}

function attachEventToDeleteStudentLink() {
    $('.course-student-delete-link').on('click', function(event) {
        $clickedLink = $(event.target);
        var messageText = 'Are you sure you want to remove ' + $clickedLink.data('studentNameForJs')
                          + ' from the course ' + $clickedLink.data('courseNameForJs') + '?';

        if (!confirm(messageText)) {
            event.preventDefault();
        }
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
