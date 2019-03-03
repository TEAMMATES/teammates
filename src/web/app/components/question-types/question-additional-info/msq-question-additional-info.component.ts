import { Component } from '@angular/core';
import {
  FeedbackMsqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for MSQ questions.
 */
@Component({
  selector: 'tm-msq-question-additional-info',
  templateUrl: './msq-question-additional-info.component.html',
  styleUrls: ['./msq-question-additional-info.component.scss'],
})
export class MsqQuestionAdditionalInfoComponent extends QuestionAdditionalInfo<FeedbackMsqQuestionDetails> {

  // enum
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;

  constructor() {
    super({
      msqChoices: [],
      otherEnabled: false,
      generateOptionsFor: FeedbackParticipantType.NONE,
      maxSelectableChoices: Number.MIN_VALUE,
      minSelectableChoices: Number.MIN_VALUE,
      hasAssignedWeights: false,
      msqWeights: [],
      msqOtherWeight: 0,
      questionType: FeedbackQuestionType.MSQ,
      questionText: '',
    });
  }

}
