import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component } from '@angular/core';
import { FeedbackMsqQuestionDetails, FeedbackParticipantType } from '../../../../types/api-output';
import { DEFAULT_MSQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
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
    moveItemInArray(this.model.msqChoices, event.previousIndex, event.currentIndex);
    moveItemInArray(this.model.msqWeights, event.previousIndex, event.currentIndex);
  }

  /**
   * Displays new Msq weight at specified index.
   */
  onMsqWeightEntered(event: number, index: number): void {
    this.model.msqWeights[index] = event;
  }

  /**
   * Increases number of Msq options.
   */
  increaseNumberOfMsqOptions(): void {
    this.model.msqChoices.push('');
    if (this.model.hasAssignedWeights) {
      this.model.msqWeights.push(0);
    }
  }

  /**
   * Deletes a Msq option.
   */
  onMsqOptionDeleted(event: number): void {
    this.model.msqChoices.splice(event, 1);
    if (this.model.hasAssignedWeights) {
      this.model.msqWeights.splice(event, 1);
    }
  }

  /**
   * Displays maxSelectableOption value.
   */
  get displayValueForMaxSelectableOption(): number {
    return this.model.maxSelectableChoices === -1 ? 2 : this.model.maxSelectableChoices;
  }

  /**
   * Displays minSelectableOption value.
   */
  get displayValueForMinSelectableOption(): number {
    return this.model.minSelectableChoices === -1 ? 1 : this.model.minSelectableChoices;
  }

  /**
   * Displays new Msq option at specified index.
   */
  onMsqOptionEntered(event: string, index: number): void {
    this.model.msqChoices[index] = event;
  }

  /**
   * Triggers the display of the weight for the other option.
   */
  triggerOtherWeight(event: any): void {
    if (!event.target.checked) {
      this.model.msqOtherWeight = 0;
    }
  }

  /**
   * Assigns a default value to generateOptionsFor when checkbox is clicked.
   */
  triggerGeneratedOptionsChange(event: any): void {
    this.model.generateOptionsFor
        = event.target.checked ? FeedbackParticipantType.STUDENTS : FeedbackParticipantType.NONE;
  }

  /**
   * Assigns a default value to maxSelectableOptions when checkbox is clicked.
   */
  triggerMaxSelectableOptionsChange(event: any): void {
    this.model.maxSelectableChoices = event.target.checked ? 2 : -1;
  }

  /**
   * Assigns a default value to minSelectableOptions when checkbox is clicked.
   */
  triggerMinSelectableOptionsChange(event: any): void {
    this.model.minSelectableChoices = event.target.checked ? 1 : -1;
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
    return this.model.maxSelectableChoices !== -1;
  }

  /**
   * Checks if the minSelectedChoices checkbox is enabled.
   */
  get isMinSelectableChoicesEnabled(): boolean {
    return this.model.minSelectableChoices !== -1;
  }

  /**
   * Triggers the display of the weight column for the Msq options if weights option is checked/unchecked.
   */
  triggerWeightsColumn(event: any): void {
    if (!event.target.checked) {
      this.model.msqWeights = [];
      this.model.msqOtherWeight = 0;
    } else {
      this.model.msqWeights = Array(this.model.msqChoices.length).fill(0);
    }
  }
}
