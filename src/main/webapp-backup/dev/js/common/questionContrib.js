function setDefaultContribQnVisibilityIfNeeded(questionNum) {
    // If visibility options have already been copied from the previous contrib question, skip
    const hasPreviousQuestion = $('.questionTable').size() >= 2;
    if (hasPreviousQuestion) {
        const previousQuestionType = $('input[name="questiontype"]').eq(-2).val();
        if (previousQuestionType === 'CONTRIB') {
            return;
        }
    }

    const $currentQuestionTable = $(`#questionTable-${questionNum}`);

    $currentQuestionTable.find(
            'a[data-option-name="ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS"]').click();
}

function setContribQnVisibilityFormat(questionNum) {
    const $currentQuestionTable = $(`#questionTable-${questionNum}`);

    // Show only the two visibility options valid for contrib questions; hide the rest
    $currentQuestionTable.find('.visibility-options-dropdown-option')
            .not('[data-option-name="ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS"]')
            .not('[data-option-name="VISIBLE_TO_INSTRUCTORS_ONLY"]')
            .parent()
            .addClass('hidden');
    $currentQuestionTable.find('.visibility-options-dropdown .dropdown-menu .divider').addClass('hidden');

    // Format checkboxes 'Can See Answer' for recipient/giver's team members/recipient's team members must be the same.

    $currentQuestionTable.find('input.visibilityCheckbox').off('change');

    $currentQuestionTable.find('input.visibilityCheckbox').filter('.answerCheckbox').change(function () {
        if (!$(this).prop('checked')) {
            if ($(this).val() === 'RECEIVER'
                    || $(this).val() === 'OWN_TEAM_MEMBERS'
                    || $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
                $currentQuestionTable.find('input.visibilityCheckbox')
                        .filter('input[class*="giverCheckbox"],input[class*="recipientCheckbox"]')
                        .filter('[value="RECEIVER"],[value="OWN_TEAM_MEMBERS"],[value="RECEIVER_TEAM_MEMBERS"]')
                        .prop('checked', false);
            } else {
                const visibilityOptionsRow = $(this).closest('tr');
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

    $currentQuestionTable.find('input.visibilityCheckbox').filter('[class*="giverCheckbox"]').change(function () {
        if ($(this).is(':checked')) {
            const visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input.answerCheckbox')
                    .prop('checked', true)
                    .trigger('change');
        }
    });

    $currentQuestionTable.find('input.visibilityCheckbox').filter('[class*="recipientCheckbox"]').change(function () {
        if ($(this).is(':checked')) {
            const visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input.answerCheckbox')
                    .prop('checked', true)
                    .trigger('change');
        }
    });

    $currentQuestionTable.find('input.visibilityCheckbox').filter('[name=receiverLeaderCheckbox]').change(function () {
        const visibilityOptionsRow = $(this).closest('tr');
        visibilityOptionsRow.find('input[name=receiverFollowerCheckbox]')
                .prop('checked', $(this).prop('checked'));
    });
}

function fixContribQnGiverRecipient(questionNum) {
    const $giverType = $(`#givertype-${questionNum}`);
    const $recipientType = $(`#recipienttype-${questionNum}`);
    const $questionTable = $(`#questionTable-${questionNum}`);

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

export {
    fixContribQnGiverRecipient,
    setContribQnVisibilityFormat,
    setDefaultContribQnVisibilityIfNeeded,
};
