import { Component } from '@angular/core';
import {
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { QuestionResponse } from './question-response';

/**
 * MSQ question response.
 */
@Component({
  selector: 'tm-msq-question-response',
  templateUrl: './msq-question-response.component.html',
  styleUrls: ['./msq-question-response.component.scss'],
})
export class MsqQuestionResponseComponent
    extends QuestionResponse<FeedbackMsqResponseDetails, FeedbackMsqQuestionDetails> {

  constructor() {
    super({
      answers: [],
      isOther: false,
      otherFieldContent: '',
      questionType: FeedbackQuestionType.MSQ,
    }, {
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
