import { Component } from '@angular/core';
import { FeedbackRankOptionsQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_RANK_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
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
    super(DEFAULT_RANK_OPTIONS_QUESTION_DETAILS());
  }

}
