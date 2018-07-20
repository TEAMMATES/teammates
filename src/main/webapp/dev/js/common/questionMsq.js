import {
    ParamsNames,
} from './const';

function isMaxSelectableChoicesEnabled(questionNum) {
    return $(`#msqEnableMaxSelectableChoices-${questionNum}`).prop('checked');
}

function isMinSelectableChoicesEnabled(questionNum) {
    return $(`#msqEnableMinSelectableChoices-${questionNum}`).prop('checked');
}

function isGenerateOptionsEnabled(questionNum) {
    return $(`#generateMsqOptionsCheckbox-${questionNum}`).prop('checked');
}

function getNumOfMsqOptions(questionNum) {
    return $(`#msqChoices-${questionNum}`).children('div').length;
}

function getMaxSelectableChoicesElement(questionNum) {
    return $(`#msqMaxSelectableChoices-${questionNum}`);
}

function getMaxSelectableChoicesValue(questionNum) {
    if (isMaxSelectableChoicesEnabled(questionNum)) {
        return parseInt(getMaxSelectableChoicesElement(questionNum).val(), 10);
    }

    // return infinity
    return Number.MAX_SAFE_INTEGER;
}

function setUpperLimitForMaxSelectableChoices(questionNum, upperLimit) {
    getMaxSelectableChoicesElement(questionNum).prop('max', upperLimit);
}

function setMaxSelectableChoices(questionNum, newVal) {
    if (newVal >= 2) {
        // No use if max selectable choices were 1
        getMaxSelectableChoicesElement(questionNum).val(newVal);
    }
}

function getMinSelectableChoicesElement(questionNum) {
    return $(`#msqMinSelectableChoices-${questionNum}`);
}

function getMinSelectableChoicesValue(questionNum) {
    if (isMinSelectableChoicesEnabled(questionNum)) {
        return parseInt(getMinSelectableChoicesElement(questionNum).val(), 10);
    }

    // return infinity
    return Number.MAX_SAFE_INTEGER;
}

function setMinSelectableChoices(questionNum, newVal) {
    if (newVal >= 1) {
        // No use if min selectable choices where 0
        getMinSelectableChoicesElement(questionNum).val(newVal);
    }
}

function setUpperLimitForMinSelectableChoices(questionNum, upperLimit) {
    getMinSelectableChoicesElement(questionNum).prop('max', upperLimit);
}

/**
 * Returns total number of options for the selected generate options type.
 * Eg. if 'instructors' is selected, returns number of instructors for feedback session.
 * Assumes that 'generateOptions' checkbox is checked.
 */
function getTotalOptionsForSelectedGenerateOptionsType(questionNum) {
    const category = $(`#msqGenerateForSelect-${questionNum}`).prop('value').toLowerCase();
    return $(`#num-${category}`).val();
}

function adjustMaxSelectableChoices(questionNum) {
    if (!isMaxSelectableChoicesEnabled(questionNum)) {
        return;
    }

    const upperLimit = isGenerateOptionsEnabled(questionNum)
            ? getTotalOptionsForSelectedGenerateOptionsType(questionNum) : getNumOfMsqOptions(questionNum);
    const currentVal = getMaxSelectableChoicesValue(questionNum);

    setUpperLimitForMaxSelectableChoices(questionNum, upperLimit);
    setMaxSelectableChoices(questionNum, Math.min(currentVal, upperLimit));
}

function adjustMinSelectableChoices(questionNum) {
    if (!isMinSelectableChoicesEnabled(questionNum)) {
        return;
    }

    const currentVal = getMinSelectableChoicesValue(questionNum);
    const upperLimit = Math.min(getMaxSelectableChoicesValue(questionNum), isGenerateOptionsEnabled(questionNum)
            ? getTotalOptionsForSelectedGenerateOptionsType(questionNum) : getNumOfMsqOptions(questionNum));

    setUpperLimitForMinSelectableChoices(questionNum, upperLimit);
    setMinSelectableChoices(questionNum, Math.min(currentVal, upperLimit));
}

function adjustMinMaxSelectableChoices(questionNum) {
    adjustMaxSelectableChoices(questionNum);
    adjustMinSelectableChoices(questionNum);
}

