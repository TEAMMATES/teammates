/* global
disallowNonNumericEntries:false, BootboxWrapper:false, StatusType:false
*/

function addRubricRow(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const numberOfRows = parseInt($(`#rubricNumRows-${questionNum}`).val(), 10);
    const numberOfCols = parseInt($(`#rubricNumCols-${questionNum}`).val(), 10);

    const newRowNumber = numberOfRows + 1;

    let rubricRowBodyFragments = '';
    // Create numberOfCols of <td>'s
    for (let cols = 0; cols < numberOfCols; cols += 1) {
        if (!$(`.rubricCol-${questionNum}-${cols}`).length) {
            continue;
        }
        const rubricRowFragment =
            `<td class="align-center rubricCol-${questionNum}-${cols}">
                <textarea class="form-control" rows="3" id="rubricDesc-${questionNum}-${newRowNumber - 1}-${cols}"
                        name="rubricDesc-${newRowNumber - 1}-${cols}">
                </textarea>
            </td>`;
        rubricRowBodyFragments += rubricRowFragment;
    }

    // Create new rubric row
    const newRubricRow =
        `<tr id="rubricRow-${questionNum}-${newRowNumber - 1}">
            <td>
                <div class="col-sm-12 input-group">
                    <span class="input-group-addon btn btn-default rubricRemoveSubQuestionLink-${questionNum}"
                            id="rubricRemoveSubQuestionLink-${questionNum}-${newRowNumber - 1}"
                            onclick="removeRubricRow(${newRowNumber - 1}, ${questionNum})"
                            onmouseover="highlightRubricRow(${newRowNumber - 1}, ${questionNum}, true)"
                            onmouseout="highlightRubricRow(${newRowNumber - 1}, ${questionNum}, false)">
                        <span class="glyphicon glyphicon-remove"></span>
                    </span>
                    <textarea class="form-control" rows="3" id="rubricSubQn-${questionNum}-${newRowNumber - 1}"
                            name="rubricSubQn-${newRowNumber - 1}">
                    </textarea>
                </div>
            </td>
            ${rubricRowBodyFragments}
        </tr>`;

    // Row to insert new row after
    const lastRow = $(`#rubricEditTable-${questionNum} tbody tr:nth-last-child(2)`);
    $(newRubricRow).insertAfter(lastRow);

    // Increment
    $(`#rubricNumRows-${questionNum}`).val(newRowNumber);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function addRubricCol(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const numberOfRows = parseInt($(`#rubricNumRows-${questionNum}`).val(), 10);
    const numberOfCols = parseInt($(`#rubricNumCols-${questionNum}`).val(), 10);

    const newColNumber = numberOfCols + 1;

    // Insert header <th>
    const rubricHeaderFragment =
        `<th class="rubricCol-${questionNum}-${newColNumber - 1}">
            <input type="text" class="col-sm-12 form-control" value=""
                    id="rubricChoice-${questionNum}-${newColNumber - 1}"
                    name="rubricChoice-${newColNumber - 1}">
        </th>`;

    // Insert after last <th>
    const lastTh = $(`#rubricEditTable-${questionNum}`).find('tr:first').children().last();
    $(rubricHeaderFragment).insertAfter(lastTh);

    // Insert weight <th>
    const rubricWeightFragment =
        `<th class="rubricCol-${questionNum}-${newColNumber - 1}">
            <input type="number" class="form-control nonDestructive" value="0"
                    id="rubricWeight-${questionNum}-${newColNumber - 1}"
                    name="rubricWeight-${newColNumber - 1}" step="0.01">
        </th>`;

    // Insert after last <th>
    const lastWeightCell = $(`#rubricWeights-${questionNum} th:last`);
    $(rubricWeightFragment).insertAfter(lastWeightCell);

    disallowNonNumericEntries($(`#rubricWeight-${questionNum}-${newColNumber - 1}`), true, true);

    // Create numberOfRows of <td>'s
    for (let rows = 0; rows < numberOfRows; rows += 1) {
        if (!$(`#rubricRow-${questionNum}-${rows}`).length) {
            continue;
        }
        // Insert body <td>'s
        const rubricRowFragment =
            `<td class="align-center rubricCol-${questionNum}-${newColNumber - 1}">
                <textarea class="form-control" rows="3" id="rubricDesc-${questionNum}-${rows}-${newColNumber - 1}"
                        name="rubricDesc-${rows}-${newColNumber - 1}">
                </textarea>
            </td>`;

        // Insert after previous <td>
        const lastTd = $(`#rubricRow-${questionNum}-${rows} td:last`);
        $(rubricRowFragment).insertAfter(lastTd);
    }

    // Add options row at the end
    const optionsRow =
        `<td class="align-center rubricCol-${questionNum}-${newColNumber - 1}" data-col="${newColNumber - 1}">
            <div class="btn-group">
                <button type="button" class="btn btn-default" id="rubricMoveChoiceLink-${questionNum}-${newColNumber - 1}-l"
                    onclick="swapRubricCol(${newColNumber - 1}, ${questionNum}, true)" data-toggle="tooltip"
                    data-placement="top" title="Move column to left">
                    <span class="glyphicon glyphicon-arrow-left"></span>
                </button>
                <button type="button" class="btn btn-default" id="rubricRemoveChoiceLink-${questionNum}-${newColNumber - 1}"
                    onclick="removeRubricCol(${newColNumber - 1}, ${questionNum})"
                    onmouseover="highlightRubricCol(${newColNumber - 1}, ${questionNum}, true)"
                    onmouseout="highlightRubricCol(${newColNumber - 1}, ${questionNum}, false)">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
                <button type="button" class="btn btn-default" id="rubricMoveChoiceLink-${questionNum}-${newColNumber - 1}-r"
                    onclick="swapRubricCol(${newColNumber - 1}, ${questionNum}, false)" data-toggle="tooltip"
                    data-placement="top" title="Move column to right">
                    <span class="glyphicon glyphicon-arrow-right"></span>
                </button>
            </div>
        </td>`;

    const lastTd = $(`#rubricOptionsRow-${questionNum} td:last`);
    $(optionsRow).insertAfter(lastTd);

    // Initialize tooltips
    $(`#rubricMoveChoiceLink-${questionNum}-${newColNumber - 1}-l`).tooltip({ container: 'body' });
    $(`#rubricMoveChoiceLink-${questionNum}-${newColNumber - 1}-r`).tooltip({ container: 'body' });

    // Increment
    $(`#rubricNumCols-${questionNum}`).val(newColNumber);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeRubricRow(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const $thisRow = $(`#rubricRow-${questionNum}-${index}`);

    // count number of table rows from table body
    const numberOfRows = $thisRow.parent().children('tr').length - 1;  // exclude options row

    const delStr = numberOfRows <= 1 ? 'clear' : 'delete';
    const messageText = `Are you sure you want to ${delStr} the row?`;
    const okCallback = function () {
        if (numberOfRows <= 1) {
            $thisRow.find('textarea').val('');
        } else {
            $thisRow.remove();

            if ($(questionId).attr('editStatus') === 'hasResponses') {
                $(questionId).attr('editStatus', 'mustDeleteResponses');
            }
        }
    };
    BootboxWrapper.showModalConfirmation('Confirm Deletion', messageText, okCallback, null,
                                         BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT,
                                         StatusType.WARNING);
}

function removeRubricCol(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const $thisCol = $(`.rubricCol-${questionNum}-${index}`);

    // count number of table columns from table body
    const numberOfCols = $thisCol.first().parent().children().length - 1;

    const delStr = numberOfCols <= 1 ? 'clear' : 'delete';
    const messageText = `Are you sure you want to ${delStr} the column?`;
    const okCallback = function () {
        if (numberOfCols <= 1) {
            $thisCol.find('input[id^="rubricChoice"], textarea').val('');
            $thisCol.find('input[id^="rubricWeight"]').val(0);
        } else {
            $thisCol.remove();

            if ($(questionId).attr('editStatus') === 'hasResponses') {
                $(questionId).attr('editStatus', 'mustDeleteResponses');
            }
        }
    };
    BootboxWrapper.showModalConfirmation('Confirm Deletion', messageText, okCallback, null,
                                         BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT,
                                         StatusType.WARNING);
}

function swapRubricCol(colIndex, questionNum, isSwapLeft) {
    if ($(`#rubricEditTable-${questionNum}`).length === 0
        && $(`.rubricCol-${questionNum}-${colIndex}`).length === 0
        && typeof isSwapLeft !== 'boolean') {
        // question and column should exist, isSwapLeft must be boolean
        return;
    }

    const numberOfRows = parseInt($(`#rubricNumRows-${questionNum}`).val(), 10);
    let swapColIndex;
    const swapCellAccessorStr = `#rubricOptionsRow-${questionNum} .rubricCol-${questionNum}-${colIndex}`;
    const rubricCellSelector = `td[class*='rubricCol-${questionNum}']`;

    if (isSwapLeft) {
        if ($(swapCellAccessorStr).prev(rubricCellSelector).length !== 0) {
            swapColIndex = $(swapCellAccessorStr).prev(rubricCellSelector).attr('data-col');
        } else {
            // trying to swap left most column to left
            return;
        }
    } else if ($(swapCellAccessorStr).next(rubricCellSelector).length !== 0) {
        swapColIndex = $(swapCellAccessorStr).next(rubricCellSelector).attr('data-col');
    } else {
        // trying to swap right most column to right
        return;
    }

    // swap rubric choices
    let $currentCell = $(`#rubricChoice-${questionNum}-${colIndex}`);
    let $swapCell = $(`#rubricChoice-${questionNum}-${swapColIndex}`);
    let temp = $currentCell.val();

    $currentCell.val($swapCell.val());
    $swapCell.val(temp);

    // swap rubric weights
    $currentCell = $(`#rubricWeight-${questionNum}-${colIndex}`);
    $swapCell = $(`#rubricWeight-${questionNum}-${swapColIndex}`);
    temp = $currentCell.val();
    $currentCell.val($swapCell.val());
    $swapCell.val(temp);

    // swap options filled
    for (let row = 0; row < numberOfRows; row += 1) {
        $currentCell = $(`#rubricDesc-${questionNum}-${row}-${colIndex}`);
        $swapCell = $(`#rubricDesc-${questionNum}-${row}-${swapColIndex}`);
        temp = $currentCell.val();
        $currentCell.val($swapCell.val());
        $swapCell.val(temp);
    }
}

function highlightRubricRow(index, questionNum, highlight) {
    const $rubricRow = $(`#rubricRow-${questionNum}-${index}`);

    if (highlight) {
        $rubricRow.find('td').addClass('cell-selected-negative');
    } else {
        $rubricRow.find('td').removeClass('cell-selected-negative');
    }
}

function highlightRubricCol(index, questionNum, highlight) {
    const $rubricCol = $(`.rubricCol-${questionNum}-${index}`);

    if (highlight) {
        $rubricCol.addClass('cell-selected-negative');
    } else {
        $rubricCol.removeClass('cell-selected-negative');
    }
}

/**
 * Moves the "weights" checkbox to the weight row if it is checked, otherwise
 * moves it to the choice row
 *
 * @param checkbox the "weights" checkbox
 */
function moveAssignWeightsCheckbox(checkbox) {
    const $choicesRow = checkbox.closest('thead').find('tr').eq(0);
    const $weightsRow = checkbox.closest('thead').find('tr').eq(1);
    const $choicesRowFirstCell = $choicesRow.find('th').first();
    const $weightsRowFirstCell = $weightsRow.find('th').first();

    const $checkboxCellContent = checkbox.closest('th').children().detach();

    $choicesRowFirstCell.empty();
    $weightsRowFirstCell.empty();

    if (checkbox.prop('checked')) {
        $choicesRowFirstCell.append('Choices <span class="glyphicon glyphicon-arrow-right"></span>');
        $weightsRowFirstCell.append($checkboxCellContent);
        $weightsRowFirstCell.find('.glyphicon-arrow-right').show();
    } else {
        $choicesRowFirstCell.append($checkboxCellContent);
        $choicesRowFirstCell.find('.glyphicon-arrow-right').hide();
    }
}

/**
 * Attaches event handlers to "weights" checkboxes to toggle the visibility of
 * the input boxes for rubric weights and move the "weights" checkbox to the
 * appropriate location
 */
function bindAssignWeightsCheckboxes() {
    $('body').on('click', 'input[id^="rubricAssignWeights"]', function () {
        const $checkbox = $(this);

        $checkbox.closest('form').find('tr[id^="rubricWeights"]').toggle();

        moveAssignWeightsCheckbox($checkbox);
    });
}

/**
 * @param questionNum
 *            the question number of the feedback question
 * @returns {Boolean} true if the weights are assigned by the user, otherwise false
 */
function hasAssignedWeights(questionNum) {
    return $(`#rubricAssignWeights-${questionNum}`).prop('checked');
}

/* exported
addRubricRow, addRubricCol, removeRubricRow, removeRubricCol, highlightRubricRow, highlightRubricCol
bindAssignWeightsCheckboxes, hasAssignedWeights
*/
