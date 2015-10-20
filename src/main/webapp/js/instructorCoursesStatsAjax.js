function linkAjaxForCourseStats(){
    var courseStatsClickHandler = function(e) {
        var row = $(this).parent().parent();
        var ajaxCols = $(row).children('td[class^="course-stats"]');
        var hyperlinkObject = $(this).clone();

        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: $(this).attr('href'),
            beforeSend: function() {
                ajaxCols.html('<img src="/images/ajax-loader.gif"/>');
            },
            error: function() {
                ajaxCols.html('Failed. ')
                    .append(hyperlinkObject);
                hyperlinkObject.attr('data-toggle', 'tooltip')
                               .attr('data-placement', 'top')
                               .prop('title', 'Error occured while trying to fetch course stats. Click to retry.')
                               .html('Try again?')
                               .click(courseStatsClickHandler);
            },
            success: function(data) {
                $(ajaxCols[0]).text(data.courseDetails.stats.sectionsTotal);
                $(ajaxCols[1]).html(data.courseDetails.stats.teamsTotal);
                $(ajaxCols[2]).html(data.courseDetails.stats.studentsTotal);
                $(ajaxCols[3]).html(data.courseDetails.stats.unregisteredTotal);
            }
        });
    };
    $('td[class^="course-stats"] > a').click(courseStatsClickHandler);
}
