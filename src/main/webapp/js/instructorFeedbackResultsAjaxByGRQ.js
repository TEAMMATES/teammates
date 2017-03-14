'use strict';

/* globals displayAjaxRetryMessageForPanelHeading:false,
           isEmptySection:false,
           removeSection:false,
           bindDefaultImageIfMissing:false,
           bindStudentPhotoLink:false,
           bindStudentPhotoHoverLink:false,
           bindCollapseEvents:false,
           toggleSingleCollapse:false,
           showHideStats:false
*/
/* eslint-disable no-use-before-define */

$(document).ready(function () {
    var seeMoreRequest = function seeMoreRequest(e) {
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
            beforeSend: function beforeSend() {
                displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">');
            },
            error: function error() {
                displayAjaxRetryMessageForPanelHeading(displayIcon);
            },
            success: function success(data) {
                var $sectionBody = $(panelBody[0]);

                if (typeof data === 'undefined') {
                    $sectionBody.html('The results is too large to be viewed. ' + 'Please choose to view the results by questions or download the results.');
                } else {
                    var $appendedSection = $(data).find('#sectionBody-0');
                    var sectionId = $(panelHeading).attr('id').match(/section-(\d+)/)[1];

                    if (isEmptySection($appendedSection)) {
                        if (parseInt(sectionId, 10) === 0) {
                            removeSection(sectionId);
                            return;
                        }
                        $sectionBody.html('There are no responses for this section yet ' + 'or you do not have access to the responses collected so far.');
                    }

                    $(data).remove();
                    if (typeof $appendedSection === 'undefined') {
                        $sectionBody.html('There are no responses for this section yet ' + 'or you do not have access to the responses collected so far.');
                    } else {
                        $sectionBody.html($appendedSection.html());
                    }
                }

                $sectionBody.find('.profile-pic-icon-hover, .profile-pic-icon-click').children('img').each(function () {
                    bindDefaultImageIfMissing(this);
                });
                // bind the show picture onclick events
                bindStudentPhotoLink($sectionBody.find('.profile-pic-icon-click > .student-profile-pic-view-link'));
                // bind the show picture onhover events
                bindStudentPhotoHoverLink($sectionBody.find('.profile-pic-icon-hover'));

                $(panelHeading).removeClass('ajax_auto');
                $(panelHeading).off('click');
                displayIcon.html('<span class="glyphicon glyphicon-chevron-down"></span>');
                var childrenPanels = $sectionBody.find('div.panel');
                bindCollapseEvents(childrenPanels, 0);

                $(panelHeading).click(toggleSingleCollapse);
                $(panelHeading).trigger('click');
                showHideStats();
            }
        });
    };
    var $sectionPanelHeadings = $('.ajax_auto');
    $sectionPanelHeadings.click(seeMoreRequest);
    $('.ajax_auto').click();
});