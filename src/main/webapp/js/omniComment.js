'use strict';

$(document).ready(function() {
    function isRedirectToSpecificComment() {
        return window.location.href.includes('#');
    }

    function getRedirectSpecificCommentRow() {
        var url = window.location.href;
        var start = url.indexOf('#');
        var end = url.length;
        var rowId = url.substring(start, end);
        var row = $(rowId);
        return row;
    }

    function highlightRedirectSpecificCommentRow(row) {
        row.toggleClass('list-group-item-warning list-group-item-success');
    }

    // for redirecting from search page, hide the header and highlight the specific comment row
    if (isRedirectToSpecificComment() && getRedirectSpecificCommentRow().length > 0) {
        $('.navbar').css('display', 'none');
        highlightRedirectSpecificCommentRow(getRedirectSpecificCommentRow());
    } else if (isRedirectToSpecificComment() && getRedirectSpecificCommentRow().length === 0) {
        // TODO: impl this, e.g. display a status msg that cannot find the comment etc
    }

    // re-display the hidden header
    var scrollEventCounter = 0;
    $(window).scroll(function() {
        if (isRedirectToSpecificComment() && scrollEventCounter > 0) {
            $('.navbar').fadeIn('fast');
        }
        scrollEventCounter++;
    });

    // show on hover for comment
    $('.comments > .list-group-item').hover(function() {
        $("a[type='button']", this).show();
    }, function() {
        $("a[type='button']", this).hide();
    });

    // check submit text before submit
    $('form.form_comment').submit(function() {
        if ($(this).find('[id^=commentedittype]').val() !== 'delete') {
            var commentTextInput = $(this).find('.mce-content-body');
            var commentTextId = commentTextInput.attr('id');
            var content = tinyMCE.get(commentTextId).getContent();
            $(this).find('input[name=' + commentTextId + ']').prop('disabled', true);
            $(this).find('input[name=commenttext]').val(content);
        }

        return checkComment(this);
    });

    // open or close show more options
    $('#option-check').click(function() {
        if ($('#option-check').is(':checked')) {
            $('#more-options').show();
        } else {
            $('#more-options').hide();
        }
    });

    // Binding for "Display All" panel option
    $('#panel_all').click(function() {
        // use panel_all checkbox to control its children checkboxes.
        if ($('#panel_all').is(':checked')) {
            $('input[id^=panel_check]').prop('checked', true);
        } else {
            $('input[id^=panel_check]').prop('checked', false);
        }

        filterPanel();
    });

    // Binding for changes in the panel check boxes
    $('input[id^=panel_check]').change(function() {
        // based on the selected panel_check check boxes, check/uncheck panel_all check box
        if ($("input[id^='panel_check']:checked").length === $("input[id^='panel_check']").length) {
            $('#panel_all').prop('checked', true);
        } else {
            $('#panel_all').prop('checked', false);
        }

        filterPanel();
    });

    function filterPanel() {
        // if no panel_check checkboxes are checked, show the no-comment box to user
        if ($("input[id^='panel_check']:checked").length === 0) {
            $('#no-comment-panel').show();
            // if all is checked, show giver and status for better user experience
            if ($('#panel_all').prop('checked')) {
                $('#giver_all').closest('.filter-options').show();
                $('#status_all').closest('.filter-options').show();
            } else {
                $('#giver_all').closest('.filter-options').hide();
                $('#status_all').closest('.filter-options').hide();
            }
        } else {
            $('#no-comment-panel').hide();
            $('#giver_all').closest('.filter-options').show();
            $('#status_all').closest('.filter-options').show();
        }

        // hide the panel accordingly based on panel_check checkbox
        $("input[id^='panel_check']").each(function() {
            var panelIdx = $(this).attr('id').split('-')[1];
            if (this.checked) {
                $('#panel_display-' + panelIdx).show();
            } else {
                $('#panel_display-' + panelIdx).hide();
            }
        });
    }

    // Binding for "Display All" giver option
    $('#giver_all').click(function() {
        // use giver_all checkbox to control its children checkboxes.
        if ($('#giver_all').is(':checked')) {
            $('input[id^=giver_check]').prop('checked', true);
            $('#status_all').prop('disabled', false);
            $('input[id^=status_check]').prop('disabled', false);
        } else {
            $('input[id^=giver_check]').prop('checked', false);
            $('#status_all').prop('disabled', true);
            $('input[id^=status_check]').prop('disabled', true);
        }

        filterGiver();
    });

    // Binding for changes in the giver checkboxes.
    $('input[id^=giver_check]').change(function() {
        // based on the selected checkboxes, check/uncheck giver_all checkbox
        if ($("input[id^='giver_check']:checked").length === $("input[id^='giver_check']").length) {
            $('#giver_all').prop('checked', true);
            $('#status_all').prop('disabled', false);
            $('input[id^=status_check]').prop('disabled', false);
        } else {
            $('#giver_all').prop('checked', false);
            $('#status_all').prop('disabled', true);
            $('input[id^=status_check]').prop('disabled', true);
        }

        filterGiver();
    });

    function filterGiver() {
        filterGiverCheckbox('you');
        filterGiverCheckbox('others');
    }

    function filterGiverCheckbox(checkboxBy) {
        $('input[id=giver_check-by-' + checkboxBy + ']').each(function() {
            if (this.checked) {
                showCommentOfPanelIndex('.giver_display-by-' + checkboxBy);
            } else {
                hideCommentOfPanelIndex('.giver_display-by-' + checkboxBy);
            }
        });
    }
    //
    // Binding for "Display All" status option
    $('#status_all').click(function() {
        // use status_all checkbox to control its children checkboxes.
        if ($('#status_all').is(':checked')) {
            $('input[id^=status_check]').prop('checked', true);
            $('#giver_all').prop('disabled', false);
            $('input[id^=giver_check]').prop('disabled', false);
        } else {
            $('input[id^=status_check]').prop('checked', false);
            $('#giver_all').prop('disabled', true);
            $('input[id^=giver_check]').prop('disabled', true);
        }

        filterStatus();
    });

    // Binding for changes in the status checkboxes.
    $('input[id^=status_check]').change(function() {
        // based on the selected checkboxes, check/uncheck status_all checkbox
        if ($("input[id^='status_check']:checked").length === $("input[id^='status_check']").length) {
            $('#status_all').prop('checked', true);
            $('#giver_all').prop('disabled', false);
            $('input[id^=giver_check]').prop('disabled', false);
        } else {
            $('#status_all').prop('checked', false);
            $('#giver_all').prop('disabled', true);
            $('input[id^=giver_check]').prop('disabled', true);
        }

        filterStatus();
    });

    function filterStatus() {
        filterStatusCheckbox('public');
        filterStatusCheckbox('private');
    }

    function filterStatusCheckbox(checkboxBy) {
        $('input[id=status_check-' + checkboxBy + ']').each(function() {
            if (this.checked) {
                showCommentOfPanelIndex('.status_display-' + checkboxBy);
            } else {
                hideCommentOfPanelIndex('.status_display-' + checkboxBy);
            }
        });
    }
    //

    function showCommentOfPanelIndex(className) {
        $(className).each(function() {
            showCommentAndItsPanel(this);
        });
    }

    function hideCommentOfPanelIndex(className) {
        $(className).each(function() {
            hideCommentAndItsPanel(this);
        });
    }

    function showCommentAndItsPanel(comment) {
        var commentToShow = $(comment);
        commentToShow.show();

        // to show student comments (only works for Giver filter)
        if (commentToShow.hasClass('student-record-comments')) {
            var studentCommentPanelBody = commentToShow.closest('.panel-body');
            studentCommentPanelBody.show();
        } else { // to show feedback question + feedback session panel
            var commentListRegionForFeedbackResponse = commentToShow.closest('tr');
            var rowsToShowClassName = commentListRegionForFeedbackResponse.attr('class');
            $('.' + rowsToShowClassName).show();

            var feedbackQuestion = commentListRegionForFeedbackResponse.closest('.feedback-question-panel');
            feedbackQuestion.show();

            var feedbackSessionPanelBody = feedbackQuestion.parent();
            feedbackSessionPanelBody.show();
        }
    }

    function hideCommentAndItsPanel(comment) {
        var commentToHide = $(comment);
        commentToHide.hide();

        // hide comment's add form in commentListRegionForFeedbackResponse
        $("li[id^='showResponseCommentAddForm']").hide();

        // to hide student comments
        if (commentToHide.hasClass('student-record-comments')) {
            var studentCommentPanel = commentToHide.closest('.student-comments-panel');
            var studentCommentPanelBody = commentToHide.closest('.panel-body');
            // if all student comments are hidden, then hide the student comments panel
            var allStudentCommentsAreHidden =
                    studentCommentPanel.find('div[class*="giver_display-by"][style*="display: none"]').length
                    === studentCommentPanel.find('div[class*="giver_display-by"]').length;
            if (allStudentCommentsAreHidden) {
                studentCommentPanelBody.hide();
            }
        } else { // to hide feedback question + feedback session panel
            var allCommentsForFeedbackResponseAreHidden =
                    commentToHide.parent().find('li[style*="display: none"]').length
                    === commentToHide.parent().find('li').length;
            if (allCommentsForFeedbackResponseAreHidden) {
                var commentListRegionForFeedbackResponse = commentToHide.closest('tr');
                var rowsToHideClassName = commentListRegionForFeedbackResponse.attr('class');
                $('.' + rowsToHideClassName).hide();

                var feedbackQuestion = commentListRegionForFeedbackResponse.closest('.feedback-question-panel');
                var allFeedbackResponsesForFeedbackQuestionAreHidden =
                        feedbackQuestion.find('tr[style*="display: none"]').length
                        === feedbackQuestion.find('tr[class*="table-row"]').length;
                if (allFeedbackResponsesForFeedbackQuestionAreHidden) {
                    feedbackQuestion.hide();

                    var feedbackSessionPanel = feedbackQuestion.closest('.feedback-session-panel');
                    var feedbackSessionPanelBody = feedbackQuestion.parent();
                    var allFeedbackQuestionsForFeedbackSessionAreHidden =
                            feedbackSessionPanel.find('div[class="panel panel-info"][style*="display: none"]').length
                            === feedbackSessionPanel.find('div[class="panel panel-info"]').length;
                    if (allFeedbackQuestionsForFeedbackSessionAreHidden) {
                        feedbackSessionPanelBody.hide();
                    }
                }
            }
        }
    }

    // Binding for "Display Archived Courses" check box.
    $('#displayArchivedCourses_check').change(function() {
        var urlToGo = $('#displayArchivedCourses_link > a').attr('href');
        if (this.checked) {
            gotoUrlWithParam(urlToGo, 'displayarchive', 'true');
        } else {
            gotoUrlWithParam(urlToGo, 'displayarchive', 'false');
        }
    });

    /**
     * Go to the url with appended param and value pair
     */
    function gotoUrlWithParam(url, param, value) {
        var paramValuePair = param + '=' + value;
        if (!url.includes('?')) {
            window.location.href = url + '?' + paramValuePair;
        } else if (!url.includes(param)) {
            window.location.href = url + '&' + paramValuePair;
        } else if (url.includes(paramValuePair)) {
            window.location.href = url;
        } else {
            var urlWithoutParam = removeParamInUrl(url, param);
            gotoUrlWithParam(urlWithoutParam, param, value);
        }
    }

    /**
     * Remove param and its value pair in the given url
     * Return the url withour param and value pair
     */
    function removeParamInUrl(url, param) {
        var indexOfParam = url.indexOf('?' + param);
        indexOfParam = indexOfParam === -1 ? url.indexOf('&' + param) : indexOfParam;
        var indexOfAndSign = url.indexOf('&', indexOfParam + 1);
        var urlBeforeParam = url.substr(0, indexOfParam);
        var urlAfterParamValue = indexOfAndSign === -1 ? '' : url.substr(indexOfAndSign);
        return urlBeforeParam + urlAfterParamValue;
    }

    $('a[id^="visibility-options-trigger"]').click(function() {
        var visibilityOptions = $(this).parent().next();
        if (visibilityOptions.is(':visible')) {
            visibilityOptions.hide();
            $(this).html('<span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options');
        } else {
            visibilityOptions.show();
            $(this).html('<span class="glyphicon glyphicon-eye-close"></span> Hide Visibility Options');
        }
    });

    $('input[type=checkbox]').click(function(e) {
        var table = $(this).closest('table');
        var form = table.closest('form');
        var visibilityOptions = [];
        var target = $(e.target);
        var visibilityOptionsRow = target.closest('tr');

        if (target.prop('class').includes('answerCheckbox') && !target.prop('checked')) {
            visibilityOptionsRow.find('input[class*=giverCheckbox]').prop('checked', false);
            visibilityOptionsRow.find('input[class*=recipientCheckbox]').prop('checked', false);
        }
        if ((target.prop('class').includes('giverCheckbox') || target.prop('class').includes('recipientCheckbox'))
                && target.prop('checked')) {
            visibilityOptionsRow.find('input[class*=answerCheckbox]').prop('checked', true);
        }

        table.find('.answerCheckbox:checked').each(function() {
            visibilityOptions.push($(this).val());
        });
        form.find("input[name='showcommentsto']").val(visibilityOptions.join(', '));

        visibilityOptions = [];
        table.find('.giverCheckbox:checked').each(function() {
            visibilityOptions.push($(this).val());
        });
        form.find("input[name='showgiverto']").val(visibilityOptions.join(', '));

        visibilityOptions = [];
        table.find('.recipientCheckbox:checked').each(function() {
            visibilityOptions.push($(this).val());
        });
        form.find("input[name='showrecipientto']").val(visibilityOptions.join(', '));
    });
});

