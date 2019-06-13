import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component } from '@angular/core';
import { FeedbackMsqQuestionDetails, FeedbackParticipantType } from '../../../../types/api-output';
import { DEFAULT_MSQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { NO_VALUE } from '../../../../types/feedback-response-details';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';

/**
 * Question details edit form component for Msq question.
 */
@Component({
  selector: 'tm-msq-question-edit-details-form',
  templateUrl: './msq-question-edit-details-form.component.html',
  styleUrls: ['./msq-question-edit-details-form.component.scss'],
})
export class MsqQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackMsqQuestionDetails> {

  readonly PARTICIPANT_TYPES: string[] = [FeedbackParticipantType.STUDENTS,
    FeedbackParticipantType.STUDENTS_EXCLUDING_SELF, FeedbackParticipantType.TEAMS,
    FeedbackParticipantType.TEAMS_EXCLUDING_SELF, FeedbackParticipantType.INSTRUCTORS];

  constructor() {
    super(DEFAULT_MSQ_QUESTION_DETAILS());
  }

  /**
   * Reorders the list on dragging the Msq options.
   */
  onMsqOptionDropped(event: CdkDragDrop<string[]>): void {
    const newWeights: number[] = this.model.msqWeights.slice();
    const newOptions: string[] = this.model.msqChoices.slice();
    moveItemInArray(newOptions, event.previousIndex, event.currentIndex);
    moveItemInArray(newWeights, event.previousIndex, event.currentIndex);
    const fieldsToUpdate: any = {};
    fieldsToUpdate.msqChoices = newOptions;
    fieldsToUpdate.msqWeights = newWeights;
    this.triggerModelChangeBatch(fieldsToUpdate);
  }

  /**
   * Displays new Msq weight at specified index.
   */
  onMsqWeightEntered(event: number, index: number): void {
    const newWeights: number[] = this.model.msqWeights.slice();
    newWeights[index] = event;
    this.triggerModelChange('msqWeights', newWeights);
  }

  /**
   * Increases number of Msq options.
   */
  increaseNumberOfMsqOptions(): void {
    const fieldsToUpdate: any = {};
    const newOptions: string[] = this.model.msqChoices.slice();
    newOptions.push('');
    fieldsToUpdate.msqChoices = newOptions;
    if (this.model.hasAssignedWeights) {
      const newWeights: number[] = this.model.msqWeights.slice();
      newWeights.push(0);
      fieldsToUpdate.msqWeights = newWeights;
    }
    this.triggerModelChangeBatch(fieldsToUpdate);
  }

  /**
   * Deletes a Msq option.
   */
  onMsqOptionDeleted(event: number): void {
    const fieldsToUpdate: any = {};
    const newOptions: string[] = this.model.msqChoices.slice();
    newOptions.splice(event, 1);
    fieldsToUpdate.msqChoices = newOptions;
    if (this.model.hasAssignedWeights) {
      const newWeights: number[] = this.model.msqWeights.slice();
      newWeights.splice(event, 1);
      fieldsToUpdate.msqWeights = newWeights;
    }
    this.triggerModelChangeBatch(fieldsToUpdate);
  }

  /**
   * Displays maxSelectableOption value.
   */
  get displayValueForMaxSelectableOption(): number {
    return this.model.maxSelectableChoices === NO_VALUE ? 2 : this.model.maxSelectableChoices;
  }

  /**
   * Displays minSelectableOption value.
   */
  get displayValueForMinSelectableOption(): number {
    return this.model.minSelectableChoices === NO_VALUE ? 1 : this.model.minSelectableChoices;
  }

  /**
   * Displays new Msq option at specified index.
   */
  onMsqOptionEntered(event: string, index: number): void {
    const newOptions: string[] = this.model.msqChoices.slice();
    newOptions[index] = event;
    this.triggerModelChange('msqChoices', newOptions);
  }

  /**
   * Triggers the display of the weight for the other option.
   */
  triggerOtherWeight(event: any): void {
    if (!event.target.checked) {
      this.triggerModelChange('msqOtherWeight', 0);
    }
  }

  /**
   * Assigns a default value to generateOptionsFor when checkbox is clicked.
   */
  triggerGeneratedOptionsChange(event: any): void {
    const feedbackParticipantType: FeedbackParticipantType
        = event.target.checked ? FeedbackParticipantType.STUDENTS : FeedbackParticipantType.NONE;
    this.triggerModelChange('generateOptionsFor', feedbackParticipantType);
  }

  /**
   * Assigns a default value to maxSelectableOptions when checkbox is clicked.
   */
  triggerMaxSelectableOptionsChange(event: any): void {
    const maxSelectableChoices: number = event.target.checked ? 2 : NO_VALUE;
    this.triggerModelChange('maxSelectableChoices', maxSelectableChoices);
  }

  /**
   * Assigns a default value to minSelectableOptions when checkbox is clicked.
   */
  triggerMinSelectableOptionsChange(event: any): void {
    const minSelectableChoices: number = event.target.checked ? 1 : NO_VALUE;
    this.triggerModelChange('minSelectableChoices', minSelectableChoices);
  }

  /**
   * Tracks the Msq option by index.
   */
  trackMsqOption(index: number, item: string[]): string {
    return item[index];
  }

  /**
   * Tracks the Msq weight by index.
   */
  trackMsqWeight(index: number, item: number[]): number {
    return item[index];
  }

  /**
   * Checks if the generatedOptionsFor checkbox is enabled.
   */
  get isGeneratedOptionsEnabled(): boolean {
    return this.model.generateOptionsFor !== FeedbackParticipantType.NONE;
  }

  /**
   * Checks if the maxSelectedChoices checkbox is enabled.
   */
  get isMaxSelectableChoicesEnabled(): boolean {
    return this.model.maxSelectableChoices !== NO_VALUE;
  }

  /**
   * Checks if the minSelectedChoices checkbox is enabled.
   */
  get isMinSelectableChoicesEnabled(): boolean {
    return this.model.minSelectableChoices !== NO_VALUE;
  }

  /**
   * Returns maximum value that minSelectable option can take.
   */
  get maxMinSelectableValue(): number {
    if (!this.isMaxSelectableChoicesEnabled) {
      return this.model.msqChoices.length;
    }
    return this.model.maxSelectableChoices;
  }

  /**
   * Triggers the display of the weight column for the Msq options if weights option is checked/unchecked.
   */
  triggerWeightsColumn(event: any): void {
    const fieldsToUpdate: any = {};
    if (!event.target.checked) {
      fieldsToUpdate.hasAssignedWeights = false;
      fieldsToUpdate.msqWeights = [];
      fieldsToUpdate.msqOtherWeight = 0;
    } else {
      fieldsToUpdate.hasAssignedWeights = true;
      fieldsToUpdate.msqWeights = Array(this.model.msqChoices.length).fill(0);
    }
    this.triggerModelChangeBatch(fieldsToUpdate);
  }
}
