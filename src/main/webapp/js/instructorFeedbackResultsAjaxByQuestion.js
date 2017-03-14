'use strict';

/* globals displayAjaxRetryMessageForPanelHeading:false,
           bindDefaultImageIfMissing:false,
           bindStudentPhotoLink:false,
           bindStudentPhotoHoverLink:false,
           toggleSingleCollapse:false,
           isPanelSetAsEmptyByBackend:false,
           displayAsEmptyPanel:false,
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
                var appendedQuestion = $(data).find('#questionBody-0').html();
                var $panelBody = $(panelBody[0]);
                $(data).remove();
                if (typeof appendedQuestion === 'undefined') {
                    $panelBody.removeClass('padding-0');
                    $panelBody.html('There are too many responses for this question. ' + 'Please view the responses one section at a time.');
                } else {
                    if (appendedQuestion.indexOf('resultStatistics') === -1) {
                        $panelBody.removeClass('padding-0');
                    }
                    $panelBody.html(appendedQuestion);
                }

                $panelBody.find('.profile-pic-icon-hover, .profile-pic-icon-click').children('img').each(function () {
                    bindDefaultImageIfMissing(this);
                });
                // bind the show picture onclick events
                bindStudentPhotoLink($panelBody.find('.profile-pic-icon-click > .student-profile-pic-view-link'));
                // bind the show picture onhover events
                bindStudentPhotoHoverLink($panelBody.find('.profile-pic-icon-hover'));

                $(panelHeading).removeClass('ajax_submit ajax_auto');

                displayIcon.html('<span class="glyphicon glyphicon-chevron-down pull-right"></span>');

                $(panelHeading).off('click');
                $(panelHeading).click(toggleSingleCollapse);
                $(panelHeading).trigger('click');

                if (isPanelSetAsEmptyByBackend($panelBody)) {
                    displayAsEmptyPanel($panelBody);
                }

                showHideStats();
            }
        });
    };

    var isPanelSetAsEmptyByBackend = function isPanelSetAsEmptyByBackend($panelBody) {
        return $panelBody.find('.no-response').length !== 0;
    };

    var displayAsEmptyPanel = function displayAsEmptyPanel($panelBody) {
        $panelBody.parents('.panel.panel-info').removeClass('panel-info').addClass('panel-default');
    };

    var $questionPanelHeadings = $('.ajax_submit,.ajax_auto');
    $questionPanelHeadings.click(seeMoreRequest);
    $('.ajax_auto').click();
});