import { Component } from '@angular/core';
import {
  FeedbackQuestionType,
  FeedbackRankRecipientsQuestionDetails,
} from '../../../../types/api-output';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for rank recipients questions.
 */
@Component({
  selector: 'tm-rank-recipients-question-additional-info',
  templateUrl: './rank-recipients-question-additional-info.component.html',
  styleUrls: ['./rank-recipients-question-additional-info.component.scss'],
})
export class RankRecipientsQuestionAdditionalInfoComponent
    extends QuestionAdditionalInfo<FeedbackRankRecipientsQuestionDetails> {

  constructor() {
    super({
      minOptionsToBeRanked: Number.MIN_VALUE,
      maxOptionsToBeRanked: Number.MIN_VALUE,
      areDuplicatesAllowed: false,
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      questionText: '',
    });
  }

}
