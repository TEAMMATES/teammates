'use strict';

function linkAjaxForCourseStats() {
    var courseStatsClickHandler = function(e) {
        var row = $(this).closest('tr');
        var ajaxCols = $(row).children('td[id^="course-stats"]');
        var hyperlinkObject = $(this);

        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: $(this).attr('href'),
            beforeSend: function() {
                ajaxCols.html('<img class="course-stats-loader" src="/images/ajax-loader.gif"/>');
            },
            error: function() {
                $.each(ajaxCols, function(i, ajaxCol) {
                    var tryAgainLink = hyperlinkObject.clone();
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
            success: function(data) {
                $(ajaxCols[0]).text(data.courseDetails.stats.sectionsTotal);
                $(ajaxCols[1]).html(data.courseDetails.stats.teamsTotal);
                $(ajaxCols[2]).html(data.courseDetails.stats.studentsTotal);
                $(ajaxCols[3]).html(data.courseDetails.stats.unregisteredTotal);
            }
        });
    };
    $('td[id^="course-stats"] > a').click(courseStatsClickHandler);
}
