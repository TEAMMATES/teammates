import {
    ParamsNames,
} from './const';

function updateConstSumPointsValue(questionNum) {
    if ($(`#${ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}-${questionNum}`).val() < 1) {
        $(`#${ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}-${questionNum}`).val(100);
    }
    if ($(`#${ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION}-${questionNum}`).val() < 1) {
        $(`#${ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION}-${questionNum}`).val(100);
    }
    if ($(`#${ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT}-${questionNum}`).val() < 1) {
        $(`#${ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT}-${questionNum}`).val(100);
    }
}

function addConstSumOption(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const curNumberOfChoiceCreated =
            parseInt($(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(), 10);

    $(`
    <div class="margin-bottom-7px" id="constSumOptionRow-${curNumberOfChoiceCreated}-${questionNum}">
        <div class="input-group width-100-pc">
            <span class="input-group-addon">
                <span class="glyphicon glyphicon-resize-vertical"></span>
            </span>
            <input type="text" name="${ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION}-${curNumberOfChoiceCreated}"
                    id="${ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION}-${curNumberOfChoiceCreated}-${questionNum}"
                    class="form-control constSumOptionTextBox">
            <span class="input-group-btn">
                <button class="btn btn-default removeOptionLink" id="constSumRemoveOptionLink"
                        onclick="removeConstSumOption(${curNumberOfChoiceCreated}, ${questionNum})"
                        tabindex="-1">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
            </span>
        </div>
    </div>
    `).appendTo($(`#constSumOptionRows-${questionNum}`));

    $(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function showConstSumOptionTable(questionNum) {
    $(`#constSumOptionTable-${questionNum}`).show();
}

function hideConstSumOptionTable(questionNum) {
    $(`#constSumOptionTable-${questionNum}`).hide();
}

function removeConstSumOption(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;
    const $thisRow = $(`#constSumOptionRow-${index}-${questionNum}`);

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
/**
 * If the radio button is checked, the corresponding number field will be enabled,
 * otherwise it will be disabled.
 */
function toggleConstSumOptionsRadioButton(questionNum) {
    const isConstSumPointsTotalChecked = $(`#constSumPointsTotal-${questionNum}`).prop('checked');
    $(`#constSumPoints-${questionNum}`).prop('disabled', !isConstSumPointsTotalChecked);

    const isConstSumPointsPerOptionChecked = $(`#constSumPointsPerOption-${questionNum}`).prop('checked');
    $(`#constSumPointsForEachOption-${questionNum}`).prop('disabled', !isConstSumPointsPerOptionChecked);

    const isConstSumPointsPerRecipientChecked = $(`#constSumPointsPerRecipient-${questionNum}`).prop('checked');
    $(`#constSumPointsForEachRecipient-${questionNum}`).prop('disabled', !isConstSumPointsPerRecipientChecked);
}

function bindConstSumOptionsRadioButtons() {
    $(document).on('change', 'input[name="constSumPointsPerOption"]', (e) => {
        const questionNumber = $(e.currentTarget).closest('form').attr('data-qnnumber');
        toggleConstSumOptionsRadioButton(questionNumber);
    });
}

function toggleConstSumDistributePointsOptions(checkbox, questionNum) {
    const constSumDistributePointsSelect = `#constSumDistributePointsSelect-${questionNum}`;
    const constSumDistributePointsOptions = `#constSumDistributePointsOptions-${questionNum}`;

    if (checkbox.checked) {
        $(constSumDistributePointsSelect).prop('disabled', false);
        $(constSumDistributePointsOptions).attr('value',
                $(constSumDistributePointsSelect).prop('value'));
    } else {
        $(constSumDistributePointsSelect).prop('disabled', true);
        $(constSumDistributePointsOptions).attr('value', 'None');
    }
}

function changeConstSumDistributePointsFor(questionNum) {
    $(`#constSumDistributePointsOptions-${questionNum}`).attr('value',
            $(`#constSumDistributePointsSelect-${questionNum}`).prop('value'));
}

/**
 * Enables options for distribute points question to be reordered through a drag and drop mechanism.
 * Binds an update event to the option elements which is triggered whenever the order of
 * elements change. The event handler updates the ids of elements to match the new order.
 */
function makeConstSumOptionsReorderable(questionNum) {
    $(`#constSumOptionRows-${questionNum}`).sortable({
        cursor: 'move',
        update() {
            $(this).children().each(function (index) {
                $(this).attr('id', `constSumOptionRow-${index}-${questionNum}`);
                $(this).find('input[id^="constSumOption-"]').attr({
                    name: `constSumOption-${index}`,
                    id: `constSumOption-${index}-${questionNum}`,
                });
                $(this).find('button[id="constSumRemoveOptionLink"]')
                        .attr('onclick', `removeConstSumOption(${index},${questionNum})`);
            });
        },
    });
}

export {
    addConstSumOption,
    bindConstSumOptionsRadioButtons,
    hideConstSumOptionTable,
    makeConstSumOptionsReorderable,
    removeConstSumOption,
    showConstSumOptionTable,
    toggleConstSumOptionsRadioButton,
    updateConstSumPointsValue,
    toggleConstSumDistributePointsOptions,
    changeConstSumDistributePointsFor,
};
