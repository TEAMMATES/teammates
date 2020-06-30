import {
  FeedbackQuestionType, FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails as FeedbackRankOptionsResponseDetails,
} from '../api-output';
import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';

/**
 * Concrete implementation of {@link FeedbackRankOptionsResponseDetails}.
 */
export class FeedbackRankOptionsResponseDetailsImpl
    extends AbstractFeedbackResponseDetails<FeedbackRankOptionsQuestionDetails>
    implements FeedbackRankOptionsResponseDetails {

  answers: number[] = [];
  questionType: FeedbackQuestionType = FeedbackQuestionType.RANK_OPTIONS;

  constructor(apiOutput: FeedbackRankOptionsResponseDetails) {
    super();
    this.answers = apiOutput.answers;
  }

  getResponseCsvAnswers(correspondingQuestionDetails: FeedbackRankOptionsQuestionDetails): string[][] {
    const answers: string[] = [];
    for (let rank: number = 1; rank <= correspondingQuestionDetails.options.length; rank += 1) {
      const selectedOptionsForCurrentRank: string[] = this.answers.reduce(
          (selectedOptions: string[], currRank: number, index: number) => {
            if (currRank === rank) {
              selectedOptions.push(correspondingQuestionDetails.options[index]);
            }
            return selectedOptions;
          }, []);
      answers.push(selectedOptionsForCurrentRank.join(', '));
    }
    return [['', ...answers]];
  }

}
