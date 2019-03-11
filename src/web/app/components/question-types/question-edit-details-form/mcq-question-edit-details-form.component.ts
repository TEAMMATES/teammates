import { Component } from '@angular/core';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';

/**
 * Question details edit form component for mcq scale question.
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
    super({
      hasAssignedWeights: false,
      mcqWeights: [],
      mcqOtherWeight: 0,
      numOfMcqChoices: 2,
      mcqChoices: [' ', ' '],
      otherEnabled: false,
      generatedOptionsEnabled: false,
      generateOptionsFor: FeedbackParticipantType.NONE,
      questionText: '',
      questionType: FeedbackQuestionType.MCQ,
    });
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
   * Deletes a Mcq.
   */
  onMcqDeleted(event: number): void {
    this.model.numOfMcqChoices -= 1;
    this.model.mcqChoices.splice(event, 1);
    if (this.model.hasAssignedWeights) {
      this.model.mcqWeights.splice(event, 1);
    }
  }

  /**
   * Displays new mcq at specified index.
   */
  onMcqEntered(event: string, index: number): void {
    this.model.mcqChoices[index] = event;
  }

  /**
   * Displays new mcq weight at specified index.
   */
  onMcqWeightEntered(event: number, index: number): void {
    this.model.mcqWeights[index] = event;
  }

  /**
   * Tracks the Mcq option by index.
   */
  trackMcqOptions(_index: number, item: string[]): string {
    return item[_index];
  }

  /**
   * Tracks the Mcq weight by index.
   */
  trackMcqWeight(_index: number, item: number[]): number {
    return item[_index];
  }

  /**
   * Displays no feedback participant if option is disabled.
   */
  triggerGenerateOptionsFor(event: any): void {
    if (!event.target.checked) {
      this.model.generateOptionsFor = FeedbackParticipantType.NONE;
    }
  }

  /**
   * Triggers the display of the weight column for the Mcq options according if weights option is checked/unchecked.
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
}
