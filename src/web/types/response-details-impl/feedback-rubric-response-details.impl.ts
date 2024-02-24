import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';
import { StringHelper } from '../../services/string-helper';
import {
  FeedbackQuestionType, FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
} from '../api-output';
import { RUBRIC_ANSWER_NOT_CHOSEN } from '../feedback-response-details';

/**
 * Concrete implementation of {@link FeedbackRubricResponseDetails}.
 */
export class FeedbackRubricResponseDetailsImpl extends AbstractFeedbackResponseDetails<FeedbackRubricQuestionDetails>
    implements FeedbackRubricResponseDetails {

  answer: number[] = [];
  questionType: FeedbackQuestionType = FeedbackQuestionType.RUBRIC;

  constructor(apiOutput: FeedbackRubricResponseDetails) {
    super();
    this.answer = apiOutput.answer;
  }

  getResponseCsvAnswers(correspondingQuestionDetails: FeedbackRubricQuestionDetails): string[][] {
    const answers: string[][] = [];

    this.answer.forEach((chosenIndex: number, currSubQuestionIndex: number) => {
      const currentAnswer: string[] = [];

      const currSubQuestionAbbr: string = StringHelper.integerToLowerCaseAlphabeticalIndex(currSubQuestionIndex + 1);
      let currChoiceStr: string = 'No Response';
      let currChoiceNumberStr: string = '';
      if (chosenIndex !== RUBRIC_ANSWER_NOT_CHOSEN) {
        currChoiceStr = correspondingQuestionDetails.rubricChoices[chosenIndex];
        currChoiceNumberStr = String(chosenIndex + 1);
      }
      currentAnswer.push(currSubQuestionAbbr);
      currentAnswer.push(currChoiceStr);
      currentAnswer.push(currChoiceNumberStr);

      answers.push(currentAnswer);
    });

    return answers;
  }

}
