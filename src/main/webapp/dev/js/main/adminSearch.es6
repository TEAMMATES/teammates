import {
    StatusType,
} from '../common/const.es6';

import {
    makeCsrfTokenParam,
} from '../common/crypto.es6';

import {
    setStatusMessage,
} from '../common/statusMessage.es6';

import {
    highlightSearchResult,
} from '../common/ui.es6';

function submitResetGoogleIdAjaxRequest(studentCourseId, studentEmail, wrongGoogleId, button) {
    const params = `studentemail=${studentEmail
                  }&courseid=${studentCourseId
                  }&googleid=${wrongGoogleId}`;

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
        const entry = e.target;
        const courseId = $(entry).data('courseid');
        const studentEmail = $(entry).data('studentemail');
        const googleId = $(entry).data('googleid');
        submitResetGoogleIdAjaxRequest(courseId, studentEmail, googleId, entry);
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
});
