function setDefaultContribQnVisibility(questionNum) {
    var idSuffix = questionNum || 'New';
    var idSuffix2 = questionNum || '';
    
    $currentQuestionTable = $('#questionTable' + idSuffix);

    $currentQuestionTable.find('input.visibilityCheckbox').prop('checked', false);
    // All except STUDENTS can see answer
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[class*="answerCheckbox' + idSuffix2 + '"]')
                         .not('[value="STUDENTS"]').prop('checked', true);
    // Only instructor can see giver
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[class*="giverCheckbox' + idSuffix2 + '"]')
                         .filter('[value="INSTRUCTORS"]').prop('checked', true);
    // Recipient and instructor can see recipient
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[class*="recipientCheckbox' + idSuffix2 + '"]')
                         .filter('[value="INSTRUCTORS"],[value="RECEIVER"]').prop('checked', true);

}

function setContribQnVisibilityFormat(questionNum) {
    var idSuffix = questionNum || 'New';

    $currentQuestionTable = $('#questionTable' + idSuffix);

    // Format checkboxes 'Can See Answer' for recipient/giver's team members/recipient's team members must be the same.

    $currentQuestionTable.find('input.visibilityCheckbox').off('change');
    
    $currentQuestionTable.find('input.visibilityCheckbox').filter('[class*="answerCheckbox"]').change(function() {
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
                                 .filter('[class*="answerCheckbox"]')
                                 .filter('[value="RECEIVER"],[value="OWN_TEAM_MEMBERS"],[value="RECEIVER_TEAM_MEMBERS"]')
                                 .prop('checked', $(this).prop('checked'));
        }
    });
    
    $currentQuestionTable.find('input.visibilityCheckbox').filter('[class*="giverCheckbox"]').change(function() {
        if ($(this).is(':checked')) {
            var visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input[class*="answerCheckbox"]')
                                     .prop('checked', true)
                                     .trigger('change');
        }
    });
    
    $currentQuestionTable.find('input.visibilityCheckbox').filter('[class*="recipientCheckbox"]').change(function() {
        if ($(this).is(':checked')) {
            var visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input[class*="answerCheckbox"]')
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
    var idSuffix = questionNum ? '-' + questionNum : '';
    var $giverType = $('#givertype' + idSuffix);
    var $recipientType = $('#recipienttype' + idSuffix);

    // Fix giver->recipient to be STUDENT->OWN_TEAM_MEMBERS_INCLUDING_SELF
    $giverType.find('option').not('[value="STUDENTS"], [value="CUSTOM"]').hide();
    $recipientType.find('option').not('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"], [value="CUSTOM"]').hide();

    $giverType.find('option').not('[value="STUDENTS"], [value="CUSTOM"]').prop('disabled', true);
    $recipientType.find('option').not('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"], [value="CUSTOM"]').prop('disabled', true);
    
    if (questionNum === undefined) {
        $giverType.find('option').filter('[value="STUDENTS"]').prop('selected', true);
        $recipientType.find('option').filter('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').prop('selected', true);
    }
}

