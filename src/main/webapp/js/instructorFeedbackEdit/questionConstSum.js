function updateConstSumPointsValue(questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    if ($('#' + FEEDBACK_QUESTION_CONSTSUMPOINTS + idSuffix).val() < 1) {
        $('#' + FEEDBACK_QUESTION_CONSTSUMPOINTS + idSuffix).val(1);
    }
}

function addConstSumOption(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var curNumberOfChoiceCreated = parseInt($('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val());
        
    $('<div class="margin-bottom-7px" id="constSumOptionRow-' + curNumberOfChoiceCreated + idSuffix + '">'
          + '<div class="input-group width-100-pc">'
              + '<input type="text" name="' + FEEDBACK_QUESTION_CONSTSUMOPTION + '-' + curNumberOfChoiceCreated + '" '
                      + 'id="' + FEEDBACK_QUESTION_CONSTSUMOPTION + '-' + curNumberOfChoiceCreated + idSuffix + '" '
                      + 'class="form-control constSumOptionTextBox">'
              + '<span class="input-group-btn">'
                  + '<button class="btn btn-default removeOptionLink" id="constSumRemoveOptionLink" '
                          + 'onclick="removeConstSumOption(' + curNumberOfChoiceCreated + ',' + questionNum + ')" '
                          + 'tabindex="-1">'
                      + '<span class="glyphicon glyphicon-remove"></span>'
                  + '</button>'
              + '</span>'
          + '</div>'
        + '</div>'
    ).insertBefore($('#constSumAddOptionRow' + idSuffix));

    $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val(curNumberOfChoiceCreated + 1);
    
    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function hideConstSumOptionTable(questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);
    $('#' + FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE + idSuffix).hide();
}

function removeConstSumOption(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    var $thisRow = $('#constSumOptionRow-' + index + idSuffix);
    
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

