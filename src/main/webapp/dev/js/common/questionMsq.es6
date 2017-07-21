import {
    ParamsNames,
} from './const.es6';

function getMaxSelectableChoices(questionNum) {
    const $maxSelectableChoicesCheckbox = $(`#msqEnableMaxSelectableChoices-${questionNum}`);

    if ($maxSelectableChoicesCheckbox.prop('checked')) {
        return parseInt($(`#msqMaxSelectableChoices-${questionNum}`).val(), 10);
    }

    // return infinity
    return Number.MAX_SAFE_INTEGER;
}

function setMaxSelectableChoices(questionNum, newVal) {
    if (newVal >= 2) {
        // No use if max selectable choices were 1
        $(`#msqMaxSelectableChoices-${questionNum}`).val(newVal);
        $(`#msqMinSelectableChoices-${questionNum}`).attr('max', Math.min(newVal - 1));
    }
}

function getMinSelectableChoices(questionNum) {
    const $minSelectableChoicesCheckbox = $(`#msqEnableMinSelectableChoices-${questionNum}`);

    if ($minSelectableChoicesCheckbox.prop('checked')) {
        return parseInt($(`#msqMinSelectableChoices-${questionNum}`).val(), 10);
    }

    // return infinity
    return Number.MAX_SAFE_INTEGER;
}

function setMinSelectableChoices(questionNum, newVal) {
    if (newVal >= 1) {
        // No use if min selectable choices where 0
        $(`#msqMinSelectableChoices-${questionNum}`).val(Math.min(newVal, getMaxSelectableChoices(questionNum)));
    }
}

function adjustMaxMinSelectableChoices(questionNum) {
    const maxSelectableChoices = getMaxSelectableChoices(questionNum);
    console.log('in adjustMaxMinSelectableChoices');
    $(`#msqMinSelectableChoices-${questionNum}`).attr('max', maxSelectableChoices);

    const minSelectableChoices = getMinSelectableChoices(questionNum);

    if (minSelectableChoices > maxSelectableChoices) {
        $(`#msqMinSelectableChoices-${questionNum}`).val(maxSelectableChoices);
    }
}

/**
 * Returns total number of options for the selected generate options from select box.
 * Eg. if 'instructors' is selected, returns number of instructors for feedback session.
 * Assumes that 'generateOptions' checkbox is checked.
 */
function getNumOfOptionsForSelectedGenerateOptions(questionNum) {
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

function setMaxValForMaxSelectableChoicesInput(questionNum) {
    const $maxSelectableChoicesCheckbox = $(`#msqEnableMaxSelectableChoices-${questionNum}`);
    const $generateMsqOptionsCheckbox = $(`#generateMsqOptionsCheckbox-${questionNum}`);

    if ($maxSelectableChoicesCheckbox.prop('checked')) {
        let maxValue;
        const $msqMaxSelectableChoices = $(`#msqMaxSelectableChoices-${questionNum}`);

        if ($generateMsqOptionsCheckbox.prop('checked')) {
            maxValue = getNumOfOptionsForSelectedGenerateOptions(questionNum);
        } else {
            // don't count last div as it is the "add option" button
            maxValue = $(`#msqChoiceTable-${questionNum}`).children('div').length - 1;
        }

        const currentVal = $msqMaxSelectableChoices.val();

        $msqMaxSelectableChoices.prop('max', maxValue);
        setMaxSelectableChoices(questionNum, Math.min(maxValue, currentVal));
    }
}

function setMaxValForMinSelectableChoicesInput(questionNum) {
    const $minSelectableChoicesCheckbox = $(`#msqEnableMinSelectableChoices-${questionNum}`);
    const $generateMsqOptionsCheckbox = $(`#generateMsqOptionsCheckbox-${questionNum}`);

    if ($minSelectableChoicesCheckbox.prop('checked')) {
        let maxValue;
        const $msqMinSelectableChoices = $(`#msqMinSelectableChoices-${questionNum}`);

        if ($generateMsqOptionsCheckbox.prop('checked')) {
            maxValue = getNumOfOptionsForSelectedGenerateOptions(questionNum);
        } else {
            // don't count last div as it is the "add option" button
            maxValue = $(`#msqChoiceTable-${questionNum}`).children('div').length - 1;
        }

        const currentVal = $msqMinSelectableChoices.val();
        maxValue = Math.min(maxValue - 1, getMaxSelectableChoices(questionNum));
        console.log(`maxValue = ${maxValue}, currentVal = ${currentVal}`);
        $msqMinSelectableChoices.prop('max', maxValue);
        setMinSelectableChoices(maxValue);
    }
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

    setMaxValForMaxSelectableChoicesInput(questionNum);
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

    setMaxValForMaxSelectableChoicesInput(questionNum);
}

function toggleMsqMaxSelectableChoices(questionNum) {
    const $checkbox = $(`#msqEnableMaxSelectableChoices-${questionNum}`);

    $(`#msqMaxSelectableChoices-${questionNum}`).prop('disabled', !$checkbox.prop('checked'));
    setMaxValForMaxSelectableChoicesInput(questionNum);
}

function toggleMsqMinSelectableChoices(questionNum) {
    const $checkbox = $(`#msqEnableMinSelectableChoices-${questionNum}`);

    $(`#msqMinSelectableChoices-${questionNum}`).prop('disabled', !$checkbox.prop('checked'));
    setMaxValForMinSelectableChoicesInput(questionNum);
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

    setMaxValForMaxSelectableChoicesInput(questionNum);
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
    setMaxValForMaxSelectableChoicesInput(questionNum);
}

function bindMsqEvents() {
    $(document).on('change', 'input[name="msqMaxSelectableChoices"]', (e) => {
        const questionNum = $(e.target).nearest('form').attr('data-qnnumber');
        console.log(`questionNum = ${questionNum}`);
        adjustMaxMinSelectableChoices(questionNum);
    });

    $(document).on('change', 'input[name="msqMinSelectableChoices"]', (e) => {
        const questionNum = $(e.target).nearest('form').attr('data-qnnumber');
        console.log(`questionNum = ${questionNum}`);
        adjustMaxMinSelectableChoices(questionNum);
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
