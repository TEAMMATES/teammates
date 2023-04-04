import { Component } from '@angular/core';
import { FeedbackRankOptionsQuestionDetails, FeedbackRankOptionsResponseDetails } from '../../../../types/api-output';
import {
  DEFAULT_RANK_OPTIONS_QUESTION_DETAILS,
  DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { NO_VALUE, RANK_OPTIONS_ANSWER_NOT_SUBMITTED } from '../../../../types/feedback-response-details';
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
    extends QuestionEditAnswerFormComponent<FeedbackRankOptionsQuestionDetails, FeedbackRankOptionsResponseDetails> {

  readonly RANK_OPTIONS_ANSWER_NOT_SUBMITTED: number = RANK_OPTIONS_ANSWER_NOT_SUBMITTED;

  constructor() {
    super(DEFAULT_RANK_OPTIONS_QUESTION_DETAILS(), DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS());
  }

  /**
   * Populates the possible Ranks that can be assigned.
   */
  get ranksToBeAssigned(): number[] {
    const ranks: number[] = [];
    for (let i: number = 1; i <= this.questionDetails.options.length; i += 1) {
      ranks.push(i);
    }
    return ranks;
  }

  /**
   * Checks if any one option has been Ranked.
   */
  get isNoOptionRanked(): boolean {
    const isAtLeastOneOptionRanked: boolean = this.responseDetails.answers
        .some((rank: number) => rank !== RANK_OPTIONS_ANSWER_NOT_SUBMITTED);
    return !isAtLeastOneOptionRanked;
  }

  /**
   * Assigns a Rank to the option specified by index.
   */
  triggerResponse(index: number, event: any): void {
    let newAnswers: number[] = this.responseDetails.answers.slice();
    if (newAnswers.length !== this.questionDetails.options.length) {
      // initialize answers array on the fly
      newAnswers = Array(this.questionDetails.options.length).fill(RANK_OPTIONS_ANSWER_NOT_SUBMITTED);
    }

    newAnswers[index] = event;
    this.triggerResponseDetailsChange('answers', newAnswers);
  }

  /**
   * Checks if the answer has same Ranks for different options.
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
   * Checks if a minimum number of options needs to be Ranked.
   */
  get isMinOptionsEnabled(): boolean {
    return this.questionDetails.minOptionsToBeRanked !== NO_VALUE;
  }

  /**
   * Checks if a maximum number of options can be Ranked.
   */
  get isMaxOptionsEnabled(): boolean {
    return this.questionDetails.maxOptionsToBeRanked !== NO_VALUE;
  }

  /**
   * Checks if the options Ranked is less than the minimum required.
   */
  get isOptionsRankedLessThanMin(): boolean {
    const numberOfOptionsRanked: number = this.responseDetails.answers
        .filter((rank: number) => rank !== RANK_OPTIONS_ANSWER_NOT_SUBMITTED).length;
    return (numberOfOptionsRanked < this.questionDetails.minOptionsToBeRanked && numberOfOptionsRanked > 0);
  }

  /**
   * Checks if the options Ranked is more than the maximum required.
   */
  get isOptionsRankedMoreThanMax(): boolean {
    const numberOfOptionsRanked: number = this.responseDetails.answers
        .filter((rank: number) => rank !== RANK_OPTIONS_ANSWER_NOT_SUBMITTED).length;
    return numberOfOptionsRanked > this.questionDetails.maxOptionsToBeRanked;
  }

  getAriaLabelForOption(option: String): String {
    const baseAriaLabel: String = this.getAriaLabel();
    return `${baseAriaLabel} for ${option} Option`;
  }
}
