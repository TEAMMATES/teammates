import { Component } from '@angular/core';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType } from '../../../../types/api-output';
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

  PARTICIPANT_TYPES: string[] = [FeedbackParticipantType.STUDENTS, FeedbackParticipantType.STUDENTS_EXCLUDING_SELF,
    FeedbackParticipantType.TEAMS, FeedbackParticipantType.TEAMS_EXCLUDING_SELF, FeedbackParticipantType.INSTRUCTORS];

  constructor() {
    super({
      hasAssignedWeights: false,
      mcqWeights: [],
      mcqOtherWeight: 0,
      numOfMcqChoices: 2,
      mcqChoices: ['', ''],
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
  }

  /**
   * Deletes a Mcq.
   */
  onMcqDeleted(): void {
    this.model.numOfMcqChoices -= 1;
    this.model.mcqChoices.pop();
  }

  /**
   * Display new mcq at specified index
   */
  onMcqEntered(event: string, index: number): void {
    this.model.mcqChoices[index] = event;
  }
}
