
import { Component } from '@angular/core';
import { QuestionAdditionalInfo } from './question-additional-info';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
} from '../../../../types/api-output';
import { DEFAULT_MCQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { GeneratedChoicePipe } from '../../teammates-common/generated-choice.pipe';
import { StripHtmlTagsPipe } from '../../teammates-common/strip-html-tags.pipe';

/**
 * Additional info for MCQ questions.
 */
@Component({
  selector: 'tm-mcq-question-additional-info',
  templateUrl: './mcq-question-additional-info.component.html',
  styleUrls: ['./mcq-question-additional-info.component.scss'],
  imports: [
    StripHtmlTagsPipe,
    GeneratedChoicePipe,
  ],
})
export class McqQuestionAdditionalInfoComponent extends QuestionAdditionalInfo<FeedbackMcqQuestionDetails> {

  // enum
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;

  constructor() {
    super(DEFAULT_MCQ_QUESTION_DETAILS());
  }

}
