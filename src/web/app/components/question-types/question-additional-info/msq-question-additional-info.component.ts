import { Component } from '@angular/core';
import { QuestionAdditionalInfo } from './question-additional-info';
import {
  FeedbackMsqQuestionDetails,
  FeedbackParticipantType,
} from '../../../../types/api-output';
import { DEFAULT_MSQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { GeneratedChoicePipe } from '../../teammates-common/generated-choice.pipe';

/**
 * Additional info for MSQ questions.
 */
@Component({
  selector: 'tm-msq-question-additional-info',
  templateUrl: './msq-question-additional-info.component.html',
  styleUrls: ['./msq-question-additional-info.component.scss'],
  imports: [
    GeneratedChoicePipe,
  ],
})
export class MsqQuestionAdditionalInfoComponent extends QuestionAdditionalInfo<FeedbackMsqQuestionDetails> {

  // enum
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;

  constructor() {
    super(DEFAULT_MSQ_QUESTION_DETAILS());
  }

}
