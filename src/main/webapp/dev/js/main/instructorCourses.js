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

function linkAjaxForCourseStats() {
    const courseStatsClickHandler = function (e) {
        const row = $(this).closest('tr');
        const ajaxCols = $(row).children('td[id^="course-stats"]');
        const hyperlinkObject = $(this);

        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: $(this).attr('href'),
            beforeSend() {
                ajaxCols.html('<img class="course-stats-loader" src="/images/ajax-loader.gif"/>');
            },
            error() {
                $.each(ajaxCols, (i, ajaxCol) => {
                    const tryAgainLink = hyperlinkObject.clone();
                    $(ajaxCol).html('Failed. ')
                            .append(tryAgainLink);
                    tryAgainLink
                            .attr('data-toggle', 'tooltip')
                            .attr('data-placement', 'top')
                            .prop('title', 'Error occured while trying to fetch course stats. Click to retry.')
                            .html('Try again?')
                            .click(courseStatsClickHandler);
                });
            },
            success(data) {
                $(ajaxCols[0]).text(data.courseDetails.stats.sectionsTotal);
                $(ajaxCols[1]).html(data.courseDetails.stats.teamsTotal);
                $(ajaxCols[2]).html(data.courseDetails.stats.studentsTotal);
                $(ajaxCols[3]).html(data.courseDetails.stats.unregisteredTotal);
            },
        });
    };
    $('td[id^="course-stats"] > a').click(courseStatsClickHandler);
}

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
                linkAjaxForCourseStats();
            },
        });
    };
    $('#ajaxForCourses').submit(ajaxRequest);
    $('#ajaxForCourses').trigger('submit');

    initializeTimeZoneOptions($(`#${ParamsNames.COURSE_TIME_ZONE}`));
});
