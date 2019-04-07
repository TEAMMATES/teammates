import { Component, OnInit } from '@angular/core';
import { FeedbackRankOptionsQuestionDetails, FeedbackRankOptionsResponseDetails } from '../../../../types/api-output';
import {
  DEFAULT_RANK_OPTIONS_QUESTION_DETAILS,
  DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { RANK_OPTIONS_ANSWER_NOT_SUBMITTED } from '../../../../types/feedback-response-details';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';

/**
 * The Rank options question submission form for a recipient.
 */
@Component({
  selector: 'tm-rank-options-question-edit-answer-form',
  templateUrl: './rank-options-question-edit-answer-form.component.html',
  styleUrls: ['./rank-options-question-edit-answer-form.component.scss'],
})
export class RankOptionsQuestionEditAnswerFormComponent
    extends QuestionEditAnswerFormComponent<FeedbackRankOptionsQuestionDetails, FeedbackRankOptionsResponseDetails>
    implements OnInit {

  readonly RANK_OPTIONS_ANSWER_NOT_SUBMITTED: number = RANK_OPTIONS_ANSWER_NOT_SUBMITTED;

  constructor() {
    super(DEFAULT_RANK_OPTIONS_QUESTION_DETAILS(), DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS());
  }

  ngOnInit(): void {
    const responseCopy: number[] = this.responseDetails.answers;
    this.responseDetails.answers = Array(this.questionDetails.options.length).fill(RANK_OPTIONS_ANSWER_NOT_SUBMITTED);
    for (let i: number = 0; i < this.responseDetails.answers.length; i += 1) {
      if (responseCopy[i] > 0) {
        this.responseDetails.answers[i] = responseCopy[i];
      }
    }
  }

  /**
   * Populates the possible ranks that can be assigned.
   */
  get ranksToBeAssigned(): number[] {
    const ranks: number[] = [];
    for (let i: number = 1; i <= this.questionDetails.options.length; i += 1) {
      ranks.push(i);
    }
    return ranks;
  }

  /**
   * Assigns a Rank to the option specified by index.
   */
  triggerResponse(index: number, event: any): void {
    this.responseDetails.answers[index] = event;
  }

  /**
   * Checks if the answer has same ranks for different options.
   */
  get isSameRanksAssigned(): boolean {
    const responseCopy: number[] = [];
    for (const response of this.responseDetails.answers) {
      if (response !== RANK_OPTIONS_ANSWER_NOT_SUBMITTED) {
        responseCopy.push(response);
      }
    }
    return (new Set(responseCopy)).size !== responseCopy.length && responseCopy.length !== 0;
  }

  /**
   * Checks if a minimum number of options needs to be ranked.
   */
  get isMinOptionsEnabled(): boolean {
    return this.questionDetails.minOptionsToBeRanked !== 0;
  }

  /**
   * Checks if a maximum number of options can be ranked.
   */
  get isMaxOptionsEnabled(): boolean {
    return this.questionDetails.maxOptionsToBeRanked !== 0;
  }

  /**
   * Checks if the options ranked is less than the minimum required.
   */
  get isOptionsRankedLessThanMin(): boolean {
    let numberOfOptionsRanked: number = 0;
    for (const response of this.responseDetails.answers) {
      if (response !== this.RANK_OPTIONS_ANSWER_NOT_SUBMITTED) {
        numberOfOptionsRanked += 1;
      }
    }
    return (numberOfOptionsRanked < this.questionDetails.minOptionsToBeRanked && numberOfOptionsRanked > 0);
  }

  /**
   * Checks if the options ranked is more than the maximum required.
   */
  get isOptionsRankedMoreThanMax(): boolean {
    let numberOfOptionsRanked: number = 0;
    for (const response of this.responseDetails.answers) {
      if (response !== this.RANK_OPTIONS_ANSWER_NOT_SUBMITTED) {
        numberOfOptionsRanked += 1;
      }
    }
    return numberOfOptionsRanked > this.questionDetails.maxOptionsToBeRanked;
  }
}
