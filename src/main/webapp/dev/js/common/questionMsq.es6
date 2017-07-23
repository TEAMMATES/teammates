import {
    ParamsNames,
} from './const.es6';

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
    return $(`msqChoiceTable-${questionNum}`).children().length - 1;
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
    let val;
    const category = $(`#msqGenerateForSelect-${questionNum}`).prop('value');

    if (category === 'STUDENTS') {
        val = $('#num-students').val();
    } else if (category === 'TEAMS') {
        val = $('#num-teams').val();
    } else {
        val = $('#num-instructors').val();
    }

    return val;
}

function adjustMaxSelectableChoices(questionNum) {
    if (!isMaxSelectableChoicesEnabled(questionNum)) {
        return;
    }

    let upperLimit;
    const currentVal = getMaxSelectableChoicesValue(questionNum);

    if (isGenerateOptionsEnabled(questionNum)) {
        upperLimit = getTotalOptionsForSelectedGenerateOptionsType(questionNum);
    } else {
        upperLimit = getNumOfMsqOptions(questionNum);
    }

    setUpperLimitForMaxSelectableChoices(questionNum, upperLimit);
    setMaxSelectableChoices(questionNum, Math.min(currentVal, upperLimit));
}

function adjustMinSelectableChoices(questionNum) {
    if (!isMinSelectableChoicesEnabled(questionNum)) {
        return;
    }

    let upperLimit = getMaxSelectableChoicesValue(questionNum);
    const currentVal = getMinSelectableChoicesValue(questionNum);

    if (isGenerateOptionsEnabled(questionNum)) {
        upperLimit = Math.min(upperLimit, getTotalOptionsForSelectedGenerateOptionsType(questionNum));
    } else {
        upperLimit = Math.min(upperLimit, getNumOfMsqOptions(questionNum));
    }

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

function toggleMsqGeneratedOptions(checkbox, questionNum) {
    if (checkbox.checked) {
        $(`#msqChoiceTable-${questionNum}`).find('input[type=text]').prop('disabled', true);
        $(`#msqChoiceTable-${questionNum}`).hide();
        $(`#msqGenerateForSelect-${questionNum}`).prop('disabled', false);
        $(`#msqOtherOptionFlag-${questionNum}`).closest('.checkbox').hide();
        $(`#generatedOptions-${questionNum}`).attr('value',
                                                   $(`#msqGenerateForSelect-${questionNum}`).prop('value'));
    } else {
        $(`#msqChoiceTable-${questionNum}`).find('input[type=text]').prop('disabled', false);
        $(`#msqChoiceTable-${questionNum}`).show();
        $(`#msqGenerateForSelect-${questionNum}`).prop('disabled', true);
        $(`#msqOtherOptionFlag-${questionNum}`).closest('.checkbox').show();
        $(`#generatedOptions-${questionNum}`).attr('value', 'NONE');
    }

    adjustMinMaxSelectableChoices(questionNum);
}

function toggleMsqOtherOptionEnabled(checkbox, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function changeMsqGenerateFor(questionNum) {
    $(`#generatedOptions-${questionNum}`).attr('value',
                                               $(`#msqGenerateForSelect-${questionNum}`).prop('value'));
    adjustMinMaxSelectableChoices(questionNum);
}

function bindMsqEvents() {
    $(document).on('change', 'input[name="msqMaxSelectableChoices"]', (e) => {
        const questionNum = $(e.target).closest('form').attr('data-qnnumber');
        adjustMinMaxSelectableChoices(questionNum);
    });

    $(document).on('change', 'input[name="msqMinSelectableChoices"]', (e) => {
        const questionNum = $(e.target).closest('form').attr('data-qnnumber');
        adjustMinMaxSelectableChoices(questionNum);
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
