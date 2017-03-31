/* global prepareInstructorPages:false
          registerResponseCommentsEvent:false
          registerResponseCommentCheckboxEvent:false
          enableHoverToDisplayEditOptions:false
          BootboxWrapper:false,
          StatusType:false,
          richTextEditorBuilder:false,
          tinymce:false,
          isBlank:false,
          setStatusMessage:false,
          StatusType:false,
          scrollToTop:false
 */

function showAddCommentBox(id) {
    $(`#comment_box_${id}`).show();
    $(`#commentText_${id}`).focus();
}

function hideAddCommentBox(id) {
    $(`#comment_box_${id}`).hide();
}

function submitCommentForm(commentIdx) {
    $(`#form_commentedit-${commentIdx}`).submit();
    return false;
}

function deleteComment(commentIdx) {
    const messageText = 'Are you sure you want to delete this comment?';
    const okCallback = function () {
        document.getElementById(`commentedittype-${commentIdx}`).value = 'delete';
        return submitCommentForm(commentIdx);
    };
    BootboxWrapper.showModalConfirmation('Confirm Deletion', messageText, okCallback, null,
                                         BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT,
                                         StatusType.WARNING);
    return false;
}

function enableComment(commentIdx) {
    $(`#commentBar-${commentIdx}`).hide();
    $(`#plainCommentText${commentIdx}`).hide();
    $(`div[id='commentTextEdit${commentIdx}']`).show();
    $(`textarea[id='commentText${commentIdx}']`).val($(`#plainCommentText${commentIdx}`).text());

    if (typeof richTextEditorBuilder !== 'undefined') {
        /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
        richTextEditorBuilder.initEditor(`#commentText${commentIdx}`, {
            inline: true,
            fixed_toolbar_container: `#rich-text-toolbar-comment-container-${commentIdx}`,
        });
        /* eslint-enable camelcase */
    }

    $(`textarea[id='commentText${commentIdx}']`).focus();
}

function disableComment(commentIdx) {
    $(`#commentBar-${commentIdx}`).show();
    $(`#plainCommentText${commentIdx}`).show();
    $(`div[id='commentTextEdit${commentIdx}']`).hide();
}

function checkComment(form, event) {
    if ($(form).find('[id^=commentedittype]').val() !== 'delete') {
        const formTextField = $(form).find('.mce-content-body');
        const editor = tinymce.get(formTextField.attr('id'));
        if (isBlank($(editor.getContent()).text())) {
            setStatusMessage("Please enter a valid comment. The comment can't be empty.", StatusType.DANGER);
            scrollToTop();
            event.preventDefault();
            event.stopPropagation();
        }
    }
}

function enableEdit(commentIdx) {
    enableComment(commentIdx);
    return false;
}

