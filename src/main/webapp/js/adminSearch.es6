/* global highlightSearchResult:false StatusType:false setStatusMessage:false */

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
    });

    $('.openMailEditorWithDefaults').submit(function (e) {
        e.preventDefault();
        const receiveremailId = $('input[name=studentEmail]').val();
        const subjectType = $(this).find('input.subjectType').val();
        const studentName = $(this).find('input.studentName').val();
        const courseName = $(this).find('input.courseName').val();
        const Id = $(this).find('input.Id').val();
        const uri = encodeURIComponent($(this).find('input.url').val());
        const GoogleSignup = encodeURIComponent('https://accounts.google.com/NewAccount');
        const status = $(this).find('input.status').val();
        let subject = '';
        let bodycontent = '';
        if (subjectType === 'Invitation to join course') {
            subject = `TEAMMATES: ${subjectType} [${courseName}][Course ID: ${Id}]`;
            bodycontent =
                `Hello ${studentName}%0D%0AThe course ${courseName} `
                + 'is using the TEAMMATES System to collect feedback.%0D%'
                + '0ATo \'join\' the course, please go to this Web address: '
                + `${uri}%0D%0A*If prompted to log in, `
                + 'use your Googleaccount to log in. If you do not '
                + 'have a Google account, please create one from the '
                + `${GoogleSignup}`
                + '%0D%0A*The above link is unique to you. Please do not '
                + 'share it with your classmates.%0D%0ANote that If you '
                + 'wish to access TEAMMATES without using your Googleaccount, '
                + 'you do not need to \'join\' the course as instructed '
                + 'above. You will still be able to submit/view feedback '
                + 'by following the instructions sent to you by TEAMMATES at the '
                + 'appropriate times. However, we recommend joining the courseusing '
                + 'your Google account, because it gives you more convenient '
                + 'access to all your feedback stored in TEAMMATES.%0D%0A%0D%0A'
                + 'If you encounter any problems when using the system, you can '
                + 'email TEAMMATES support team at teammates@comp.nus.edu.sg.%0D%0A%0D%0A'
                + 'Regards,%0D%0ATEAMMATES Team.';
        } else {
            subject = `TEAMMATES: ${subjectType} [Course: ${courseName}][Feedback Session: ${Id}]`;
            bodycontent =
                `Hello ${studentName}`
                + `%0D%0AThe following feedback session is ${status}%0D%0A`
                + `Course: [${Id}][${courseName}]%0D%0AFeedback Session Name: ${Id}%0D%0A`
                + 'The link of the feedback for the above session, please go to this Web address: '
                + `${uri}%0D%0A*The above link is unique to you. Please `
                + 'do not share it with others.%0D%0A%0D%0AIf you encounter any problems when using '
                + 'the system, you can email TEAMMATES support team at teammates@comp.nus.edu.sg.'
                + '%0D%0A%0D%0ARegards,%0D%0ATEAMMATES Team.';
        }
        const EmailWrapper = `mailto:${receiveremailId}?Subject=${subject}&body=${bodycontent}`;
        window.location.href = EmailWrapper;
    });
});

function submitResetGoogleIdAjaxRequest(studentCourseId, studentEmail, wrongGoogleId, button) {
    const params = `studentemail=${studentEmail
                  }&courseid=${studentCourseId
                  }&googleid=${wrongGoogleId}`;

    const googleIdEntry = $(button).closest('.studentRow').find('.homePageLink');
    const originalButton = $(button).html();

    const originalGoogleIdEntry = $(googleIdEntry).html();

    $.ajax({
        type: 'POST',
        url: `/admin/adminStudentGoogleIdReset?${params}`,
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
                setStatusMessage(data.statusForAjax, StatusType.INFO);
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
/*
export default {
    submitResetGoogleIdAjaxRequest,
    adminSearchDiscloseAllStudents,
    adminSearchCollapseAllStudents,
    adminSearchDiscloseAllInstructors,
    adminSearchCollapseAllInstructors,
};
*/
/*
exported submitResetGoogleIdAjaxRequest,
         adminSearchDiscloseAllStudents,
         adminSearchCollapseAllStudents,
         adminSearchDiscloseAllInstructors,
         adminSearchCollapseAllInstructors
*/
