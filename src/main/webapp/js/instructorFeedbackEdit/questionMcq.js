function addMcqOption(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var curNumberOfChoiceCreated =
            parseInt($('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val());
    
    $('<div id="mcqOptionRow-' + curNumberOfChoiceCreated + idSuffix + '">'
          + '<div class="input-group">'
              + '<span class="input-group-addon">'
                 + '<input type="radio" disabled>'
              + '</span>'
              + '<input type="text" name="' + FEEDBACK_QUESTION_MCQCHOICE + '-' + curNumberOfChoiceCreated + '" '
                      + 'id="' + FEEDBACK_QUESTION_MCQCHOICE + '-' + curNumberOfChoiceCreated + idSuffix + '" '
                      + 'class="form-control mcqOptionTextBox">'
              + '<span class="input-group-btn">'
                  + '<button type="button" class="btn btn-default removeOptionLink" id="mcqRemoveOptionLink" '
                          + 'onclick="removeMcqOption(' + curNumberOfChoiceCreated + ',' + questionNum + ')" tabindex="-1">'
                      + '<span class="glyphicon glyphicon-remove"></span>'
                  + '</button>'
              + '</span>'
          + '</div>'
        + '</div>'
    ).insertBefore($('#mcqAddOptionRow' + idSuffix));

    $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val(curNumberOfChoiceCreated + 1);
    
    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeMcqOption(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var $thisRow = $('#mcqOptionRow-' + index + idSuffix);
    
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

function toggleMcqGeneratedOptions(checkbox, questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);

    if (checkbox.checked) {
        $('#mcqChoiceTable' + idSuffix).find('input[type=text]').prop('disabled', true);
        $('#mcqChoiceTable' + idSuffix).hide();
        $('#mcqGenerateForSelect' + idSuffix).prop('disabled', false);
        $('#mcqOtherOptionFlag' + idSuffix).closest('.checkbox').hide();
        $('#generatedOptions' + idSuffix).attr('value',
                                               $('#mcqGenerateForSelect' + idSuffix).prop('value'));
    } else {
        $('#mcqChoiceTable' + idSuffix).find('input[type=text]').prop('disabled', false);
        $('#mcqChoiceTable' + idSuffix).show();
        $('#mcqGenerateForSelect' + idSuffix).prop('disabled', true);
        $('#mcqOtherOptionFlag' + idSuffix).closest('.checkbox').show();
        $('#generatedOptions' + idSuffix).attr('value', 'NONE');
    }
}

function toggleMcqOtherOptionEnabled(checkbox, questionNum) {
    questionId = '#form_editquestion-' + questionNum;
    idSuffix = getQuestionIdSuffix(questionNum);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function changeMcqGenerateFor(questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);

    $('#generatedOptions' + idSuffix).attr('value',
                                           $('#mcqGenerateForSelect' + idSuffix).prop('value'));
}

