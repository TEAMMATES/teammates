var COURSE_STUDENT_EDIT = 'Use this to edit the details of this student. <br>To edit multiple students in one go, you can use the enroll page: <br>Simply enroll students using the updated data and existing data will be updated accordingly';
var COURSE_STUDENT_DELETE = 'Delete the student and the corresponding evaluations from the course';
var COURSE_STUDENT_RECORDS = 'View all student\'s evaluations and feedbacks';
var STUDENT_LIMIT = 3000;
var PERFORMANCE_ISSUE_MESSAGE = 'Due to performance issue, it is not allowed to show more than ' + STUDENT_LIMIT  + ' students. Please deselect some courses to view student list of other courses.';
var numStudents = 0;

function sanitizeForHtml(str) {
    if (typeof str == 'undefined') {
        return '';
    }
    return str.replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
              .replace(/\'/g, '&quot;')
              .replace(/\//g, '&#x2f;')
              .replace(/\'/, '&#39;')
              // To ensure when apply sanitizeForHtml for multiple times, the string's still fine
              // Regex meaning: replace '&' with safe encoding, but not the one that is safe already
              .replace(/&(?!(amp;)|(lt;)|(gt;)|(quot;)|(#x2f;)|(#39;))/g, '&amp;');
}

function sanitizeForJs(str) {
    return sanitizeForHtml(str.replace(/\/\//g, "\\\\")
                              .replace(/\"/g, "\\\"")
                              .replace(/\'/g, "\\'")
                              .replace(/#/g, "\\#"));
}

function getCourseStudentDetailsLink(student, userId) {
    var link = '/page/instructorCourseStudentDetailsPage';
    link = addParamToUrl(link, 'courseid', student.course);
    link = addParamToUrl(link, 'studentemail', student.email);
    link = addParamToUrl(link, 'user', userId);
    return link;
}

function getCourseStudentEditLink(student, userId) {
    var link = '/page/instructorCourseStudentDetailsEdit';
    link = addParamToUrl(link,'courseid', student.course);
    link = addParamToUrl(link, 'studentemail', student.email);
    link = addParamToUrl(link, 'user', userId);
    return link;
}

function getCourseStudentDeleteLink(student, userId) {
    var link = '/page/instructorCourseStudentDelete';
    link = addParamToUrl(link, 'courseid', student.course);
    link = addParamToUrl(link, 'studentemail', student.email);
    link = addParamToUrl(link, 'user', userId);
    return link;
}

function getStudentRecordsLink(student, userId) {
    var link = '/page/instructorStudentRecordsPage';
    link = addParamToUrl(link, 'courseid', student.course);
    link = addParamToUrl(link, 'studentemail', student.email);
    link = addParamToUrl(link, 'user', userId);
    return link;
}

function addParamToUrl(url, key, value) {
    if (typeof key == 'undefined' || typeof value == 'undefined'
                                  || url.indexOf('?' + key + '=') != -1
                                  || url.indexOf('&' + key + '=') != -1) {
        return url;
    }
    url += url.indexOf('?') != -1 ? '&' : '?';
    url += key + '=' + escape(value);
    return url;
}

function transportSectionChoices() {
    var sectionChoices = $(".section-to-be-transported");
    sectionChoices.remove();
    $("#sectionChoices").append(sectionChoices);
    sectionChoices.removeClass("section-to-be-transported");
}

function transportTeamChoices() {
    var teamChoices = $(".team-to-be-transported");
    teamChoices.remove();
    $("#teamChoices").append(teamChoices);
    teamChoices.removeClass("team-to-be-transported");
}

function transportEmailChoices() {
    var emailChoices = $('div[id^="student_email-c"]');
    emailChoices.remove();
    $("#emails").append(emailChoices);
}

function bindPhotos(courseIdx) {
    $('td[id^="studentphoto-c' + courseIdx + '"]').each(function() {
        bindErrorImages($(this).children('.profile-pic-icon-click'));
        bindStudentPhotoLink($(this).children('.profile-pic-icon-click').children('.student-profile-pic-view-link'));
    });
}

var seeMoreRequest = function(e) {
    var panelHeading = $(this);
    var panelCollapse = $(this).parent().children('.panel-collapse');
    var panelBody = $(panelCollapse[0]).children('.panel-body');
    var displayIcon = $(this).children('.display-icon');
    var courseIdx = $(panelCollapse[0]).attr("id").split('-')[1];
    var courseCheck = $('#course_check-' + courseIdx);
    var courseNumStudents = parseInt($('#numStudents-' + courseIdx).val());
    
    if ($(panelHeading).attr('class').indexOf('ajax_submit') == -1) {
        setStatusMessage('', false);
        if ($(panelCollapse[0]).attr('class').indexOf('checked') != -1) {
            $(panelCollapse[0]).collapse('hide');
            $(panelHeading).addClass('ajax_submit');
            $(panelBody[0]).html('');
            $(panelCollapse[0]).removeClass('checked');
            $(courseCheck).prop('checked', false);
            numStudents -= courseNumStudents;
        } else {
            $(panelCollapse).collapse('show');
            $(panelCollapse[0]).addClass('checked');
            $(courseCheck).prop('checked', true);
        }
        checkCourseBinding(courseCheck);
    } else {
        numStudents += courseNumStudents;
        if (numStudents < STUDENT_LIMIT) {
            setStatusMessage('', false);
            var formObject = $(this).children('form');
            var courseIdx = $(formObject[0]).attr('class').split('-')[1];
            var formData = formObject.serialize();
            e.preventDefault();
            if (displayIcon.html().indexOf('img') == -1) {
                $.ajax({
                    type: 'POST',
                    url:   $(formObject[0]).attr('action') + '?' + formData + '&courseidx=' + courseIdx,
                    beforeSend: function() {
                        displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">')
                    },
                    error: function() {
                        numStudents -= courseNumStudents;
                        console.log('Error');
                    },
                    success: function(data) {
                        $(panelBody[0]).html(data);
                        transportSectionChoices();
                        transportTeamChoices();
                        transportEmailChoices();
                        bindPhotos(courseIdx);
                        $(panelHeading).removeClass('ajax_submit');
                        displayIcon.html('');
                        if ($(panelCollapse[0]).attr('class').indexOf("in") == -1) {
                            $(panelHeading).trigger('click');
                        }
                        $('[data-toggle="tooltip"]').tooltip({
                            html: true,
                            container: 'body'
                        });
                    }
                });
            }
        } else {
            numStudents -= courseNumStudents;
            courseCheck.prop('checked', false);
            setStatusMessage(PERFORMANCE_ISSUE_MESSAGE, true);
            displayIcon.html('');
        }
    }
};

$(document).ready(function() {
    $('.ajax_submit').click(seeMoreRequest);
});
