import {
    ParamsNames,
} from './const';

function addMcqOption(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const curNumberOfChoiceCreated =
            parseInt($(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(), 10);
    const lastChoice = $(`#mcqChoices-${questionNum}`).children().last();
    const lastWeight = $(`#mcqWeights-${questionNum}`).children().last();

    // Insert choice
    $(`
    <div class="margin-bottom-7px mcqChoiceRow-${curNumberOfChoiceCreated}-${questionNum}"
            id="mcqOptionRow-${curNumberOfChoiceCreated}-${questionNum}">
        <div class="input-group">
            <span class="input-group-addon">
                <input type="radio" disabled>
            </span>
            <input type="text" name="${ParamsNames.FEEDBACK_QUESTION_MCQCHOICE}-${curNumberOfChoiceCreated}"
                    id="${ParamsNames.FEEDBACK_QUESTION_MCQCHOICE}-${curNumberOfChoiceCreated}-${questionNum}"
                    class="form-control mcqOptionTextBox">
            <span class="input-group-btn">
                <button type="button" class="btn btn-default removeOptionLink" id="mcqRemoveOptionLink"
                        onclick="removeMcqOption(${curNumberOfChoiceCreated}, ${questionNum})" tabindex="-1">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
            </span>
        </div>
    </div>
    `).insertAfter(lastChoice);

    // Insert Weight Cell
    $(`
    <div class="margin-bottom-7px mcqChoiceRow-${curNumberOfChoiceCreated}-${questionNum}">
        <input type="number" class="form-control nonDestructive" value="0"
                id="${ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT}-${curNumberOfChoiceCreated}-${questionNum}"
                name="${ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT}-${curNumberOfChoiceCreated}" step="0.01">
    </div>
    `).insertAfter(lastWeight);

    $(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeMcqOption(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const $thisRow = $(`.mcqChoiceRow-${index}-${questionNum}`);

    // count number of child rows the table have and - 1 because of add option button
    const numberOfOptions = $thisRow.parent().children('div').length - 1;

    if (numberOfOptions <= 1) {
        $thisRow.find('input[id^="mcqOption"]').val('');
        $thisRow.find('input[id^="mcqWeight"]').val(0);
    } else {
        $thisRow.remove();

        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

function changeMcqGenerateFor(questionNum) {
    $(`#mcqGeneratedOptions-${questionNum}`).val($(`#mcqGenerateForSelect-${questionNum}`).prop('value'));
}

function toggleMcqGeneratedOptions(checkbox, questionNum) {
    if ($(checkbox).prop('checked')) {
        $(`#mcqChoiceTable-${questionNum}`).find('input[type=text]').prop('disabled', true);
        $(`#mcqChoiceTable-${questionNum}`).hide();
        $(`#mcqGenerateForSelect-${questionNum}`).prop('disabled', false);
        $(`#mcqOtherOptionFlag-${questionNum}`).closest('.checkbox').hide();
        changeMcqGenerateFor(questionNum);
    } else {
        $(`#mcqChoiceTable-${questionNum}`).find('input[type=text]').prop('disabled', false);
        $(`#mcqChoiceTable-${questionNum}`).show();
        $(`#mcqGenerateForSelect-${questionNum}`).prop('disabled', true);
        $(`#mcqOtherOptionFlag-${questionNum}`).closest('.checkbox').show();
        $(`#mcqGeneratedOptions-${questionNum}`).val('NONE');
    }
}

function toggleMcqOtherOptionEnabled(checkbox, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

export {
    addMcqOption,
    changeMcqGenerateFor,
    removeMcqOption,
    toggleMcqGeneratedOptions,
    toggleMcqOtherOptionEnabled,
};
