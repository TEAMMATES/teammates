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
    return $(`#msqChoiceTable-${questionNum}`).children().length - 1;
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

    $(`
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
    `).insertBefore($(`#msqAddOptionRow-${questionNum}`));

    $(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }

    adjustMinMaxSelectableChoices(questionNum);
}

function removeMsqOption(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const $thisRow = $(`#msqOptionRow-${index}-${questionNum}`);

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

    adjustMinMaxSelectableChoices(questionNum);
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
        $(`#msqChoiceTable-${questionNum}`).find('input[type=text]').prop('disabled', true);
        $(`#msqChoiceTable-${questionNum}`).hide();
        $(`#msqGenerateForSelect-${questionNum}`).prop('disabled', false);
        $(`#msqOtherOptionFlag-${questionNum}`).closest('.checkbox').hide();
        changeMsqGenerateFor(questionNum);
    } else {
        $(`#msqChoiceTable-${questionNum}`).find('input[type=text]').prop('disabled', false);
        $(`#msqChoiceTable-${questionNum}`).show();
        $(`#msqGenerateForSelect-${questionNum}`).prop('disabled', true);
        $(`#msqOtherOptionFlag-${questionNum}`).closest('.checkbox').show();
        $(`#msqGeneratedOptions-${questionNum}`).val('NONE');
    }

    adjustMinMaxSelectableChoices(questionNum);
}

function toggleMsqOtherOptionEnabled(checkbox, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

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
}

export {
    addMsqOption,
    bindMsqEvents,
    changeMsqGenerateFor,
    removeMsqOption,
    toggleMsqGeneratedOptions,
    toggleMsqOtherOptionEnabled,
    toggleMsqMaxSelectableChoices,
    toggleMsqMinSelectableChoices,
};
