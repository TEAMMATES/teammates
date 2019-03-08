import { Component, OnInit } from '@angular/core';
import {
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_RANK_OPTIONS_QUESTION_DETAILS,
  DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import {
  RANK_OPTIONS_ANSWER_NOT_SUBMITTED,
} from '../../../../types/feedback-response-details';
import { QuestionResponse } from './question-response';

/**
 * Rank options question response.
 */
@Component({
  selector: 'tm-rank-options-question-response',
  templateUrl: './rank-options-question-response.component.html',
  styleUrls: ['./rank-options-question-response.component.scss'],
})
export class RankOptionsQuestionResponseComponent
    extends QuestionResponse<FeedbackRankOptionsResponseDetails, FeedbackRankOptionsQuestionDetails>
    implements OnInit {

  orderedAnswer: string[] = [];

  constructor() {
    super(DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS(), DEFAULT_RANK_OPTIONS_QUESTION_DETAILS());
  }

  ngOnInit(): void {
    let arrayOfRanks: any[][] = [];
    for (let i: number = 0; i < this.questionDetails.options.length; i += 1) {
      const rank: number = this.responseDetails.answers[i];
      if (rank === RANK_OPTIONS_ANSWER_NOT_SUBMITTED) {
        continue;
      }
      arrayOfRanks[rank] = arrayOfRanks[rank] || [];
      arrayOfRanks[rank].push({
        rank,
        option: this.questionDetails.options[i],
      });
    }
    arrayOfRanks = arrayOfRanks.filter((answer: any[]) => answer);
    for (const answers of arrayOfRanks) {
      for (const answer of answers) {
        this.orderedAnswer.push(answer);
      }
    }
  }

}
