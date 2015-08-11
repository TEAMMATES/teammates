$(document).ready(function() {
    var seeMoreRequest = function(e) {
        var panelHeading = $(this);
        if ($('#show-stats-checkbox').is(':checked')) {
            $(panelHeading).find('[id^="showStats-"]').val('on');
        } else {
            $(panelHeading).find('[id^="showStats-"]').val('off');
        }

        var displayIcon = $(this).find('.display-icon');
        var formObject = $(this).children('form');
        var panelCollapse = $(this).parent().children('.panel-collapse');
        var panelBody = $(panelCollapse[0]).children('.panel-body');
        var formData = formObject.serialize();
        e.preventDefault();
        $.ajax({
            type: 'POST',
            cache: false,
            url: $(formObject[0]).attr('action') + '?' + formData,
            beforeSend: function() {
                displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">');
            },
            error: function() {
                // TODO handle errors!
            },
            success: function(data) {
                var appendedQuestion = $(data).find('#questionBody-0').html();
                $(data).remove();
                if (typeof appendedQuestion != 'undefined') {
                    if (appendedQuestion.indexOf('resultStatistics') == -1) {
                        $(panelBody[0]).removeClass('padding-0');
                    }
                    $(panelBody[0]).html(appendedQuestion);
                } else {
                    $(panelBody[0]).removeClass('padding-0');
                    $(panelBody[0]).html('There are too many responses for this question. Please view the responses one section at a time.');
                }
                
                bindErrorImages($(panelBody[0]).find('.profile-pic-icon-hover, .profile-pic-icon-click'));
                // bind the show picture onclick events
                bindStudentPhotoLink($(panelBody[0]).find('.profile-pic-icon-click > .student-profile-pic-view-link'));
                // bind the show picture onhover events
                bindStudentPhotoHoverLink($(panelBody[0]).find('.profile-pic-icon-hover'));

                $(panelHeading).removeClass('ajax_submit ajax_auto');

                displayIcon.html('<span class="glyphicon glyphicon-chevron-down pull-right"></span>');

                $(panelHeading).off('click');
                $(panelHeading).click(toggleSingleCollapse);
                $(panelHeading).trigger('click');

                showHideStats();
            }
        });
    };

    var $questionPanelHeadings = $('.ajax_submit,.ajax_auto');
    $questionPanelHeadings.click(seeMoreRequest);
    $('.ajax_auto').click();
});
