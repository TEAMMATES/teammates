function addMsqOption(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);

    var curNumberOfChoiceCreated =
            parseInt($('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val());
        
    $('<div id="msqOptionRow-' + curNumberOfChoiceCreated + idSuffix + '">'
          + '<div class="input-group">'
              + '<span class="input-group-addon">'
                 + '<input type="checkbox" disabled>'
              + '</span>'
              + '<input type="text" name="' + FEEDBACK_QUESTION_MSQCHOICE + '-' + curNumberOfChoiceCreated + '" '
                      + 'id="' + FEEDBACK_QUESTION_MSQCHOICE + '-' + curNumberOfChoiceCreated + idSuffix + '" '
                      + 'class="form-control msqOptionTextBox">'
              + '<span class="input-group-btn">'
                  + '<button type="button" class="btn btn-default removeOptionLink" id="msqRemoveOptionLink" '
                          + 'onclick="removeMsqOption(' + curNumberOfChoiceCreated + ',' + questionNum + ')" tabindex="-1">'
                      + '<span class="glyphicon glyphicon-remove"></span>'
                  + '</button>'
              + '</span>'
          + '</div>'
        + '</div>'
    ).insertBefore($('#msqAddOptionRow' + idSuffix));

    $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val(curNumberOfChoiceCreated + 1);
    
    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeMsqOption(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var $thisRow = $('#msqOptionRow-' + index + idSuffix);
    
    // count number of child rows the table have and - 1 because of add option button
    var numberOfOptions = $thisRow.parent().children('div').length - 1;
    
    if (numberOfOptions <= 1) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();
    
        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

function toggleMsqGeneratedOptions(checkbox, questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);

    if (checkbox.checked) {
        $('#msqChoiceTable' + idSuffix).find('input[type=text]').prop('disabled', true);
        $('#msqChoiceTable' + idSuffix).hide();
        $('#msqGenerateForSelect' + idSuffix).prop('disabled', false);
        $('#msqOtherOptionFlag' + idSuffix).closest('.checkbox').hide();
        $('#generatedOptions' + idSuffix).attr('value',
                                               $('#msqGenerateForSelect' + idSuffix).prop('value'));
    } else {
        $('#msqChoiceTable' + idSuffix).find('input[type=text]').prop('disabled', false);
        $('#msqChoiceTable' + idSuffix).show();
        $('#msqGenerateForSelect' + idSuffix).prop('disabled', true);
        $('#msqOtherOptionFlag' + idSuffix).closest('.checkbox').show();
        $('#generatedOptions' + idSuffix).attr('value', 'NONE');
    }
}

function changeMsqGenerateFor(questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);

    $('#generatedOptions' + idSuffix).attr('value',
                                           $('#msqGenerateForSelect' + idSuffix).prop('value'));
}

