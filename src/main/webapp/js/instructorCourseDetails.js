var teamNames = [];
var teamNamesToSectionMap = [];

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
    
    $('#team-select option').each(function() {
        teamNames.push($(this).val());
    });
    
    if (isCourseHasSections()) {
        $('tr').each(function() {
            var team = $(this).find('td[id^="studentteam"]').text().trim();
            if (!(team in teamNamesToSectionMap)) {
                var section = $(this).find('td[id^="studentsection"]').text().trim();
                teamNamesToSectionMap[team] = section;
            }
        });
    }
    
    $('#new-team-name-input').keyup(function() {
        prepareRenameTeamModalUi();
    });
    
    $('#team-select').change(function() {
        prepareRenameTeamModalUi();
    });
    
    $('#rename-team-form').submit(function(e) {
        if (isNewTeamNameExists()) {
            e.preventDefault();
            var okCallback = function() {
                e.currentTarget.submit();
            };
            BootboxWrapper.showModalConfirmation(
                    'Merging Teams', 'You are about to merge two teams. This might result in the deletion of '
                    + 'feedback responses associated with the team being merged. Proceed?', okCallback, null,
                    'Proceed with merging teams.', BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.WARNING);
        }
    });
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

/**
 * Functions to trigger registration key sending to a specific student in the
 * course.
 * Currently no confirmation dialog is shown.
 */
function toggleSendRegistrationKey() {
    return confirm('Usually, there is no need to use this feature because TEAMMATES '
                   + 'sends an automatic invite to students at the opening time of each'
                   + ' session. Send a join request anyway?');
}

/**
 * Function to trigger registration key sending to every unregistered students
 * in the course.
 * @param courseID
 */
function toggleSendRegistrationKeysConfirmation(courseID) {
    return confirm('Usually, there is no need to use this feature because TEAMMATES'
                   + ' sends an automatic invite to students at the opening time of'
                   + ' each session. Send a join request to all yet-to-join students in '
                   + courseID + ' anyway?');
}

/**
 * Function that shows confirmation dialog for removing a student from a course
 * @param studentName
 * @param courseId
 * @returns
 */
function toggleDeleteStudentConfirmation(courseId, studentName) {
    return confirm('Are you sure you want to remove ' + studentName + ' from the course ' + courseId + '?');
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

function isNewTeamNameExists() {
    return teamNames.indexOf($('#new-team-name-input').val()) !== -1;
}

function isTeamNameChanged() {
    return $('#new-team-name-input').val() !== $('#team-select').val();
}

function prepareRenameTeamModalUi() {
    var teamNameInputValue = $('#new-team-name-input').val();
    var teamSelectValue = $('#team-select').val();
    
    if (teamNameInputValue.length === 0 || !isTeamNameChanged() || isTryingToMergeTeamsInDifferentSections()) {
        $('#rename-team-save-button').attr('disabled', true);
    } else {
        $('#rename-team-save-button').removeAttr('disabled');
    }
    
    if (isTryingToMergeTeamsInDifferentSections()) {
        $('#team-unable-to-merge-message .first-team').text(teamSelectValue);
        $('#team-unable-to-merge-message .second-team').text(teamNameInputValue);
        $('#team-unable-to-merge-message').removeClass('hidden');
    } else {
        $('#team-unable-to-merge-message').addClass('hidden');
    }
    
    if (isNewTeamNameExists() && isTeamNameChanged() && !isTryingToMergeTeamsInDifferentSections()) {
        $('#team-able-to-merge-message .first-team').text(teamSelectValue);
        $('#team-able-to-merge-message .second-team').text(teamNameInputValue);
        $('#team-able-to-merge-message').removeClass('hidden');
    } else {
        $('#team-able-to-merge-message').addClass('hidden');
    }
}

function isCourseHasSections() {
    return $('#button_sortsection-0').is(':visible');
}

function isTeamsInSameSection() {
    var teamNameInputValue = $('#new-team-name-input').val();
    var teamSelectValue = $('#team-select').val();
    return teamNamesToSectionMap[teamNameInputValue] === teamNamesToSectionMap[teamSelectValue];
}

function isTryingToMergeTeamsInDifferentSections() {
    return isCourseHasSections() && isTeamNameChanged() && isNewTeamNameExists() && !isTeamsInSameSection();
}
