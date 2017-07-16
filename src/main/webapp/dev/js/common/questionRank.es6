import {
    ParamsNames,
} from './const.es6';

function addRankOption(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const curNumberOfChoiceCreated =
            parseInt($(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(), 10);

    $(`
    <div class="margin-bottom-7px" id="rankOptionRow-${curNumberOfChoiceCreated}-${questionNum}">
        <div class="input-group">
            <input type="text" name="${ParamsNames.FEEDBACK_QUESTION_RANKOPTION}-${curNumberOfChoiceCreated}"
                    id="${ParamsNames.FEEDBACK_QUESTION_RANKOPTION}-${curNumberOfChoiceCreated}-${questionNum}"
                    class="form-control rankOptionTextBox">
            <span class="input-group-btn">
                <button class="btn btn-default removeOptionLink" id="rankRemoveOptionLink"
                        onclick="removeRankOption(${curNumberOfChoiceCreated}, ${questionNum})" tabindex="-1">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
            </span>
        </div>
    </div>
    `).insertBefore($(`#rankAddOptionRow-${questionNum}`));

    $(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function hideRankOptionTable(questionNum) {
    $(`#${ParamsNames.FEEDBACK_QUESTION_RANKOPTIONTABLE}-${questionNum}`).hide();
}

function removeRankOption(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;
    const $thisRow = $(`#rankOptionRow-${index}-${questionNum}`);

    // count number of child rows the table have and - 1 because of 'add option' button
    const numberOfOptions = $thisRow.parent().children('div').length - 1;

    if (numberOfOptions <= 2) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();

        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

export {
    addRankOption,
    hideRankOptionTable,
    removeRankOption,
};
