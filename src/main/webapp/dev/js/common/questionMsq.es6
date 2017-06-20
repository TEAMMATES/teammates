import { ParamsNames } from './const.es6';

function setMaxValForMaxSelectableChoicesInput(questionNum) {
    const $maxSelectableChoicesCheckbox = $(`#msqEnableMaxSelectableChoices-${questionNum}`);
    const $generateOptionsCheckbox = $(`#generateOptionsCheckbox-${questionNum}`);

    if ($maxSelectableChoicesCheckbox.prop('checked')) {
        let maxValue = 2;
        const $msqMaxSelectableChoices = $(`#msqMaxSelectableChoices-${questionNum}`);

        if ($generateOptionsCheckbox.prop('checked')) {
            const selectedVal = $(`#msqGenerateForSelect-${questionNum}`).prop('value');

            if (selectedVal === 'STUDENTS') {
                maxValue = $('#num-students').val();
            } else if (selectedVal === 'TEAMS') {
                maxValue = $('#num-teams').val();
            } else {
                maxValue = $('#num-instructors').val();
            }
        } else {
            // don't count last div as it is the "add option" button
            maxValue = $(`#msqChoiceTable-${questionNum}`).children('div').length - 1;
        }

        const currentVal = $msqMaxSelectableChoices.val();
        console.log(`maxValue = ${maxValue}`);

        $msqMaxSelectableChoices.prop('max', maxValue);
        $msqMaxSelectableChoices.val(Math.min(maxValue, currentVal));
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

export {
    addMsqOption,
    changeMsqGenerateFor,
    removeMsqOption,
    toggleMsqGeneratedOptions,
    toggleMsqOtherOptionEnabled,
    toggleMsqMaxSelectableChoices,
};
