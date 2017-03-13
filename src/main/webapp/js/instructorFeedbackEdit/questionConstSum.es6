/* global
FEEDBACK_QUESTION_CONSTSUMPOINTS:false, FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION:false
FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT:false, FEEDBACK_QUESTION_CONSTSUMOPTION:false
FEEDBACK_QUESTION_NUMBEROFCHOICECREATED:false, FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE:false
*/

function updateConstSumPointsValue(questionNum) {
    if ($(`#${FEEDBACK_QUESTION_CONSTSUMPOINTS}-${questionNum}`).val() < 1) {
        $(`#${FEEDBACK_QUESTION_CONSTSUMPOINTS}-${questionNum}`).val(1);
    }
    if ($(`#${FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION}-${questionNum}`).val() < 1) {
        $(`#${FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION}-${questionNum}`).val(1);
    }
    if ($(`#${FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT}-${questionNum}`).val() < 1) {
        $(`#${FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT}-${questionNum}`).val(1);
    }
}

function addConstSumOption(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const curNumberOfChoiceCreated = parseInt($(`#${FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(), 10);

    $(`
    <div class="margin-bottom-7px" id="constSumOptionRow-${curNumberOfChoiceCreated}-${questionNum}">
        <div class="input-group width-100-pc">
            <input type="text" name="${FEEDBACK_QUESTION_CONSTSUMOPTION}-${curNumberOfChoiceCreated}"
                    id="${FEEDBACK_QUESTION_CONSTSUMOPTION}-${curNumberOfChoiceCreated}-${questionNum}"
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
    `).insertBefore($(`#constSumAddOptionRow-${questionNum}`));

    $(`#${FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function hideConstSumOptionTable(questionNum) {
    $(`#${FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE}-${questionNum}`).hide();
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

/* exported
updateConstSumPointsValue, addConstSumOption, hideConstSumOptionTable, removeConstSumOption
*/
