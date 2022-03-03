import { Component, Input } from '@angular/core';
import {
  FeedbackQuestionDetails,
  FeedbackQuestionType,
  FeedbackResponseDetails,
} from '../../../../types/api-output';

/**
 * The component that will map a generic response to its specialized response view component.
 */
@Component({
  selector: 'tm-single-response',
  templateUrl: './single-response.component.html',
  styleUrls: ['./single-response.component.scss'],
})
export class SingleResponseComponent {

  @Input() responseDetails: FeedbackResponseDetails = {
    questionType: FeedbackQuestionType.TEXT,
  };

  @Input() questionDetails: FeedbackQuestionDetails = {
    questionType: FeedbackQuestionType.TEXT,
    questionText: '',
  };

  @Input() isStudentPage: boolean = false;
  @Input() statistics: string = '';
  @Input() giverEmail: string = '';
  @Input() recipientEmail: string = '';

  // enum
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;

}
