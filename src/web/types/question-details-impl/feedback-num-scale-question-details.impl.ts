// tslint:disable-next-line:max-line-length
import { NumScaleQuestionStatisticsCalculation } from '../../app/components/question-types/question-statistics/question-statistics-calculation/num-scale-question-statistics-calculation';
import {
  FeedbackNumericalScaleQuestionDetails, FeedbackParticipantType,
  FeedbackQuestionType, QuestionOutput,
} from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackNumericalScaleQuestionDetails}.
 */
export class FeedbackNumericalScaleQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackNumericalScaleQuestionDetails {

  minScale: number = 1;
  maxScale: number = 5;
  step: number = 0.5;
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.NUMSCALE;

  constructor(apiOutput: FeedbackNumericalScaleQuestionDetails) {
    super();
    this.minScale = apiOutput.minScale;
    this.maxScale = apiOutput.maxScale;
    this.step = apiOutput.step;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const statsRows: string[][] = [];

    const statsCalculation: NumScaleQuestionStatisticsCalculation = new NumScaleQuestionStatisticsCalculation(this);
    this.populateQuestionStatistics(statsCalculation, question);
    if (statsCalculation.responses.length === 0) {
      // skip stats for no response
      return [];
    }
    statsCalculation.calculateStatistics();

    const header: string[] = ['Team', 'Recipient', 'Average', 'Minimum', 'Maximum'];
    const shouldShowAvgExcludingSelf: boolean =
        this.shouldShowAverageExcludingSelfInCsvStats(question, statsCalculation);
    if (shouldShowAvgExcludingSelf) {
      header.push('Average excluding self response');
    }
    statsRows.push(header);

    for (const team of Object.keys(statsCalculation.teamToRecipientToScores).sort()) {
      for (const recipient of Object.keys(statsCalculation.teamToRecipientToScores[team]).sort()) {
        const stats: any = statsCalculation.teamToRecipientToScores[team][recipient];
        const currRow: string[] = [
          team,
          recipient,
          String(stats.average),
          String(stats.min),
          String(stats.max),
        ];
        if (shouldShowAvgExcludingSelf) {
          currRow.push(String(stats.averageExcludingSelf));
        }
        statsRows.push(currRow);
      }
    }

    return statsRows;
  }

  /**
   * Checks whether AverageExcludingSelf should appear as a CSV header.
   */
  shouldShowAverageExcludingSelfInCsvStats(
      question: QuestionOutput, statsCalculation: NumScaleQuestionStatisticsCalculation): boolean {
    if (question.feedbackQuestion.recipientType === FeedbackParticipantType.NONE) {
      // General recipient type would not give self response
      // Therefore average exclude self response will always be hidden
      return false;
    }

    // There should exist at least one average score exclude self
    return Object.values(statsCalculation.teamToRecipientToScores)
        .some((recipientStats: Record<string, any>) => Object.values(recipientStats)
            .some((stats: any) => stats.averageExcludingSelf));

  }

  isParticipantCommentsOnResponsesAllowed(): boolean {
    return false;
  }

  isInstructorCommentsOnResponsesAllowed(): boolean {
    return true;
  }
}
