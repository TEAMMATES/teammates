var numPanels = 0;
var recipientIndex = -1;

$(document).ready(function() {
    var seeMoreRequest = function(e) {
        var panelHeading = $(this);
        if ($('#show-stats-checkbox').is(':checked')) {
            $(panelHeading).find('[id^="showStats-"]').val('on');
        } else {
            $(panelHeading).find('[id^="showStats-"]').val('off');
        }
        $(panelHeading).find('[id^="mainIndex-"]').val(recipientIndex);

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
                displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">')
            },
            error: function() {

            },
            success: function(data) {
                if (numPanels == 0) {
                    numPanels += $('.panel-collapse').length;
                }

                if (typeof data == 'undefined') {
                    $(panelBody[0]).html('The results is too large to be viewed. Please choose to view the results by questions or download the results.');
                } else {
                    var appendedSection = $(data).find('#sectionBody-0').html();
                    $(data).remove();
                    if (typeof appendedSection != 'undefined') {
                        $(panelBody[0]).html(appendedSection);
                    } else {
                        $(panelBody[0]).html('There are no responses for this feedback session yet or you do not have access to the responses collected so far.');
                    }
                } 

                bindErrorImages($(panelBody[0]).find('.profile-pic-icon-hover, .profile-pic-icon-click'));
                // bind the show picture onclick events
                bindStudentPhotoLink($(panelBody[0]).find('.profile-pic-icon-click > .student-profile-pic-view-link'));
                // bind the show picture onhover events
                bindStudentPhotoHoverLink($(panelBody[0]).find('.profile-pic-icon-hover'));
                
                $(panelHeading).removeClass('ajax_submit');
                $(panelHeading).off('click');
                displayIcon.html('<span class="glyphicon glyphicon-chevron-down"></span>');
                var childrenPanels = $(panelBody[0]).find('div.panel');
                numPanels = bindCollapseEvents(childrenPanels, numPanels);

                $(panelBody[0]).find('form[class*="responseCommentAddForm"] > div > a').click(addCommentHandler);
                $(panelBody[0]).find('form[class*="responseCommentEditForm"] > div > a').click(editCommentHandler);
                $(panelBody[0]).find('form[class*="responseCommentDeleteForm"] > a').click(deleteCommentHandler);

                $('a[id^="collapse-panels-button-section-"],a[id^="collapse-panels-button-team-"]').off('click');
                $('a[id^="collapse-panels-button-section-"]').on('click', function() {
                    var isGroupByTeam = $('#frgroupbyteam').prop('checked');
                    var childPanelType = (isGroupByTeam) ? 'div.panel.panel-warning' : 'div.panel.panel-primary';
                    var panels = $(this).closest('.panel-success').children('.panel-collapse').find(childPanelType).children('.panel-collapse');
                    toggleCollapse(this, panels);
                });

                $('a[id^="collapse-panels-button-team-"]').on('click', function(){
                    var panels = $(this).closest('.panel-warning').children('.panel-collapse').find('div.panel.panel-primary').children('.panel-collapse');
                    toggleCollapse(this, panels);
                });

                $(panelHeading).click(toggleSingleCollapse);
                $(panelHeading).trigger('click');
                showHideStats();
                recipientIndex += $(panelBody[0]).find('.panel-primary').length;
            }
        });
    };
    $('.ajax_submit').click(seeMoreRequest);
});
