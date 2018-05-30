import {
    ParamsNames,
    BootstrapContextualColors,
} from '../common/const';

import {
    initializeTimeZoneOptions,
    prepareInstructorPages,
} from '../common/instructor';

import {
    toggleSort,
} from '../common/sortBy';

import {
    appendStatusMessage,
    clearStatusMessages,
    setStatusMessage,
} from '../common/statusMessage';

let isFetchingCourses = false;
let needsRetrying = false;

$(document).ready(() => {
    prepareInstructorPages();

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
                        '<img height="75" width="75" class="margin-center-horizontal" src="/images/ajax-preload.gif"/>'
                );
                isFetchingCourses = true;
            },
            error() {
                isFetchingCourses = false;
                needsRetrying = true;
                $('#coursesList').html('');
                setStatusMessage(
                        'Courses could not be loaded. Click <a href="javascript:;" id="retryAjax">here</a> to retry.',
                        BootstrapContextualColors.WARNING
                );
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
            },
        });
    };
    $('#ajaxForCourses').submit(ajaxRequest);
    $('#ajaxForCourses').trigger('submit');

    initializeTimeZoneOptions($(`#${ParamsNames.COURSE_TIME_ZONE}`));
});
