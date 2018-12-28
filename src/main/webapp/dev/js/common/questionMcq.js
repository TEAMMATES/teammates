import {
    ParamsNames,
} from './const';

function addMcqOption(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const curNumberOfChoiceCreated =
            parseInt($(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(), 10);
    const $choiceColumn = $(`#mcqChoices-${questionNum}`);
    const $weightColumn = $(`#mcqWeights-${questionNum}`);

    const choiceFragment = (`
    <div class="margin-bottom-7px" id="mcqOptionRow-${curNumberOfChoiceCreated}-${questionNum}">
        <div class="input-group width-100-pc">
            <span class="input-group-addon">
                <span class="glyphicon glyphicon-resize-vertical"></span>
                <input type="radio" class="disabled_radio" disabled>
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

    const weightFragment = (`
    <div class="margin-bottom-7px">
        <input type="number" class="form-control nonDestructive" value="0"
                id="${ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT}-${curNumberOfChoiceCreated}-${questionNum}"
                name="${ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT}-${curNumberOfChoiceCreated}" step="0.01" min="0" required>
    </div>
    `);

    if (curNumberOfChoiceCreated === 0) {
        $choiceColumn.html(choiceFragment);
        $weightColumn.html(weightFragment);
    } else {
        $choiceColumn.append(choiceFragment);
        $weightColumn.append(weightFragment);
    }
    $choiceColumn.sortable('refresh');

    $(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeMcqOption(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;
    const numberOfOptions = $(`#mcqChoices-${questionNum}`).children('div').length;
    const $thisChoice = $(`#mcqOptionRow-${index}-${questionNum}`);
    const $thisWeight = $(`#mcqWeight-${index}-${questionNum}`);

    if (numberOfOptions <= 1) {
        $thisChoice.find('input').val('');
        $thisWeight.val(0);
    } else {
        $thisChoice.remove();
        // Remove the containing div of weight input field.
        $thisWeight.parent().remove();

        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

/**
 * Sets the required attribute for Mcq weight cells, and if 'other' option is checked,
 * then sets the required attribute for other weight cell too.
 */
function setRequiredAttributeForMcqWeightCells($weightColumn, questionNum, isRequired) {
    const $weightCells = $weightColumn.find('input[id^="mcqWeight"]');
    const $otherWeightCell = $(`#mcqOtherWeight-${questionNum}`);
    const isOtherOptionEnabled = $(`#mcqOtherOptionFlag-${questionNum}`).prop('checked');

    $weightCells.each(function () {
        $(this).prop('required', isRequired);
    });

    // If 'other' option is checked, make other weight required.
    if (isOtherOptionEnabled) {
        $otherWeightCell.prop('required', isRequired);
    }
}

function toggleVisibilityOfMcqOtherWeight($checkbox, questionNum) {
    // The 'Choices are weighted' checkbox
    const $mcqAssignWeightCheckbox = $(`#mcqHasAssignedWeights-${questionNum}`);
    const $mcqOtherWeightCell = $(`#mcqOtherWeight-${questionNum}`);

    if ($checkbox.prop('checked') && $mcqAssignWeightCheckbox.prop('checked')) {
        $mcqOtherWeightCell.show();
        // Set other weight as required.
        $mcqOtherWeightCell.prop('required', true);
    } else {
        $mcqOtherWeightCell.prop('required', false);
        $mcqOtherWeightCell.hide();
    }
}

/**
 * Hides the weight cells and weight label on top of the cells when the checkbox is unchecked,
 * otherwise, shows the weight cells and label.
 * If 'other' option is enabled, and 'choices are weighted' checkbox is checked, shows the 'other' weight cell,
 * otherwise if the checkbox is unchecked, hides the 'other' weight cell.
 * @param $checkbox
 * @param questionNum
 */
function toggleMcqHasAssignedWeights($checkbox, questionNum) {
    // The weight label
    const $weightLabel = $checkbox.parent().siblings('div');
    const $weightColumn = $(`#mcqWeights-${questionNum}`);
    const $otherEnabledCheckbox = $(`#mcqOtherOptionFlag-${questionNum}`);

    if ($checkbox.prop('checked')) {
        $weightLabel.show();
        $weightColumn.show();

        // Set the weight cells as required.
        setRequiredAttributeForMcqWeightCells($weightColumn, questionNum, true);
    } else {
        $weightLabel.hide();
        // Set the weight cells as not required.
        setRequiredAttributeForMcqWeightCells($weightColumn, questionNum, false);
        $weightColumn.hide();
    }

    // Set the visibility of the 'Other' weight cell
    toggleVisibilityOfMcqOtherWeight($otherEnabledCheckbox, questionNum);
}

function changeMcqGenerateFor(questionNum) {
    $(`#mcqGeneratedOptions-${questionNum}`).val($(`#mcqGenerateForSelect-${questionNum}`).prop('value'));
}

function toggleMcqGeneratedOptions(checkbox, questionNum) {
    if ($(checkbox).prop('checked')) {
        $(`#mcqChoices-${questionNum}`).find('input[type=text]').prop('disabled', true);
        $(`#mcqWeights-${questionNum}`).find('input[type=number]').prop('disabled', true);
        $(`#mcqOtherWeight-${questionNum}`).prop('disabled', true);
        $(`#mcqChoiceTable-${questionNum}`).hide();
        $(`#mcqHasAssignedWeights-${questionNum}`).parent().hide();
        // Hide the 'Weights' label
        $(`#mcqHasAssignedWeights-${questionNum}`).parent().siblings('div').hide();
        $(`#mcqGenerateForSelect-${questionNum}`).prop('disabled', false);
        $(`#mcqOtherOptionFlag-${questionNum}`).closest('.checkbox').hide();
        $(`#mcqOtherWeight-${questionNum}`).hide();
        changeMcqGenerateFor(questionNum);
    } else {
        $(`#mcqChoices-${questionNum}`).find('input[type=text]').prop('disabled', false);
        $(`#mcqWeights-${questionNum}`).find('input[type=number]').prop('disabled', false);
        $(`#mcqOtherWeight-${questionNum}`).prop('disabled', false);
        $(`#mcqChoiceTable-${questionNum}`).show();
        $(`#mcqHasAssignedWeights-${questionNum}`).parent().show();
        $(`#mcqOtherOptionFlag-${questionNum}`).closest('.checkbox').show();
        toggleMcqHasAssignedWeights($(`#mcqHasAssignedWeights-${questionNum}`), questionNum);
        $(`#mcqGenerateForSelect-${questionNum}`).prop('disabled', true);
        $(`#mcqGeneratedOptions-${questionNum}`).val('NONE');
    }
}

/**
 * If the 'other' option and Assign weight both are checked, shows the 'other' option,
 * otherwise hides it.
 * @param checkbox (Note that checkbox is a DOM element, not a jquery element)
 * @param questionNum
 */
function toggleMcqOtherOptionEnabled(checkbox, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    // Set visibility of mcq other weight cell.
    toggleVisibilityOfMcqOtherWeight($(checkbox), questionNum);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function bindMcqOtherOptionEnabled() {
    $('body').on('click', 'input[id^="mcqOtherOptionFlag"]', function () {
        const checkbox = (this);
        const questionNum = $(checkbox).closest('form').data('qnnumber');
        toggleMcqOtherOptionEnabled(checkbox, questionNum);
    });
}

function bindMcqHasAssignedWeightsCheckbox() {
    $('body').on('click', 'input[id^="mcqHasAssignedWeights"]', function () {
        const $checkbox = $(this);
        const questionNum = $checkbox.closest('form').data('qnnumber');
        toggleMcqHasAssignedWeights($checkbox, questionNum);
    });
}

export {
    addMcqOption,
    bindMcqHasAssignedWeightsCheckbox,
    bindMcqOtherOptionEnabled,
    changeMcqGenerateFor,
    removeMcqOption,
    toggleMcqHasAssignedWeights,
    toggleMcqGeneratedOptions,
    toggleMcqOtherOptionEnabled,
};
