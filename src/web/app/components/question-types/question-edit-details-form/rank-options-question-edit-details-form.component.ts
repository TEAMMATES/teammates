import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component } from '@angular/core';
import { FeedbackRankOptionsQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_RANK_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';

/**
 * Question details edit form component for Rank Options question.
 */
@Component({
  selector: 'tm-rank-options-question-edit-details-form',
  templateUrl: './rank-options-question-edit-details-form.component.html',
  styleUrls: ['./rank-options-question-edit-details-form.component.scss'],
})
export class RankOptionsQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackRankOptionsQuestionDetails> {

  constructor() {
    super(DEFAULT_RANK_OPTIONS_QUESTION_DETAILS());
  }

  /**
   * Increases number of Rank options.
   */
  increaseNumberOfRankOptions(): void {
    this.model.options.push('');
  }

  /**
   * Reorders the list on dragging the Rank options.
   */
  onRankOptionDropped(event: CdkDragDrop<string[]>): void {
    moveItemInArray(this.model.options, event.previousIndex, event.currentIndex);
  }

  /**
   * Tracks the Rank option by index.
   */
  trackRankOption(index: number, item: string[]): string {
    return item[index];
  }

  /**
   * Deletes a Rank option.
   */
  onRankOptionDeleted(event: number): void {
    this.model.options.splice(event, 1);
    if (this.model.maxOptionsToBeRanked > this.model.options.length) {
      this.model.maxOptionsToBeRanked = this.model.options.length;
    }
  }

  /**
   * Displays new Rank option at specified index.
   */
  onRankOptionEntered(event: string, index: number): void {
    this.model.options[index] = event;
  }

  /**
   * Assigns a default value to minOptionsToBeRanked when checkbox is clicked.
   */
  triggerMinOptionsToBeRankedChange(event: any): void {
    this.model.minOptionsToBeRanked = event.target.checked ? 1 : -1;
  }

  /**
   * Assigns a default value to maxOptionsToBeRanked when checkbox is clicked.
   */
  triggerMaxOptionsToBeRankedChange(event: any): void {
    this.model.maxOptionsToBeRanked = event.target.checked ? 1 : -1;
  }

  /**
   * Checks if the minOptionsToBeRanked checkbox is enabled.
   */
  get isMinOptionsToBeRankedEnabled(): boolean {
    return this.model.minOptionsToBeRanked !== -1;
  }

  /**
   * Checks if the maxOptionsToBeRanked checkbox is enabled.
   */
  get isMaxOptionsToBeRankedEnabled(): boolean {
    return this.model.maxOptionsToBeRanked !== -1;
  }

  /**
   * Displays minOptionsToBeRanked value.
   */
  get displayValueForMinOptionsToBeRanked(): number {
    return this.model.minOptionsToBeRanked === -1 ? 1 : this.model.minOptionsToBeRanked;
  }

  /**
   * Displays minOptionsToBeRanked value.
   */
  get displayValueForMaxOptionsToBeRanked(): number {
    return this.model.maxOptionsToBeRanked === -1 ? 1 : this.model.maxOptionsToBeRanked;
  }

}
