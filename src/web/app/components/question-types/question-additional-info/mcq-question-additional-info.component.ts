import { Component } from '@angular/core';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
} from '../../../../types/api-output';
import { DEFAULT_MCQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for MCQ questions.
 */
@Component({
  selector: 'tm-mcq-question-additional-info',
  templateUrl: './mcq-question-additional-info.component.html',
  styleUrls: ['./mcq-question-additional-info.component.scss'],
})
export class McqQuestionAdditionalInfoComponent extends QuestionAdditionalInfo<FeedbackMcqQuestionDetails> {

  // enum
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;

  constructor() {
    super(DEFAULT_MCQ_QUESTION_DETAILS());
  }

}
