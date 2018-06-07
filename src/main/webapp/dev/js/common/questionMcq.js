import {
    ParamsNames,
} from './const';

function addMcqOption(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const curNumberOfChoiceCreated =
            parseInt($(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(), 10);
    const lastChoice = $(`#mcqChoices-${questionNum}`).children().last();
    const lastWeight = $(`#mcqWeights-${questionNum}`).children().last();

    const choice = (`
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
    `);

    const weight = (`
    <div class="margin-bottom-7px mcqChoiceRow-${curNumberOfChoiceCreated}-${questionNum}">
        <input type="number" class="form-control nonDestructive" value="0"
                id="${ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT}-${curNumberOfChoiceCreated}-${questionNum}"
                name="${ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT}-${curNumberOfChoiceCreated}" step="0.01">
    </div>
    `);

    if (curNumberOfChoiceCreated === 0) {
        $(`#mcqChoices-${questionNum}`).html(choice);
        $(`#mcqWeights-${questionNum}`).html(weight);
    } else {
        $(choice).insertAfter(lastChoice);
        $(weight).insertAfter(lastWeight);
    }

    $(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeMcqOption(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const $thisRow = $(`.mcqChoiceRow-${index}-${questionNum}`);

    // count number of child of mcqChoices div
    const numberOfOptions = $(`#mcqChoices-${questionNum}`).children('div').length;

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
        $(`#mcqChoices-${questionNum}`).find('input[type=text]').prop('disabled', true);
        $(`#mcqWeights-${questionNum}`).find('input[type=number]').prop('disabled', true);
        $(`#mcqChoiceTable-${questionNum}`).hide();
        $(`#mcqAssignWeights-${questionNum}`).parent().hide();
        $(`#mcqGenerateForSelect-${questionNum}`).prop('disabled', false);
        $(`#mcqOtherOptionFlag-${questionNum}`).closest('.checkbox').hide();
        $(`#mcqOtherWeight-${questionNum}`).parent().hide();
        changeMcqGenerateFor(questionNum);
    } else {
        $(`#mcqChoices-${questionNum}`).find('input[type=text]').prop('disabled', false);
        $(`#mcqWeights-${questionNum}`).find('input[type=number]').prop('disabled', false);
        $(`#mcqChoiceTable-${questionNum}`).show();
        $(`#mcqAssignWeights-${questionNum}`).parent().show();
        $(`#mcqGenerateForSelect-${questionNum}`).prop('disabled', true);
        $(`#mcqOtherOptionFlag-${questionNum}`).closest('.checkbox').show();
        $(`#mcqOtherWeight-${questionNum}`).parent().show();
        $(`#mcqGeneratedOptions-${questionNum}`).val('NONE');
    }
}

/**
 * If the 'other' option and Assign weight both are checked, shows the 'other' option,
 * otherwise hides it.
 * @param checkbox
 * @param questionNum
 */
function toggleMcqOtherOptionEnabled(checkbox, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    // The 'Choices are weighted' checkbox
    const $mcqAssignWeightCheckbox = $(`#mcqAssignWeights-${questionNum}`);
    const $mcqOtherWeightCell = $(`#mcqOtherWeight-${questionNum}`).parent();

    if ($(checkbox).prop('checked') && $mcqAssignWeightCheckbox.prop('checked')) {
        $mcqOtherWeightCell.show();
    } else {
        $mcqOtherWeightCell.hide();
    }

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function bindMcqOtherOptionEnabled() {
    $('body').on('click', 'input[id^="mcqOtherOptionFlag"]', function() {
       const checkbox = (this);
       const questionNum = $(checkbox).closest('form').data('qnnumber');
       toggleMcqOtherOptionEnabled(checkbox, questionNum);
    });
}
/**
 * Hides the weight cells and weight label on top of the cells when the checkbox is unchecked,
 * otherwise, shows the weight cells and label.
 * If 'other' option is enabled, and 'choices are weighted' checkbox is checked, shows the 'other' weight cell,
 * otherwise if the checkbox is unchecked, hides the 'other' weight cell.
 * @param $checkbox
 * @param questionNum
 */
function toggleMcqAssignWeights($checkbox, questionNum) {
    // The weight label
    const $weightLabel = $checkbox.parent().siblings('div');
    const $weightCells = $(`#mcqWeights-${questionNum}`);
    const $otherWeightCell = $(`#mcqOtherWeight-${questionNum}`).parent();
    const $otherEnabledCheckbox = $(`#mcqOtherOptionFlag-${questionNum}`);
    if ($checkbox.prop('checked')) {
        $weightLabel.show();
        $weightCells.show();

        if ($otherEnabledCheckbox.prop('checked')) {
            $otherWeightCell.show();
        }
    } else {
        $weightLabel.hide();
        $weightCells.hide();
        // If weight is not assigned, no need to check if the other option is checked,
        // As, other weight will be disabled anyway.
        $otherWeightCell.hide();
    }
}

function bindMcqAssignWeightsCheckbox() {
    $('body').on('click', 'input[id^="mcqAssignWeights"]', function() {
        const $checkbox = $(this);
        const questionNum = $checkbox.closest('form').data('qnnumber');
        toggleMcqAssignWeights($checkbox, questionNum);
    });
}

export {
    addMcqOption,
    bindMcqAssignWeightsCheckbox,
    bindMcqOtherOptionEnabled,
    changeMcqGenerateFor,
    removeMcqOption,
    toggleMcqAssignWeights,
    toggleMcqGeneratedOptions,
    toggleMcqOtherOptionEnabled,
};
