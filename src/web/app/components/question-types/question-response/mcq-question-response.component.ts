import { Component } from '@angular/core';
import {
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { QuestionResponse } from './question-response';

/**
 * MCQ question response.
 */
@Component({
  selector: 'tm-mcq-question-response',
  templateUrl: './mcq-question-response.component.html',
  styleUrls: ['./mcq-question-response.component.scss'],
})
export class McqQuestionResponseComponent
    extends QuestionResponse<FeedbackMcqResponseDetails, FeedbackMcqQuestionDetails> {

  constructor() {
    super({
      answer: '',
      isOther: false,
      otherFieldContent: '',
      questionType: FeedbackQuestionType.MCQ,
    }, {
      hasAssignedWeights: false,
      mcqWeights: [],
      mcqOtherWeight: 0,
      numOfMcqChoices: 0,
      mcqChoices: [],
      otherEnabled: false,
      generateOptionsFor: FeedbackParticipantType.NONE,
      questionType: FeedbackQuestionType.MCQ,
      questionText: '',
    });
  }

}
