'use strict';

function setDefaultContribQnVisibility(questionNum) {
    var $currentQuestionTable = $('#questionTable-' + questionNum);

    $currentQuestionTable.find('input.visibilityCheckbox').prop('checked', false);
    // All except STUDENTS can see answer
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('.answerCheckbox')
                         .not('[value="STUDENTS"]').prop('checked', true);
    // Only instructor can see giver
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('.giverCheckbox')
                         .filter('[value="INSTRUCTORS"]').prop('checked', true);
    // Recipient and instructor can see recipient
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('.recipientCheckbox')
                         .filter('[value="INSTRUCTORS"],[value="RECEIVER"]').prop('checked', true);

}

function setContribQnVisibilityFormat(questionNum) {
    var $currentQuestionTable = $('#questionTable-' + questionNum);

    // Format checkboxes 'Can See Answer' for recipient/giver's team members/recipient's team members must be the same.

    $currentQuestionTable.find('input.visibilityCheckbox').off('change');

    $currentQuestionTable.find('input.visibilityCheckbox').filter('.answerCheckbox').change(function() {
        if (!$(this).prop('checked')) {
            if ($(this).val() === 'RECEIVER'
                    || $(this).val() === 'OWN_TEAM_MEMBERS'
                    || $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
                $currentQuestionTable.find('input.visibilityCheckbox')
                                     .filter('input[class*="giverCheckbox"],input[class*="recipientCheckbox"]')
                                     .filter('[value="RECEIVER"],[value="OWN_TEAM_MEMBERS"],[value="RECEIVER_TEAM_MEMBERS"]')
                                     .prop('checked', false);
            } else {
                var visibilityOptionsRow = $(this).closest('tr');
                visibilityOptionsRow.find('input[class*="giverCheckbox"]')
                                         .prop('checked', false);
                visibilityOptionsRow.find('input[class*="recipientCheckbox"]')
                                         .prop('checked', false);
            }

        }

        if ($(this).val() === 'RECEIVER'
                || $(this).val() === 'OWN_TEAM_MEMBERS'
                || $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
            $currentQuestionTable.find('input.visibilityCheckbox')
                                 .filter('input[name=receiverFollowerCheckbox]')
                                 .prop('checked', $(this).prop('checked'));
        }

        if ($(this).val() === 'RECEIVER'
                || $(this).val() === 'OWN_TEAM_MEMBERS'
                || $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
            $currentQuestionTable.find('input.visibilityCheckbox')
                                 .filter('.answerCheckbox')
                                 .filter('[value="RECEIVER"],[value="OWN_TEAM_MEMBERS"],[value="RECEIVER_TEAM_MEMBERS"]')
                                 .prop('checked', $(this).prop('checked'));
        }
    });

    $currentQuestionTable.find('input.visibilityCheckbox').filter('[class*="giverCheckbox"]').change(function() {
        if ($(this).is(':checked')) {
            var visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input.answerCheckbox')
                                     .prop('checked', true)
                                     .trigger('change');
        }
    });

    $currentQuestionTable.find('input.visibilityCheckbox').filter('[class*="recipientCheckbox"]').change(function() {
        if ($(this).is(':checked')) {
            var visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input.answerCheckbox')
                                     .prop('checked', true)
                                     .trigger('change');
        }
    });

    $currentQuestionTable.find('input.visibilityCheckbox').filter('[name=receiverLeaderCheckbox]').change(function() {
        var visibilityOptionsRow = $(this).closest('tr');
        visibilityOptionsRow.find('input[name=receiverFollowerCheckbox]')
                                 .prop('checked', $(this).prop('checked'));
    });

}

function fixContribQnGiverRecipient(questionNum) {
    var $giverType = $('#givertype-' + questionNum);
    var $recipientType = $('#recipienttype-' + questionNum);
    var $questionTable = $('#questionTable-' + questionNum);

    // Fix giver->recipient to be STUDENT->OWN_TEAM_MEMBERS_INCLUDING_SELF
    $giverType.find('option').not('[value="STUDENTS"]').hide();
    $recipientType.find('option').not('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').hide();

    $giverType.find('option').not('[value="STUDENTS"]').prop('disabled', true);
    $recipientType.find('option').not('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').prop('disabled', true);

    $giverType.find('option').filter('[value="STUDENTS"]').prop('selected', true);
    $recipientType.find('option').filter('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').prop('selected', true);

    // simulate a click to update the text of the dropdown menu button
    $questionTable.find('.feedback-path-dropdown-option[data-giver-type="STUDENTS"]'
            + '[data-recipient-type="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').click();
    // the dropdown button is not an input tag and has no property "disabled", so .addClass is used
    $questionTable.find('.feedback-path-dropdown > button').addClass('disabled');
}
