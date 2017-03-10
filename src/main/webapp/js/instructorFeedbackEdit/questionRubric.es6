/* eslint-disable no-undef */

function addRubricRow(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const numberOfRows = parseInt($(`#rubricNumRows-${questionNum}`).val());
    const numberOfCols = parseInt($(`#rubricNumCols-${questionNum}`).val());

    const newRowNumber = numberOfRows + 1;

    const rubricRowTemplate =
        '<tr id="rubricRow-${qnIndex}-${row}">'
          + '<td>'
              + '<div class="col-sm-12 input-group">'
                  + '<span class="input-group-addon btn btn-default rubricRemoveSubQuestionLink-${qnIndex}" '
                          + 'id="rubricRemoveSubQuestionLink-${qnIndex}-${row}" '
                          + 'onclick="removeRubricRow(${row},${qnIndex})" '
                          + 'onmouseover="highlightRubricRow(${row}, ${qnIndex}, true)" '
                          + 'onmouseout="highlightRubricRow(${row}, ${qnIndex}, false)">'
                      + '<span class="glyphicon glyphicon-remove"></span>'
                  + '</span>'
                  + '<textarea class="form-control" rows="3" '
                          + 'id="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}-${qnIndex}-${row}" '
                          + 'name="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}-${row}">'
                      + '${subQuestion}'
                  + '</textarea>'
              + '</div>'
          + '</td>'
          + '${rubricRowBodyFragments}'
      + '</tr>';

    const rubricRowFragmentTemplate =
        '<td class="align-center rubricCol-${qnIndex}-${col}">'
        + '<textarea class="form-control" rows="3" '
                + 'id="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}-${qnIndex}-${row}-${col}" '
                + 'name="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}-${row}-${col}">'
            + '${description}'
        + '</textarea>'
      + '</td>';

    let rubricRowBodyFragments = '';
    // Create numberOfCols of <td>'s
    for (let cols = 0; cols < numberOfCols; cols++) {
        if (!$(`.rubricCol-${questionNum}-${cols}`).length) {
            continue;
        }
        let fragment = rubricRowFragmentTemplate;
        fragment = replaceAll(fragment, '${qnIndex}', questionNum);
        fragment = replaceAll(fragment, '${row}', newRowNumber - 1);
        fragment = replaceAll(fragment, '${col}', cols);
        fragment = replaceAll(fragment, '${description}', '');
        fragment = replaceAll(fragment, '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}', 'rubricDesc');
        rubricRowBodyFragments += fragment;
    }

    // Create new rubric row
    let newRubricRow = rubricRowTemplate;
    newRubricRow = replaceAll(newRubricRow, '${qnIndex}', questionNum);
    newRubricRow = replaceAll(newRubricRow, '${row}', newRowNumber - 1);
    newRubricRow = replaceAll(newRubricRow, '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}', 'rubricSubQn');
    newRubricRow = replaceAll(newRubricRow, '${subQuestion}', '');
    newRubricRow = replaceAll(newRubricRow, '${rubricRowBodyFragments}', rubricRowBodyFragments);

    // Row to insert new row after
    const lastRow = $(`#rubricEditTable-${questionNum} tr:last`);
    $(newRubricRow).insertAfter(lastRow);

    // Increment
    $(`#rubricNumRows-${questionNum}`).val(newRowNumber);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function addRubricCol(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const numberOfRows = parseInt($(`#rubricNumRows-${questionNum}`).val());
    const numberOfCols = parseInt($(`#rubricNumCols-${questionNum}`).val());

    const newColNumber = numberOfCols + 1;

    // Insert header <th>
    const rubricHeaderFragmentTemplate =
       '<th class="rubricCol-${qnIndex}-${col}">'
          + '<div class="input-group">'
              + '<input type="text" class="col-sm-12 form-control" value="${rubricChoiceValue}" '
                      + 'id="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}-${qnIndex}-${col}" '
                      + 'name="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}-${col}">'
              + '<span class="input-group-addon btn btn-default rubricRemoveChoiceLink-${qnIndex}" '
                      + 'id="rubricRemoveChoiceLink-${qnIndex}-${col}" onclick="removeRubricCol(${col}, ${qnIndex})" '
                      + 'onmouseover="highlightRubricCol(${col}, ${qnIndex}, true)" '
                      + 'onmouseout="highlightRubricCol(${col}, ${qnIndex}, false)">'
                  + '<span class="glyphicon glyphicon-remove"></span>'
              + '</span>'
          + '</div>'
      + '</th>';

    let rubricHeaderFragment = rubricHeaderFragmentTemplate;
    rubricHeaderFragment = replaceAll(rubricHeaderFragment, '${qnIndex}', questionNum);
    rubricHeaderFragment = replaceAll(rubricHeaderFragment, '${col}', newColNumber - 1);
    rubricHeaderFragment = replaceAll(rubricHeaderFragment, '${rubricChoiceValue}', '');
    rubricHeaderFragment = replaceAll(rubricHeaderFragment,
                                      '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}',
                                      'rubricChoice');

    // Insert after last <th>
    const lastTh = $(`#rubricEditTable-${questionNum}`).find('tr:first').children().last();
    $(rubricHeaderFragment).insertAfter(lastTh);

    // Insert weight <th>
    const rubricWeightFragmentTemplate =
        '<th class="rubricCol-${qnIndex}-${col}">'
           + '<input type="number" class="form-control nonDestructive" value="${rubricWeight}" '
                   + 'id="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT}-${qnIndex}-${col}" '
                   + 'name="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT}-${col}" step="0.01">'
      + '</th>';

    let rubricWeightFragment = rubricWeightFragmentTemplate;
    rubricWeightFragment = replaceAll(rubricWeightFragment, '${qnIndex}', questionNum);
    rubricWeightFragment = replaceAll(rubricWeightFragment, '${col}', newColNumber - 1);
    rubricWeightFragment = replaceAll(rubricWeightFragment, '${rubricWeight}', 0);
    rubricWeightFragment = replaceAll(rubricWeightFragment,
                                      '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT}',
                                      'rubricWeight');

    // Insert after last <th>
    const lastWeightCell = $(`#rubricWeights-${questionNum} th:last`);
    $(rubricWeightFragment).insertAfter(lastWeightCell);

    disallowNonNumericEntries($(`#rubricWeight-${questionNum}-${newColNumber - 1}`), true, true);

    // Insert body <td>'s
    const rubricRowFragmentTemplate =
        '<td class="align-center rubricCol-${qnIndex}-${col}">'
        + '<textarea class="form-control" rows="3" '
                + 'id="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}-${qnIndex}-${row}-${col}" '
                + 'name="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}-${row}-${col}">'
            + '${description}'
        + '</textarea>'
      + '</td>';

    // Create numberOfRows of <td>'s
    for (let rows = 0; rows < numberOfRows; rows++) {
        if (!$(`#rubricRow-${questionNum}-${rows}`).length) {
            continue;
        }
        let fragment = rubricRowFragmentTemplate;
        fragment = replaceAll(fragment, '${qnIndex}', questionNum);
        fragment = replaceAll(fragment, '${row}', rows);
        fragment = replaceAll(fragment, '${col}', newColNumber - 1);
        fragment = replaceAll(fragment, '${description}', '');
        fragment = replaceAll(fragment,
                              '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}',
                              'rubricDesc');

        // Insert after previous <td>
        const lastTd = $(`#rubricRow-${questionNum}-${rows} td:last`);
        $(fragment).insertAfter(lastTd);
    }

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
    const numberOfRows = $thisRow.parent().children('tr').length;

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
 * @param questionNum
 *            the question number of the feedback question
 * @returns {Boolean} true if the weights are assigned by the user, otherwise false
 */
function hasAssignedWeights(questionNum) {
    return $(`#rubricAssignWeights-${questionNum}`).prop('checked');
}
