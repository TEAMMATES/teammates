import {
    ParamsNames,
} from './const';

function addMcqOption(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const curNumberOfChoiceCreated =
            parseInt($(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(), 10);

    $(`
    <div class="margin-bottom-7px" id="mcqOptionRow-${curNumberOfChoiceCreated}-${questionNum}">
        <div class="input-group">
            <span class="input-group-addon">
                <span class="glyphicon glyphicon-resize-vertical"></span>
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
    `).appendTo($(`#mcqOptionRows-${questionNum}`));

    $(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeMcqOption(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const $mcqOptionRows = $(`#mcqOptionRows-${questionNum}`);
    const $thisRow = $(`#mcqOptionRow-${index}-${questionNum}`);

    // count number of child rows the table has
    const numberOfOptions = $mcqOptionRows.children('div').length;

    if (numberOfOptions <= 1) {
        $thisRow.find('input').val('');
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
        $(`#mcqChoiceTable-${questionNum}`).find('input[type=text]').prop('disabled', true);
        $(`#mcqChoiceTable-${questionNum}`).hide();
        $(`#mcqGenerateForSelect-${questionNum}`).prop('disabled', false);
        $(`#mcqOtherOptionFlag-${questionNum}`).closest('.checkbox').hide();
        changeMcqGenerateFor(questionNum);
    } else {
        $(`#mcqChoiceTable-${questionNum}`).find('input[type=text]').prop('disabled', false);
        $(`#mcqChoiceTable-${questionNum}`).show();
        $(`#mcqGenerateForSelect-${questionNum}`).prop('disabled', true);
        $(`#mcqOtherOptionFlag-${questionNum}`).closest('.checkbox').show();
        $(`#mcqGeneratedOptions-${questionNum}`).val('NONE');
    }
}

function toggleMcqOtherOptionEnabled(checkbox, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function makeMcqOptionsReorderable(questionNum) {
    $(`div[id^='mcqOptionRows-${questionNum}']`).sortable({
        update() {
            $(`div[id^='mcqOptionRow-'][id$=${questionNum}]`).each(function (index) {
                $(this).attr('id', `mcqOptionRow-${index}-${questionNum}`);
                $(this).find('input[id^="mcqOption-"]').attr({
                    name: `mcqOption-${index}`,
                    id: `mcqOption-${index}-${questionNum}`,
                });
                $(this).find('button[id="mcqRemoveOptionLink"]').attr('onclick', `removeMcqOption(${index},${questionNum})`);
            });
        },
    });
}

export {
    addMcqOption,
    changeMcqGenerateFor,
    makeMcqOptionsReorderable,
    removeMcqOption,
    toggleMcqGeneratedOptions,
    toggleMcqOtherOptionEnabled,
};