function addMsqOption(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const curNumberOfChoiceCreated =
            parseInt($(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(), 10);
    const $choiceColumn = $(`#msqChoices-${questionNum}`);
    const $weightColumn = $(`#msqWeights-${questionNum}`);

    const choiceFragment = (`
    <div class="margin-bottom-7px" id="msqOptionRow-${curNumberOfChoiceCreated}-${questionNum}">
        <div class="input-group">
            <span class="input-group-addon">
                <input type="checkbox" disabled>
            </span>
            <input type="text" name="${ParamsNames.FEEDBACK_QUESTION_MSQCHOICE}-${curNumberOfChoiceCreated}"
                    id="${ParamsNames.FEEDBACK_QUESTION_MSQCHOICE}-${curNumberOfChoiceCreated}-${questionNum}"
                    class="form-control msqOptionTextBox">
            <span class="input-group-btn">
                <button type="button" class="btn btn-default removeOptionLink" id="msqRemoveOptionLink"
                        onclick="removeMsqOption(${curNumberOfChoiceCreated}, ${questionNum})" tabindex="-1">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
            </span>
        </div>
    </div>
    `);

    const weightFragment = (`
    <div class="margin-bottom-7px">
        <input type="number" class="form-control nonDestructive" value="0"
                id="${ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT}-${curNumberOfChoiceCreated}-${questionNum}"
                name="${ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT}-${curNumberOfChoiceCreated}" step="0.01" min="0" required>
    </div>
    `);

    if (curNumberOfChoiceCreated === 0) {
        $choiceColumn.html(choiceFragment);
        $weightColumn.html(weightFragment);
    } else {
        $choiceColumn.append(choiceFragment);
        $weightColumn.append(weightFragment);
    }

    $(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }

    adjustMinMaxSelectableChoices(questionNum);
}

function removeMsqOption(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const $thisChoice = $(`#msqOptionRow-${index}-${questionNum}`);
    const $thisWeight = $(`#msqWeight-${index}-${questionNum}`);

    // count number of child of msqChoices column
    const numberOfOptions = $(`#msqChoices-${questionNum}`).children('div').length;

    if (numberOfOptions <= 1) {
        $thisChoice.find('input').val('');
        $thisWeight.val(0);
    } else {
        $thisChoice.remove();
        $thisWeight.parent().remove();

        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }

    adjustMinMaxSelectableChoices(questionNum);
}

/**
 * Sets the required attribute for Msq weight cells, and if 'other' option is checked,
 * then sets the required attribute for other weight cell too.
 */
function setRequiredAttributeForMsqWeightCells($weightColumn, questionNum, isRequired) {
    const $weightCells = $weightColumn.find('input[id^="msqWeight"]');
    const $otherWeightCell = $(`#msqOtherWeight-${questionNum}`);
    const isOtherOptionEnabled = $(`#msqOtherOptionFlag-${questionNum}`).prop('checked');

    $weightCells.each(function () {
        $(this).prop('required', isRequired);
    });

    // If 'other' option is checked, make other weight required.
    if (isOtherOptionEnabled) {
        $otherWeightCell.prop('required', isRequired);
    }
}

/**
 * Toggle visiblity of the msq other weight cell.
 * @param $checkbox is the 'Add Other Option' checkbox.
 */
function toggleVisibilityOfMsqOtherWeight($checkbox, questionNum) {
    // The 'Choices are weighted' checkbox
    const $msqAssignWeightCheckbox = $(`#msqHasAssignedWeights-${questionNum}`);
    const $msqOtherWeightCell = $(`#msqOtherWeight-${questionNum}`);

    if ($checkbox.prop('checked') && $msqAssignWeightCheckbox.prop('checked')) {
        $msqOtherWeightCell.show();
        // Set other weight as required.
        $msqOtherWeightCell.prop('required', true);
    } else {
        $msqOtherWeightCell.prop('required', false);
        $msqOtherWeightCell.hide();
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
function toggleMsqHasAssignedWeights($checkbox, questionNum) {
    // The weight label
    const $weightLabel = $checkbox.parent().siblings('div');
    const $weightColumn = $(`#msqWeights-${questionNum}`);
    const $otherEnabledCheckbox = $(`#msqOtherOptionFlag-${questionNum}`);

    if ($checkbox.prop('checked')) {
        $weightLabel.show();
        $weightColumn.show();
        // Set the weight cells as required.
        setRequiredAttributeForMsqWeightCells($weightColumn, questionNum, true);
    } else {
        $weightLabel.hide();
        // Set the weight cells as not required.
        setRequiredAttributeForMsqWeightCells($weightColumn, questionNum, false);
        $weightColumn.hide();
    }

    // Set the visibility of the 'Other' weight cell
    toggleVisibilityOfMsqOtherWeight($otherEnabledCheckbox, questionNum);
}

function toggleMsqMaxSelectableChoices(questionNum) {
    const $checkbox = $(`#msqEnableMaxSelectableChoices-${questionNum}`);

    $(`#msqMaxSelectableChoices-${questionNum}`).prop('disabled', !$checkbox.prop('checked'));
    adjustMinMaxSelectableChoices(questionNum);
}

function toggleMsqMinSelectableChoices(questionNum) {
    const $checkbox = $(`#msqEnableMinSelectableChoices-${questionNum}`);

    $(`#msqMinSelectableChoices-${questionNum}`).prop('disabled', !$checkbox.prop('checked'));
    adjustMinMaxSelectableChoices(questionNum);
}

function changeMsqGenerateFor(questionNum) {
    $(`#msqGeneratedOptions-${questionNum}`).val($(`#msqGenerateForSelect-${questionNum}`).prop('value'));
    adjustMinMaxSelectableChoices(questionNum);
}

function toggleMsqGeneratedOptions(checkbox, questionNum) {
    if ($(checkbox).prop('checked')) {
        $(`#msqChoices-${questionNum}`).find('input[type=text]').prop('disabled', true);
        $(`#msqWeights-${questionNum}`).find('input[type=number]').prop('disabled', true);
        $(`#msqOtherWeight-${questionNum}`).prop('disabled', true);
        $(`#msqChoiceTable-${questionNum}`).hide();
        $(`#msqHasAssignedWeights-${questionNum}`).parent().hide();
        // Hide the 'Weights' label
        $(`#msqHasAssignedWeights-${questionNum}`).parent().siblings('div').hide();
        $(`#msqGenerateForSelect-${questionNum}`).prop('disabled', false);
        $(`#msqOtherOptionFlag-${questionNum}`).closest('.checkbox').hide();
        $(`#msqOtherWeight-${questionNum}`).hide();
        changeMsqGenerateFor(questionNum);
    } else {
        $(`#msqChoices-${questionNum}`).find('input[type=text]').prop('disabled', false);
        $(`#msqWeights-${questionNum}`).find('input[type=number]').prop('disabled', false);
        $(`#msqOtherWeight-${questionNum}`).prop('disabled', false);
        $(`#msqChoiceTable-${questionNum}`).show();
        $(`#msqHasAssignedWeights-${questionNum}`).parent().show();
        $(`#msqOtherOptionFlag-${questionNum}`).closest('.checkbox').show();
        toggleMsqHasAssignedWeights($(`#msqHasAssignedWeights-${questionNum}`), questionNum);
        $(`#msqGenerateForSelect-${questionNum}`).prop('disabled', true);
        $(`#msqGeneratedOptions-${questionNum}`).val('NONE');
    }

    adjustMinMaxSelectableChoices(questionNum);
}

/**
 * If the 'other' option and Assign weight both are checked, shows the 'other' option,
 * otherwise hides it.
 * @param checkbox (Note that checkbox is a DOM element, not a jquery element)
 * @param questionNum
 */
function toggleMsqOtherOptionEnabled(checkbox, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    // Set visibility of msq other weight cell.
    toggleVisibilityOfMsqOtherWeight($(checkbox), questionNum);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function bindMsqEvents() {
    $(document).on('change', 'input[name="msqMaxSelectableChoices"]', (e) => {
        const questionNum = $(e.currentTarget).closest('form').attr('data-qnnumber');
        adjustMinMaxSelectableChoices(questionNum);
    });

    $(document).on('change', 'input[name="msqMinSelectableChoices"]', (e) => {
        const questionNum = $(e.currentTarget).closest('form').attr('data-qnnumber');
        adjustMinMaxSelectableChoices(questionNum);
    });

    $(document).on('change', 'input[name*="msqEnableMaxSelectableChoices"]', (e) => {
        const questionNumber = $(e.currentTarget).closest('form').attr('data-qnnumber');
        toggleMsqMaxSelectableChoices(questionNumber);
    });

    $(document).on('change', 'input[name*="msqEnableMinSelectableChoices"]', (e) => {
        const questionNumber = $(e.currentTarget).closest('form').attr('data-qnnumber');
        toggleMsqMinSelectableChoices(questionNumber);
    });

    // Bind events for msq other option
    $('body').on('click', 'input[id^="msqOtherOptionFlag"]', function () {
        const checkbox = (this);
        const questionNum = $(checkbox).closest('form').data('qnnumber');
        toggleMsqOtherOptionEnabled(checkbox, questionNum);
    });

    // Bind events for msq 'Choices are weighted' checkbox
    $('body').on('click', 'input[id^="msqHasAssignedWeights"]', function () {
        const $checkbox = $(this);
        const questionNum = $checkbox.closest('form').data('qnnumber');
        toggleMsqHasAssignedWeights($checkbox, questionNum);
    });
}

export {
    addMsqOption,
    bindMsqEvents,
    changeMsqGenerateFor,
    removeMsqOption,
    toggleMsqGeneratedOptions,
    toggleMsqHasAssignedWeights,
    toggleMsqOtherOptionEnabled,
    toggleMsqMaxSelectableChoices,
    toggleMsqMinSelectableChoices,
};
