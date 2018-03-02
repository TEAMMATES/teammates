import {
    BootstrapContextualColors,
} from '../common/const';

import {
    makeCsrfTokenParam,
} from '../common/crypto';

import {
    setStatusMessage,
} from '../common/statusMessage';

import {
    highlightSearchResult,
} from '../common/ui';

function submitResetGoogleIdAjaxRequest(studentCourseId, studentEmail, wrongGoogleId, button) {
    const params = `studentemail=${studentEmail}&courseid=${studentCourseId}&googleid=${wrongGoogleId}`;

    const googleIdEntry = $(button).closest('.studentRow').find('.homePageLink');
    const originalButton = $(button).html();

    const originalGoogleIdEntry = $(googleIdEntry).html();

    $.ajax({
        type: 'POST',
        url: `/admin/adminStudentGoogleIdReset?${makeCsrfTokenParam()}&${params}`,
        beforeSend() {
            $(button).html("<img src='/images/ajax-loader.gif'/>");
        },
        error() {
            $(button).html('An Error Occurred, Please Retry');
        },
        success(data) {
            setTimeout(() => {
                if (data.isError) {
                    $(button).html('An Error Occurred, Please Retry');
                } else if (data.isGoogleIdReset) {
                    googleIdEntry.html('');
                    $(button).hide();
                } else {
                    googleIdEntry.html(originalGoogleIdEntry);
                    $(button).html(originalButton);
                }
                setStatusMessage(data.statusForAjax, BootstrapContextualColors.INFO);
            }, 500);
        },
    });
}

function adminSearchDiscloseAllStudents() {
    $('.fslink_student').slideDown();
    $('.studentRow').attr('class', 'studentRow active');
}

function adminSearchCollapseAllStudents() {
    $('.fslink_student').hide();
    $('.studentRow').attr('class', 'studentRow');
}

function adminSearchDiscloseAllInstructors() {
    $('.fslink_instructor').slideDown();
    $('.instructorRow').attr('class', 'instructorRow active');
}

function adminSearchCollapseAllInstructors() {
    $('.fslink_instructor').hide();
    $('.instructorRow').attr('class', 'instructorRow');
}

$(document).ready(() => {
    $('.fslink').hide();

    // highlight search string
    highlightSearchResult('#filterQuery', '.studentRow, .instructorRow');

    $('#rebuildButton').click(function () {
        $(this).val('true');
    });

    $('#searchButton').click(() => {
        $('#rebuildButton').val('false');
    });

    $('.studentRow').click(function () {
        const rawId = $(this).attr('id');
        if ($(this).attr('class') === 'studentRow active') {
            $(this).attr('class', 'studentRow');
        } else {
            $(this).attr('class', 'studentRow active');
        }
        $(`.fslink${rawId}`).toggle();
    });

    $('.instructorRow').click(function () {
        const rawId = $(this).attr('id');
        if ($(this).attr('class') === 'instructorRow active') {
            $(this).attr('class', 'instructorRow');
        } else {
            $(this).attr('class', 'instructorRow active');
        }
        $(`.fslink${rawId}`).toggle();
    });

    $('.homePageLink').click((e) => {
        e.stopPropagation();
    });

    $('.detailsPageLink').click((e) => {
        e.stopPropagation();
    });

    $('.optionButton').click((e) => {
        e.stopPropagation();
    });

    $('input').click(function () {
        this.select();
    });

    $('.resetGoogleIdButton').click((e) => {
        e.stopPropagation();
        const $entry = $(e.currentTarget);
        const courseId = $entry.data('courseid');
        const studentEmail = $entry.data('studentemail');
        const googleId = $entry.data('googleid');
        submitResetGoogleIdAjaxRequest(courseId, studentEmail, googleId, $entry);
    });

    $('#btn-disclose-all-instructors').on('click', () => {
        adminSearchDiscloseAllInstructors();
    });

    $('#btn-collapse-all-instructors').on('click', () => {
        adminSearchCollapseAllInstructors();
    });

    $('#btn-disclose-all-students').on('click', () => {
        adminSearchDiscloseAllStudents();
    });

    $('#btn-collapse-all-students').on('click', () => {
        adminSearchCollapseAllStudents();
    });

    $('.open-email-application-default-values').submit((e) => {
        e.preventDefault();
        const $entry = $(e.currentTarget);
        const crlf = encodeURIComponent('\r\n');
        const studentEmail = $entry.parentsUntil('.fslink_student').find('input[name=studentEmail]').val();
        const supportEmail = $entry.parentsUntil('.fslink_student').find('input[name=supportEmail]').val();
        const subjectType = $entry.find('input[name=subjectType]').val();
        const courseName = $entry.find('input[name=courseName]').val();
        const courseId = $entry.find('input[name=courseId]').val();
        const studentName = $entry.find('input[name=studentName]').val();
        const sessionStatus = $entry.find('input[name=sessionStatus]').val();
        const sessionName = $entry.find('#feedback-session-name').text();
        const sessionNameWithoutDate = sessionName.substring(0, sessionName.lastIndexOf(' ['));

        const relatedLink = encodeURIComponent($entry.find('input[name=relatedLink]').val());
        const googleSignup = encodeURIComponent('https://accounts.google.com/NewAccount');

        const bodyHeader = `Hello ${studentName},`;
        const bodyFooter =
                `${crlf}${crlf}If you encounter any problems when using the system,`
                + ` you can email TEAMMATES support team at ${supportEmail}.`
                + `${crlf}${crlf}Regards,${crlf}TEAMMATES Team.`;

        const uniqueLinkMessage = 'The above link is unique to you. Please do not share it with your classmates.';

        let subject = '';
        let bodyContent = '';

        if (subjectType === 'Invitation to join course') {
            subject = `TEAMMATES: ${subjectType} [${courseName}][Course ID: ${courseId}]`;
            bodyContent = `${crlf}${crlf}The course ${courseName} is using the TEAMMATES System to collect feedback.`
                    + ` ${crlf}${crlf}To 'join' the course, please go to this Web address: ${relatedLink}`
                    + ` ${crlf}${crlf}*If prompted to log in, use your Google account to log in. If you do not have`
                    + ` a Google account, please create one from the Google Accounts page: ${googleSignup}`
                    + ` ${crlf}*${uniqueLinkMessage} ${crlf}${crlf}Note that If you wish to access TEAMMATES`
                    + ' without using your Google account, you do not need to \'join\' the course as instructed above.'
                    + ' You will still be able to submit/view feedback by following the instructions sent to you by'
                    + ' TEAMMATES at the appropriate times. However, we recommend joining the course using your Google'
                    + ' account, because it gives you more convenient access to all your feedback stored in TEAMMATES.';
        } else {
            subject = `TEAMMATES: ${subjectType} [Course: ${courseName}][Feedback Session: ${sessionNameWithoutDate}]`;
            bodyContent =
                    `${crlf}${crlf}The following feedback session is ${sessionStatus}.${crlf}`
                    + `Course: [${courseId}][${courseName}]${crlf}Feedback Session Name: ${sessionName}${crlf}`
                    + `${crlf}The link to the feedback for the above session, please go to this Web`
                    + ` address: ${relatedLink} ${crlf}${crlf}*${uniqueLinkMessage}`;
        }

        const emailWrapper = `mailto:${studentEmail}`
                           + `?Subject=${encodeURI(subject)}`
                           + `&body=${encodeURI(bodyHeader + bodyContent + bodyFooter)}`;
        window.location.href = emailWrapper;
    });
});
