import { Component, Input } from '@angular/core';
import { FeedbackMcqQuestionDetails, QuestionRecipientType } from '../../../../types/api-output';
import { DEFAULT_MCQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { GeneratedChoicePipe } from '../../teammates-common/generated-choice.pipe';
import { StripHtmlTagsPipe } from '../../teammates-common/strip-html-tags.pipe';

/**
 * Additional info for MCQ questions.
 */
@Component({
  selector: 'tm-mcq-question-additional-info',
  templateUrl: './mcq-question-additional-info.component.html',
  imports: [StripHtmlTagsPipe, GeneratedChoicePipe],
})
export class McqQuestionAdditionalInfoComponent {
  // enum
  QuestionRecipientType: typeof QuestionRecipientType = QuestionRecipientType;

  @Input() questionDetails: FeedbackMcqQuestionDetails = DEFAULT_MCQ_QUESTION_DETAILS();
}
