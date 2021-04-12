import { moveItemInArray } from '@angular/cdk/drag-drop';
import { Component, OnInit } from '@angular/core';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { FeedbackRubricQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_RUBRIC_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SimpleModalType } from '../../simple-modal/simple-modal-type';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';

/**
 * Question details edit form component for rubric question.
 */
@Component({
  selector: 'tm-rubric-question-edit-details-form',
  templateUrl: './rubric-question-edit-details-form.component.html',
  styleUrls: ['./rubric-question-edit-details-form.component.scss'],
})
export class RubricQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackRubricQuestionDetails> implements OnInit {

  rowToHighlight: number = -1;
  columnToHighlight: number = -1;

  constructor(private simpleModalService: SimpleModalService) {
    super(DEFAULT_RUBRIC_QUESTION_DETAILS());
  }

  ngOnInit(): void {
  }

  /**
   * Triggers change of rubric choice.
   */
  triggerRubricChoiceChange(value: string, index: number): void {
    const newChoices: string[] = this.model.rubricChoices.slice();
    newChoices[index] = value;

    this.triggerModelChange('rubricChoices', newChoices);
  }

  /**
   * Triggers change of rubric sub question.
   */
  triggerRubricSubQuestionChange(value: string, index: number): void {
    const newSubQuestions: string[] = this.model.rubricSubQuestions.slice();
    newSubQuestions[index] = value;

    this.triggerModelChange('rubricSubQuestions', newSubQuestions);
  }

  /**
   * Triggers change of rubric description.
   */
  triggerRubricDescriptionChange(value: string, row: number, col: number): void {
    const newDescriptions: string[][] = this.model.rubricDescriptions.map((arr: string[]) => arr.slice());
    newDescriptions[row][col] = value;

    this.triggerModelChange('rubricDescriptions', newDescriptions);
  }

  /**
   * Triggers change of rubric weight.
   */
  triggerRubricWeightChange(value: number, row: number, col: number): void {
    const newWeightsForEachCell: number[][] = this.model.rubricWeightsForEachCell.map((arr: number[]) => arr.slice());
    newWeightsForEachCell[row][col] = value;

    this.triggerModelChange('rubricWeightsForEachCell', newWeightsForEachCell);
  }

  /**
   * Tracks by index.
   */
  trackByIndex(index: number): string {
    return index.toString();
  }

  /**
   * Adds a new sub question.
   */
  addNewSubQuestion(): void {
    const newSubQuestions: string[] = this.model.rubricSubQuestions.slice();
    newSubQuestions.push('');

    const newDescriptions: string[][] = this.model.rubricDescriptions.map((arr: string[]) => arr.slice());
    newDescriptions.push(Array(this.model.numOfRubricChoices).fill(''));

    // update weights
    let newWeightsForEachCell: number[][] = [];
    if (this.model.hasAssignedWeights) {
      newWeightsForEachCell = this.model.rubricWeightsForEachCell.map((arr: number[]) => arr.slice());
      newWeightsForEachCell.push(Array(this.model.numOfRubricChoices).fill(0));
    }

    this.triggerModelChangeBatch({
      rubricSubQuestions: newSubQuestions,
      rubricDescriptions: newDescriptions,
      numOfRubricSubQuestions: this.model.numOfRubricSubQuestions + 1,
      rubricWeightsForEachCell: newWeightsForEachCell,
    });
  }

  /**
   * Adds a new choice.
   */
  addNewChoice(): void {
    const newChoices: string[] = this.model.rubricChoices.slice();
    newChoices.push('');

    const newDescriptions: string[][] = this.model.rubricDescriptions.map((arr: string[]) => arr.slice());
    for (const row of newDescriptions) {
      row.push('');
    }

    // update weights
    let newWeightsForEachCell: number[][] = [];
    if (this.model.hasAssignedWeights) {
      newWeightsForEachCell = this.model.rubricWeightsForEachCell.map((arr: number[]) => arr.slice());

      for (const row of newWeightsForEachCell) {
        row.push(0);
      }
    }

    this.triggerModelChangeBatch({
      rubricChoices: newChoices,
      rubricDescriptions: newDescriptions,
      numOfRubricChoices: this.model.numOfRubricChoices + 1,
      rubricWeightsForEachCell: newWeightsForEachCell,
    });
  }

