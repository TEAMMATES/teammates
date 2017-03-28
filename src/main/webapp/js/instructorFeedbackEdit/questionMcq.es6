/* global
FEEDBACK_QUESTION_NUMBEROFCHOICECREATED:false, FEEDBACK_QUESTION_MCQCHOICE:false
*/

function addMcqOption(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const curNumberOfChoiceCreated =
            parseInt($(`#${FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(), 10);

    $(`
    <div id="mcqOptionRow-${curNumberOfChoiceCreated}-${questionNum}">
        <div class="input-group">
            <span class="input-group-addon">
                <input type="radio" disabled>
            </span>
            <input type="text" name="${FEEDBACK_QUESTION_MCQCHOICE}-${curNumberOfChoiceCreated}"
                    id="${FEEDBACK_QUESTION_MCQCHOICE}-${curNumberOfChoiceCreated}-${questionNum}"
                    class="form-control mcqOptionTextBox">
            <span class="input-group-btn">
                <button type="button" class="btn btn-default removeOptionLink" id="mcqRemoveOptionLink"
                        onclick="removeMcqOption(${curNumberOfChoiceCreated}, ${questionNum})" tabindex="-1">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
            </span>
        </div>
    </div>
    `).insertBefore($(`#mcqAddOptionRow-${questionNum}`));

    $(`#${FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeMcqOption(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const $thisRow = $(`#mcqOptionRow-${index}-${questionNum}`);

    // count number of child rows the table have and - 1 because of add option button
    const numberOfOptions = $thisRow.parent().children('div').length - 1;

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
    if (checkbox.checked) {
        $(`#mcqChoiceTable-${questionNum}`).find('input[type=text]').prop('disabled', true);
        $(`#mcqChoiceTable-${questionNum}`).hide();
        $(`#mcqGenerateForSelect-${questionNum}`).prop('disabled', false);
        $(`#mcqOtherOptionFlag-${questionNum}`).closest('.checkbox').hide();
        $(`#generatedOptions-${questionNum}`).attr('value',
                                                   $(`#mcqGenerateForSelect-${questionNum}`).prop('value'));
    } else {
        $(`#mcqChoiceTable-${questionNum}`).find('input[type=text]').prop('disabled', false);
        $(`#mcqChoiceTable-${questionNum}`).show();
        $(`#mcqGenerateForSelect-${questionNum}`).prop('disabled', true);
        $(`#mcqOtherOptionFlag-${questionNum}`).closest('.checkbox').show();
        $(`#generatedOptions-${questionNum}`).attr('value', 'NONE');
    }
}

function toggleMcqOtherOptionEnabled(checkbox, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function changeMcqGenerateFor(questionNum) {
    $(`#generatedOptions-${questionNum}`).attr('value',
                                               $(`#mcqGenerateForSelect-${questionNum}`).prop('value'));
}

/* exported
addMcqOption, removeMcqOption, toggleMcqGeneratedOptions, toggleMcqOtherOptionEnabled, changeMcqGenerateFor
*/
