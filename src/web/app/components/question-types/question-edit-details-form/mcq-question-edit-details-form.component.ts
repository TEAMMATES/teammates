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

  CURRENT_NUMBER_OF_MCQ_OPTIONS: number = 2;
  IS_GENERATE_OPTIONS_FROM_LIST_CHECKED: boolean = false;
  numbers: number[] = Array(this.CURRENT_NUMBER_OF_MCQ_OPTIONS).fill(0);
  participantTypes: string[] = [FeedbackParticipantType.STUDENTS, FeedbackParticipantType.STUDENTS_EXCLUDING_SELF,
    FeedbackParticipantType.TEAMS, FeedbackParticipantType.TEAMS_EXCLUDING_SELF, FeedbackParticipantType.INSTRUCTORS];

  constructor() {
    super({
      hasAssignedWeights: false,
      mcqWeights: [],
      mcqOtherWeight: 1,
      numOfMcqChoices: 5,
      mcqChoices: [''],
      otherEnabled: false,
      generateOptionsFor: FeedbackParticipantType.NONE,
      questionText: '',
      questionType: FeedbackQuestionType.NUMSCALE,
    });
  }

  /**
   * Listen for checkbox tick event for generating options by list.
   */
  toggleEditable(event: any): void {
    if (event.target.checked) {
      this.IS_GENERATE_OPTIONS_FROM_LIST_CHECKED = true;
    } else {
      this.IS_GENERATE_OPTIONS_FROM_LIST_CHECKED = false;
    }
  }
}