$(document).ready(() => {
    prepareInstructorPages();

    registerResponseCommentsEvent();
    registerResponseCommentCheckboxEvent();
    enableHoverToDisplayEditOptions();

    function isRedirectToSpecificComment() {
        return window.location.href.includes('#');
    }

    function getRedirectSpecificCommentRow() {
        const url = window.location.href;
        const start = url.indexOf('#');
        const end = url.length;
        const rowId = url.substring(start, end);
        const row = $(rowId);
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
    let scrollEventCounter = 0;
    $(window).scroll(() => {
        if (isRedirectToSpecificComment() && scrollEventCounter > 0) {
            $('.navbar').fadeIn('fast');
        }
        scrollEventCounter += 1;
    });

    // show on hover for comment
    $('.comments > .list-group-item').hover(function () {
        $("a[type='button']", this).show();
    }, function () {
        $("a[type='button']", this).hide();
    });

    // check submit text before submit
    $('form.form_comment').submit((e) => {
        if ($(e.currentTarget).find('[id^=commentedittype]').val() !== 'delete') {
            const commentTextInput = $(e.currentTarget).find('.mce-content-body');
            const commentTextId = commentTextInput.attr('id');
            const content = tinymce.get(commentTextId).getContent();
            $(e.currentTarget).find(`input[name=${commentTextId}]`).prop('disabled', true);
            $(e.currentTarget).find('input[name=commenttext]').val(content);
        }

        return checkComment(e.currentTarget, e);
    });

    // open or close show more options
    $('#option-check').click(() => {
        if ($('#option-check').is(':checked')) {
            $('#more-options').show();
        } else {
            $('#more-options').hide();
        }
    });

    function showCommentAndItsPanel(comment) {
        const commentToShow = $(comment);
        commentToShow.show();

        // to show student comments (only works for Giver filter)
        if (commentToShow.hasClass('student-record-comments')) {
            const studentCommentPanelBody = commentToShow.closest('.panel-body');
            studentCommentPanelBody.show();
        } else { // to show feedback question + feedback session panel
            const commentListRegionForFeedbackResponse = commentToShow.closest('tr');
            const rowsToShowClassName = commentListRegionForFeedbackResponse.attr('class');
            $(`.${rowsToShowClassName}`).show();

            const feedbackQuestion = commentListRegionForFeedbackResponse.closest('.feedback-question-panel');
            feedbackQuestion.show();

            const feedbackSessionPanelBody = feedbackQuestion.parent();
            feedbackSessionPanelBody.show();
        }
    }

    function hideCommentAndItsPanel(comment) {
        const commentToHide = $(comment);
        commentToHide.hide();

        // hide comment's add form in commentListRegionForFeedbackResponse
        $("li[id^='showResponseCommentAddForm']").hide();

        // to hide student comments
        if (commentToHide.hasClass('student-record-comments')) {
            const studentCommentPanel = commentToHide.closest('.student-comments-panel');
            const studentCommentPanelBody = commentToHide.closest('.panel-body');
            // if all student comments are hidden, then hide the student comments panel
            const allStudentCommentsAreHidden =
                    studentCommentPanel.find('div[class*="giver_display-by"][style*="display: none"]').length
                    === studentCommentPanel.find('div[class*="giver_display-by"]').length;
            if (allStudentCommentsAreHidden) {
                studentCommentPanelBody.hide();
            }
        } else { // to hide feedback question + feedback session panel
            const allCommentsForFeedbackResponseAreHidden =
                    commentToHide.parent().find('li[style*="display: none"]').length
                    === commentToHide.parent().find('li').length;
            if (allCommentsForFeedbackResponseAreHidden) {
                const commentListRegionForFeedbackResponse = commentToHide.closest('tr');
                const rowsToHideClassName = commentListRegionForFeedbackResponse.attr('class');
                $(`.${rowsToHideClassName}`).hide();

                const feedbackQuestion = commentListRegionForFeedbackResponse.closest('.feedback-question-panel');
                const allFeedbackResponsesForFeedbackQuestionAreHidden =
                        feedbackQuestion.find('tr[style*="display: none"]').length
                        === feedbackQuestion.find('tr[class*="table-row"]').length;
                if (allFeedbackResponsesForFeedbackQuestionAreHidden) {
                    feedbackQuestion.hide();

                    const feedbackSessionPanel = feedbackQuestion.closest('.feedback-session-panel');
                    const feedbackSessionPanelBody = feedbackQuestion.parent();
                    const allFeedbackQuestionsForFeedbackSessionAreHidden =
                            feedbackSessionPanel.find('div[class="panel panel-info"][style*="display: none"]').length
                            === feedbackSessionPanel.find('div[class="panel panel-info"]').length;
                    if (allFeedbackQuestionsForFeedbackSessionAreHidden) {
                        feedbackSessionPanelBody.hide();
                    }
                }
            }
        }
    }

    function showCommentOfPanelIndex(className) {
        $(className).each(function () {
            showCommentAndItsPanel(this);
        });
    }

    function hideCommentOfPanelIndex(className) {
        $(className).each(function () {
            hideCommentAndItsPanel(this);
        });
    }

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
        $("input[id^='panel_check']").each(function () {
            const panelIdx = $(this).attr('id').split('-')[1];
            if (this.checked) {
                $(`#panel_display-${panelIdx}`).show();
            } else {
                $(`#panel_display-${panelIdx}`).hide();
            }
        });
    }

    // Binding for "Display All" panel option
    $('#panel_all').click(() => {
        // use panel_all checkbox to control its children checkboxes.
        if ($('#panel_all').is(':checked')) {
            $('input[id^=panel_check]').prop('checked', true);
        } else {
            $('input[id^=panel_check]').prop('checked', false);
        }

        filterPanel();
    });

    // Binding for changes in the panel check boxes
    $('input[id^=panel_check]').change(() => {
        // based on the selected panel_check check boxes, check/uncheck panel_all check box
        if ($("input[id^='panel_check']:checked").length === $("input[id^='panel_check']").length) {
            $('#panel_all').prop('checked', true);
        } else {
            $('#panel_all').prop('checked', false);
        }

        filterPanel();
    });

    function filterGiverCheckbox(checkboxBy) {
        $(`input[id=giver_check-by-${checkboxBy}]`).each(function () {
            if (this.checked) {
                showCommentOfPanelIndex(`.giver_display-by-${checkboxBy}`);
            } else {
                hideCommentOfPanelIndex(`.giver_display-by-${checkboxBy}`);
            }
        });
    }

    function filterGiver() {
        filterGiverCheckbox('you');
        filterGiverCheckbox('others');
    }

    // Binding for "Display All" giver option
    $('#giver_all').click(() => {
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
    $('input[id^=giver_check]').change(() => {
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

    function filterStatusCheckbox(checkboxBy) {
        $(`input[id=status_check-${checkboxBy}]`).each(function () {
            if (this.checked) {
                showCommentOfPanelIndex(`.status_display-${checkboxBy}`);
            } else {
                hideCommentOfPanelIndex(`.status_display-${checkboxBy}`);
            }
        });
    }

    function filterStatus() {
        filterStatusCheckbox('public');
        filterStatusCheckbox('private');
    }

    // Binding for "Display All" status option
    $('#status_all').click(() => {
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
    $('input[id^=status_check]').change(() => {
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

    /**
     * Remove param and its value pair in the given url
     * Return the url without param and value pair
     */
    function removeParamInUrl(url, param) {
        let indexOfParam = url.indexOf(`?${param}`);
        indexOfParam = indexOfParam === -1 ? url.indexOf(`&${param}`) : indexOfParam;
        const indexOfAndSign = url.indexOf('&', indexOfParam + 1);
        const urlBeforeParam = url.substr(0, indexOfParam);
        const urlAfterParamValue = indexOfAndSign === -1 ? '' : url.substr(indexOfAndSign);
        return urlBeforeParam + urlAfterParamValue;
    }

    /**
     * Go to the url with appended param and value pair
     */
    function gotoUrlWithParam(url, param, value) {
        const paramValuePair = `${param}=${value}`;
        if (!url.includes('?')) {
            window.location.href = `${url}?${paramValuePair}`;
        } else if (!url.includes(param)) {
            window.location.href = `${url}&${paramValuePair}`;
        } else if (url.includes(paramValuePair)) {
            window.location.href = url;
        } else {
            const urlWithoutParam = removeParamInUrl(url, param);
            gotoUrlWithParam(urlWithoutParam, param, value);
        }
    }

    // Binding for "Display Archived Courses" check box.
    $('#displayArchivedCourses_check').change(function () {
        const urlToGo = $('#displayArchivedCourses_link > a').attr('href');
        if (this.checked) {
            gotoUrlWithParam(urlToGo, 'displayarchive', 'true');
        } else {
            gotoUrlWithParam(urlToGo, 'displayarchive', 'false');
        }
    });

    $('a[id^="visibility-options-trigger"]').click(function () {
        const visibilityOptions = $(this).parent().next();
        if (visibilityOptions.is(':visible')) {
            visibilityOptions.hide();
            $(this).html('<span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options');
        } else {
            visibilityOptions.show();
            $(this).html('<span class="glyphicon glyphicon-eye-close"></span> Hide Visibility Options');
        }
    });

    $('input[type=checkbox]').click((e) => {
        const table = $(e.currentTarget).closest('table');
        const form = table.closest('form');
        let visibilityOptions = [];
        const target = $(e.target);
        const visibilityOptionsRow = target.closest('tr');

        if (target.prop('class').includes('answerCheckbox') && !target.prop('checked')) {
            visibilityOptionsRow.find('input[class*=giverCheckbox]').prop('checked', false);
            visibilityOptionsRow.find('input[class*=recipientCheckbox]').prop('checked', false);
        }
        if ((target.prop('class').includes('giverCheckbox') || target.prop('class').includes('recipientCheckbox'))
                && target.prop('checked')) {
            visibilityOptionsRow.find('input[class*=answerCheckbox]').prop('checked', true);
        }

        table.find('.answerCheckbox:checked').each(function () {
            visibilityOptions.push($(this).val());
        });
        form.find("input[name='showcommentsto']").val(visibilityOptions.join(', '));

        visibilityOptions = [];
        table.find('.giverCheckbox:checked').each(function () {
            visibilityOptions.push($(this).val());
        });
        form.find("input[name='showgiverto']").val(visibilityOptions.join(', '));

        visibilityOptions = [];
        table.find('.recipientCheckbox:checked').each(function () {
            visibilityOptions.push($(this).val());
        });
        form.find("input[name='showrecipientto']").val(visibilityOptions.join(', '));
    });
});
