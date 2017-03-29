'use strict';

var STUDENT_LIMIT = 3000;
var PERFORMANCE_ISSUE_MESSAGE = 'Due to performance issue, it is not allowed to show more than ' + STUDENT_LIMIT
                                + ' students. Please deselect some courses to view student list of other courses.';
var numStudents = 0;

$(document).ready(function() {
    $('.ajax_submit').click(seeMoreRequest);
});

function transportSectionChoices() {
    var sectionChoices = $('.section-to-be-transported');
    sectionChoices.remove();
    $('#sectionChoices').append(sectionChoices);
    sectionChoices.removeClass('section-to-be-transported');
}

function transportTeamChoices() {
    var teamChoices = $('.team-to-be-transported');
    teamChoices.remove();
    $('#teamChoices').append(teamChoices);
    teamChoices.removeClass('team-to-be-transported');
}

function transportEmailChoices() {
    var emailChoices = $('.email-to-be-transported');
    emailChoices.remove();
    $('#emails').append(emailChoices);
    emailChoices.removeAttr('class'); // the email divs have no other class
}

function bindPhotos(courseIdx) {
    $('td[id^="studentphoto-c' + courseIdx + '"]').each(function() {

        $(this).children('.profile-pic-icon-click > img').each(function() {
            bindDefaultImageIfMissing(this);
        });
        bindStudentPhotoLink($(this).children('.profile-pic-icon-click').children('.student-profile-pic-view-link'));
    });
}

function numStudentsRetrieved() {
    var emailChoices = $('.email-to-be-transported');
    return emailChoices.length;
}

function showStudentLimitError(courseCheck, displayIcon) {
    courseCheck.prop('checked', false);
    setStatusMessage(PERFORMANCE_ISSUE_MESSAGE, StatusType.DANGER);
    displayIcon.html('');
}

function removeDataToBeTransported() {
    var sectionChoices = $('.section-to-be-transported');
    sectionChoices.remove();
    var teamChoices = $('.team-to-be-transported');
    teamChoices.remove();
    var emailChoices = $('.email-to-be-transported');
    emailChoices.remove();
}

var seeMoreRequest = function(e) {
    var panelHeading = $(this);
    var panelCollapse = $(this).parent().children('.panel-collapse');
    var panelBody = $(panelCollapse[0]).children('.panel-body');
    var displayIcon = $(this).children('.display-icon');
    var courseIndex = $(panelCollapse[0]).attr('id').split('-')[1];
    var courseCheck = $('#course_check-' + courseIndex);
    var courseNumStudents = parseInt($('#numStudents-' + courseIndex).val());

    if ($(panelHeading).attr('class').indexOf('ajax_submit') === -1) {
        clearStatusMessages();
        if ($(panelCollapse[0]).attr('class').indexOf('checked') === -1) {
            $(panelCollapse).collapse('show');
            $(panelCollapse[0]).addClass('checked');
            $(courseCheck).prop('checked', true);
        } else {
            $(panelCollapse[0]).collapse('hide');
            $(panelHeading).addClass('ajax_submit');
            $(panelBody[0]).html('');
            $(panelCollapse[0]).removeClass('checked');
            $(courseCheck).prop('checked', false);
            numStudents -= courseNumStudents;
        }
        checkCourseBinding(courseCheck);
    } else if (numStudents < STUDENT_LIMIT) {
        clearStatusMessages();
        var formObject = $(this).children('form');
        var courseIdx = $(formObject[0]).attr('class').split('-')[1];
        var formData = formObject.serialize();
        e.preventDefault();
        if (displayIcon.html().indexOf('img') === -1) {
            $.ajax({
                type: 'POST',
                url: $(formObject[0]).attr('action') + '?' + formData + '&courseidx=' + courseIdx,
                beforeSend: function() {
                    displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">');
                },
                error: function() {
                    var warningSign = '<span class="glyphicon glyphicon-warning-sign"></span>';
                    var errorMsg = '[ Failed to load. Click here to retry. ]';
                    errorMsg = '<strong style="margin-left: 1em; margin-right: 1em;">' + errorMsg + '</strong>';
                    displayIcon.html(warningSign + errorMsg);
                },
                success: function(data) {
                    $(panelBody[0]).html(data);

                    // Count number of students retrieved
                    courseNumStudents = numStudentsRetrieved();
                    $('#numStudents-' + courseIdx).val(courseNumStudents);

                    // If number of students shown is already more than the limit
                    // Do not show more, even if we can retrieve it, as browser will lag.
                    if (numStudents >= STUDENT_LIMIT) {
                        showStudentLimitError(courseCheck, displayIcon);
                        removeDataToBeTransported();
                        return;
                    }

                    // Show newly retrieved students
                    numStudents += courseNumStudents;
                    transportSectionChoices();
                    transportTeamChoices();
                    transportEmailChoices();
                    bindPhotos(courseIdx);

                    $(panelHeading).removeClass('ajax_submit');
                    displayIcon.html('');
                    if ($(panelCollapse[0]).attr('class').indexOf('in') === -1) {
                        $(panelHeading).trigger('click');
                    }
                }
            });
        }
    } else {
        // Do not make ajax call if students shown already above limit
        showStudentLimitError(courseCheck, displayIcon);
    }
};
