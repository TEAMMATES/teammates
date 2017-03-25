

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

/*
export default {
    linkAjaxForCourseStats,
};
*/