  /**
   * Moves a choice.
   */
  moveChoice(from: number, to: number): void {
    const newChoices: string[] = this.model.rubricChoices.slice();
    moveItemInArray(newChoices, from, to);

    const newDescriptions: string[][] = this.model.rubricDescriptions.map((arr: string[]) => arr.slice());
    for (const row of newDescriptions) {
      moveItemInArray(row, from, to);
    }

    // update weights
    let newWeightsForEachCell: number[][] = [];
    if (this.model.hasAssignedWeights) {
      newWeightsForEachCell = this.model.rubricWeightsForEachCell.map((arr: number[]) => arr.slice());

      for (const row of newWeightsForEachCell) {
        moveItemInArray(row, from, to);
      }
    }

    this.triggerModelChangeBatch({
      rubricChoices: newChoices,
      rubricDescriptions: newDescriptions,
      rubricWeightsForEachCell: newWeightsForEachCell,
    });
  }

  /**
   * Deletes a sub question.
   */
  deleteSubQuestion(index: number): void {
    if (this.model.numOfRubricSubQuestions === 1 || !this.isEditable) {
      // ignore deletion
      return;
    }

    this.simpleModalService.openConfirmationModal('Delete the row?', SimpleModalType.WARNING,
        'Are you sure you want to clear the row?').result.then(() => {

          const newSubQuestions: string[] = this.model.rubricSubQuestions.slice();
          newSubQuestions.splice(index, 1);

          const newDescriptions: string[][] = this.model.rubricDescriptions.map((arr: string[]) => arr.slice());
          newDescriptions.splice(index, 1);

      // update weights
          let newWeightsForEachCell: number[][] = [];
          if (this.model.hasAssignedWeights) {
            newWeightsForEachCell = this.model.rubricWeightsForEachCell.map((arr: number[]) => arr.slice());

            newWeightsForEachCell.splice(index, 1);
          }

          this.triggerModelChangeBatch({
            rubricSubQuestions: newSubQuestions,
            rubricDescriptions: newDescriptions,
            numOfRubricSubQuestions: this.model.numOfRubricSubQuestions - 1,
            rubricWeightsForEachCell: newWeightsForEachCell,
          });

        }, () => {});
  }

  /**
   * Deletes a choice.
   */
  deleteChoice(index: number): void {
    this.simpleModalService.openConfirmationModal('Delete the column?', SimpleModalType.WARNING,
        'Are you sure you want to clear the column?').result.then(() => {
          const newChoices: string[] = this.model.rubricChoices.slice();
          newChoices.splice(index, 1);

          const newDescriptions: string[][] = this.model.rubricDescriptions.map((arr: string[]) => {
            const newArr: string[] = arr.slice();
            newArr.splice(index, 1);
            return newArr;
          });

      // update weights
          let newWeightsForEachCell: number[][] = [];
          if (this.model.hasAssignedWeights) {
            newWeightsForEachCell = this.model.rubricWeightsForEachCell.map((arr: number[]) => {
              const newArr: number[] = arr.slice();
              newArr.splice(index, 1);
              return newArr;
            });
          }

          this.triggerModelChangeBatch({
            rubricChoices: newChoices,
            numOfRubricChoices: this.model.numOfRubricChoices - 1,
            rubricDescriptions: newDescriptions,
            rubricWeightsForEachCell: newWeightsForEachCell,
          });
        }, () => {});
  }

  /**
   * Triggers choices are weighted option.
   */
  triggerChoicesWeight(isEnabled: boolean): void {
    this.triggerModelChangeBatch({
      hasAssignedWeights: isEnabled,
      rubricWeightsForEachCell:
          isEnabled ? this.model.rubricDescriptions.map((arr: string[]) => arr.map(() => 0)) : [],
    });
  }
}
