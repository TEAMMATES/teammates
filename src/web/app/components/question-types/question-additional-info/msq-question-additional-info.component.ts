import { Component, Input } from '@angular/core';
import { FeedbackMsqQuestionDetails, FeedbackParticipantType } from '../../../../types/api-output';
import { DEFAULT_MSQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { GeneratedChoicePipe } from '../../teammates-common/generated-choice.pipe';

/**
 * Additional info for MSQ questions.
 */
@Component({
  selector: 'tm-msq-question-additional-info',
  templateUrl: './msq-question-additional-info.component.html',
  imports: [GeneratedChoicePipe],
})
export class MsqQuestionAdditionalInfoComponent {
  // enum
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;

  @Input() questionDetails: FeedbackMsqQuestionDetails = DEFAULT_MSQ_QUESTION_DETAILS();
}
