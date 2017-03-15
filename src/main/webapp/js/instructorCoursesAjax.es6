/* global setStatusMessage:false StatusType:false clearStatusMessages:false */
/* global appendStatusMessage:false toggleSort:false linkAjaxForCourseStats:false */

let isFetchingCourses = false;
let needsRetrying = false;

$(document).ready(() => {
    const ajaxRequest = function (e) {
        if (isFetchingCourses) {
            return;
        }
        e.preventDefault();
        const formData = $(this).serialize();
        $.ajax({
            type: 'POST',
            cache: false,
            url: `${$(this).attr('action')}?${formData}`,
            beforeSend() {
                $('#coursesList').html(
                    '<img height="75" width="75" class="margin-center-horizontal" src="/images/ajax-preload.gif"/>',
                );
                isFetchingCourses = true;
            },
            error() {
                isFetchingCourses = false;
                needsRetrying = true;
                $('#coursesList').html('');
                setStatusMessage(
                    'Courses could not be loaded. Click <a href="#" id="retryAjax">here</a> to retry.'
                , StatusType.WARNING);
                $('#retryAjax').click((ev) => {
                    ev.preventDefault();
                    $('#ajaxForCourses').trigger('submit');
                });
            },
            success(data) {
                isFetchingCourses = false;
                if (needsRetrying) {
                    clearStatusMessages();
                    needsRetrying = false;
                }

                const statusMessages = $(data).find('.statusMessage');
                appendStatusMessage(statusMessages);

                const appendedCoursesTable = $(data).find('#coursesList').html();
                $('#coursesList')
                    .removeClass('align-center')
                    .html(appendedCoursesTable);
                toggleSort($('#button_sortcourseid'));
                linkAjaxForCourseStats();
            },
        });
    };
    $('#ajaxForCourses').submit(ajaxRequest);
});
