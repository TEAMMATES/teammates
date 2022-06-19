import {
    FeedbackNumericalRangeQuestionDetails,
    FeedbackNumericalRangeResponseDetails,
    FeedbackQuestionType,
  } from '../api-output';
  import { NUMERICAL_RANGE_END_NOT_SUBMITTED, NUMERICAL_RANGE_START_NOT_SUBMITTED } from '../feedback-response-details';
  import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';
  
  /**
   * Concrete implementation of {@link FeedbackNumericalRangeResponseDetails}.
   */
  export class FeedbackNumericalRangeResponseDetailsImpl
      extends AbstractFeedbackResponseDetails<FeedbackNumericalRangeQuestionDetails>
      implements FeedbackNumericalRangeResponseDetails {
  
    start: number = NUMERICAL_RANGE_START_NOT_SUBMITTED;
    end: number = NUMERICAL_RANGE_END_NOT_SUBMITTED;
    questionType: FeedbackQuestionType = FeedbackQuestionType.NUMRANGE;
  
    constructor(apiOutput: FeedbackNumericalRangeResponseDetails) {
      super();
      this.start = apiOutput.start;
      this.end = apiOutput.end;
    }
  
    // Todo
    getResponseCsvAnswers(): string[][] {
      const start: number = this.start;
      const end: number = this.end;
      // up to three decimal places
      const roundedStart: number = Math.round((start + Number.EPSILON) * 1000) / 1000;4
      const roundedEnd: number = Math.round((end + Number.EPSILON) * 1000) / 1000;
      return [["", String(roundedStart), String(roundedEnd)]];
    }
  
  }
  