// public functions:

function showAddCommentBox(id) {
    $('#comment_box_' + id).show();
    $('#commentText_' + id).focus();
}

function hideAddCommentBox(id) {
    $('#comment_box_' + id).hide();
}

function submitCommentForm(commentIdx) {
    $('#form_commentedit-' + commentIdx).submit();
    return false;
}

function deleteComment(commentIdx) {
    var messageText = 'Are you sure you want to delete this comment?';
    var okCallback = function() {
        document.getElementById('commentedittype-' + commentIdx).value = 'delete';
        return submitCommentForm(commentIdx);
    };
    BootboxWrapper.showModalConfirmation('Confirm Deletion', messageText, okCallback, null,
                                         BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT,
                                         StatusType.WARNING);
    return false;
}

function enableEdit(commentIdx) {
    enableComment(commentIdx);
    return false;
}

function enableComment(commentIdx) {
    $('#commentBar-' + commentIdx).hide();
    $('#plainCommentText' + commentIdx).hide();
    $("div[id='commentTextEdit" + commentIdx + "']").show();
    $("textarea[id='commentText" + commentIdx + "']").val($('#plainCommentText' + commentIdx).text());

    if (typeof richTextEditorBuilder !== 'undefined') {
        /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
        richTextEditorBuilder.initEditor('#commentText' + commentIdx, {
            inline: true,
            fixed_toolbar_container: '#rich-text-toolbar-comment-container-' + commentIdx
        });
        /* eslint-enable camelcase */
    }

    $("textarea[id='commentText" + commentIdx + "']").focus();
}

function disableComment(commentIdx) {
    $('#commentBar-' + commentIdx).show();
    $('#plainCommentText' + commentIdx).show();
    $("div[id='commentTextEdit" + commentIdx + "']").hide();
}

function checkComment(form) {
    if ($(form).find('[id^=commentedittype]').val() === 'delete') {
        return true;
    }
    var formTextField = $(form).find('.mce-content-body');
    var editor = tinymce.get(formTextField.attr('id'));
    if (isBlank($(editor.getContent()).text())) {
        setStatusMessage("Please enter a valid comment. The comment can't be empty.", StatusType.DANGER);
        scrollToTop();
        return false;
    }
}
