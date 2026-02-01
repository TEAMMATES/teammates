
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FeedbackQuestionDetails, FeedbackQuestionType } from '../../../types/api-output';
import { ConstsumOptionsQuestionAdditionalInfoComponent } from '../question-types/question-additional-info/constsum-options-question-additional-info.component';
import { ConstsumRecipientsQuestionAdditionalInfoComponent } from '../question-types/question-additional-info/constsum-recipients-question-additional-info.component';
import { ContributionQuestionAdditionalInfoComponent } from '../question-types/question-additional-info/contribution-question-additional-info.component';
import { McqQuestionAdditionalInfoComponent } from '../question-types/question-additional-info/mcq-question-additional-info.component';
import { MsqQuestionAdditionalInfoComponent } from '../question-types/question-additional-info/msq-question-additional-info.component';
import { NumScaleQuestionAdditionalInfoComponent } from '../question-types/question-additional-info/num-scale-question-additional-info.component';
import { RankOptionsQuestionAdditionalInfoComponent } from '../question-types/question-additional-info/rank-options-question-additional-info.component';
import { RankRecipientsQuestionAdditionalInfoComponent } from '../question-types/question-additional-info/rank-recipients-question-additional-info.component';
import { RubricQuestionAdditionalInfoComponent } from '../question-types/question-additional-info/rubric-question-additional-info.component';
import { TextQuestionAdditionalInfoComponent } from '../question-types/question-additional-info/text-question-additional-info.component';

/**
 * Question text with toggle-able additional info.
 */
@Component({
  selector: 'tm-question-text-with-info',
  templateUrl: './question-text-with-info.component.html',
  styleUrls: ['./question-text-with-info.component.scss'],
  imports: [
    ContributionQuestionAdditionalInfoComponent,
    TextQuestionAdditionalInfoComponent,
    McqQuestionAdditionalInfoComponent,
    MsqQuestionAdditionalInfoComponent,
    RankOptionsQuestionAdditionalInfoComponent,
    RankRecipientsQuestionAdditionalInfoComponent,
    RubricQuestionAdditionalInfoComponent,
    ConstsumOptionsQuestionAdditionalInfoComponent,
    ConstsumRecipientsQuestionAdditionalInfoComponent,
    NumScaleQuestionAdditionalInfoComponent,
  ],
})
export class QuestionTextWithInfoComponent {

  @Input() questionNumber: number = 0;
  @Input() questionDetails: FeedbackQuestionDetails = {
    questionType: FeedbackQuestionType.TEXT,
    questionText: '',
  };

  @Output() downloadQuestionResultEvent: EventEmitter<any> = new EventEmitter();

  // enum
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;

  additionalInfoIsExpanded: boolean = false;

  /**
   * Returns true if the question has additional info.
   */
  hasAdditionalInfo(questionDetails: FeedbackQuestionDetails): boolean {
    return questionDetails.questionType !== FeedbackQuestionType.TEXT;
  }

}
