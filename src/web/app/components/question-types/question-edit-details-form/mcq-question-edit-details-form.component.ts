import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component } from '@angular/core';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
} from '../../../../types/api-output';
import { DEFAULT_MCQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';

/**
 * Question details edit form component for Mcq question.
 */
@Component({
  selector: 'tm-mcq-question-edit-details-form',
  templateUrl: './mcq-question-edit-details-form.component.html',
  styleUrls: ['./mcq-question-edit-details-form.component.scss'],
})
export class McqQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackMcqQuestionDetails> {

  readonly PARTICIPANT_TYPES: string[] = [FeedbackParticipantType.STUDENTS,
    FeedbackParticipantType.STUDENTS_EXCLUDING_SELF, FeedbackParticipantType.TEAMS,
    FeedbackParticipantType.TEAMS_EXCLUDING_SELF, FeedbackParticipantType.INSTRUCTORS];

  constructor() {
    super(DEFAULT_MCQ_QUESTION_DETAILS());
  }

  /**
   * Reorders the list on dragging the Mcq options.
   */
  onMcqOptionDropped(event: CdkDragDrop<string[]>): void {
    moveItemInArray(this.model.mcqChoices, event.previousIndex, event.currentIndex);
    moveItemInArray(this.model.mcqWeights, event.previousIndex, event.currentIndex);
  }

  /**
   * Increases number of Mcq options.
   */
  increaseNumberOfOptions(): void {
    this.model.numOfMcqChoices += 1;
    this.model.mcqChoices.push('');
    if (this.model.hasAssignedWeights) {
      this.model.mcqWeights.push(0);
    }
  }

  /**
   * Deletes a Mcq option.
   */
  onMcqOptionDeleted(event: number): void {
    this.model.numOfMcqChoices -= 1;
    this.model.mcqChoices.splice(event, 1);
    if (this.model.hasAssignedWeights) {
      this.model.mcqWeights.splice(event, 1);
    }
  }

  /**
   * Displays new Mcq option at specified index.
   */
  onMcqOptionEntered(event: string, index: number): void {
    this.model.mcqChoices[index] = event;
  }

  /**
   * Displays new Mcq weight at specified index.
   */
  onMcqWeightEntered(event: number, index: number): void {
    this.model.mcqWeights[index] = event;
  }

  /**
   * Tracks the Mcq option by index.
   */
  trackMcqOption(index: number, item: string[]): string {
    return item[index];
  }

  /**
   * Tracks the Mcq weight by index.
   */
  trackMcqWeight(index: number, item: number[]): number {
    return item[index];
  }

  /**
   * Triggers the display of the weight column for the Mcq options if weights option is checked/unchecked.
   */
  triggerWeightsColumn(event: any): void {
    if (!event.target.checked) {
      this.model.mcqWeights = [];
      this.model.mcqOtherWeight = 0;
    } else {
      this.model.mcqWeights = Array(this.model.numOfMcqChoices).fill(0);
    }
  }

  /**
   * Triggers the display of the weight for the other option.
   */
  triggerOtherWeight(event: any): void {
    if (!event.target.checked) {
      this.model.mcqOtherWeight = 0;
    }
  }

  /**
   * Assigns a default value to generateOptionsFor when checkbox is clicked.
   */
  triggerGeneratedOptionsChange(event: any): void {
    if (!event.target.checked) {
      this.model.generateOptionsFor = FeedbackParticipantType.NONE;
    } else {
      this.model.generateOptionsFor = FeedbackParticipantType.STUDENTS;
    }
  }

  /**
   * Checks if the generatedOptionsFor checkbox is enabled.
   */
  get isGeneratedOptionsEnabled(): boolean {
    return this.model.generateOptionsFor !== FeedbackParticipantType.NONE;
  }
}
