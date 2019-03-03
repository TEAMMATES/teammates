import { Component } from '@angular/core';
import {
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
} from '../../../../types/api-output';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for rank options questions.
 */
@Component({
  selector: 'tm-rank-options-question-additional-info',
  templateUrl: './rank-options-question-additional-info.component.html',
  styleUrls: ['./rank-options-question-additional-info.component.scss'],
})
export class RankOptionsQuestionAdditionalInfoComponent
    extends QuestionAdditionalInfo<FeedbackRankOptionsQuestionDetails> {

  constructor() {
    super({
      minOptionsToBeRanked: Number.MIN_VALUE,
      maxOptionsToBeRanked: Number.MIN_VALUE,
      areDuplicatesAllowed: false,
      options: [],
      questionType: FeedbackQuestionType.RANK_OPTIONS,
      questionText: '',
    });
  }

}
