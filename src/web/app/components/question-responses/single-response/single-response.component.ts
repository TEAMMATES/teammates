
import { Component, Input } from '@angular/core';
import {
  FeedbackQuestionDetails,
  FeedbackQuestionType,
  FeedbackResponseDetails,
} from '../../../../types/api-output';
import { ConstsumQuestionResponseComponent } from '../../question-types/question-response/constsum-question-response.component';
import { ContributionQuestionResponseComponent } from '../../question-types/question-response/contribution-question-response.component';
import { McqQuestionResponseComponent } from '../../question-types/question-response/mcq-question-response.component';
import { MsqQuestionResponseComponent } from '../../question-types/question-response/msq-question-response.component';
import { NumScaleQuestionResponseComponent } from '../../question-types/question-response/num-scale-question-response.component';
import { RankOptionsQuestionResponseComponent } from '../../question-types/question-response/rank-options-question-response.component';
import { RankRecipientsQuestionResponseComponent } from '../../question-types/question-response/rank-recipients-question-response.component';
import { RubricQuestionResponseComponent } from '../../question-types/question-response/rubric-question-response.component';
import { TextQuestionResponseComponent } from '../../question-types/question-response/text-question-response.component';

/**
 * The component that will map a generic response to its specialized response view component.
 */
@Component({
  selector: 'tm-single-response',
  templateUrl: './single-response.component.html',
  styleUrls: ['./single-response.component.scss'],
  imports: [
    ContributionQuestionResponseComponent,
    TextQuestionResponseComponent,
    ConstsumQuestionResponseComponent,
    NumScaleQuestionResponseComponent,
    RubricQuestionResponseComponent,
    RankOptionsQuestionResponseComponent,
    RankRecipientsQuestionResponseComponent,
    MsqQuestionResponseComponent,
    McqQuestionResponseComponent,
  ],
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
