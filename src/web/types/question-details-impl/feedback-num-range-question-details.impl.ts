import {
    NumRangeQuestionStatisticsCalculation,
  } from '../../app/components/question-types/question-statistics/question-statistics-calculation/num-range-question-statistics-calculation';
  import {
    FeedbackNumericalRangeQuestionDetails, FeedbackParticipantType,
    FeedbackQuestionType, QuestionOutput,
  } from '../api-output';
  import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
  
  /**
   * Concrete implementation of {@link FeedbackNumericalRangeQuestionDetails}.
   */
  export class FeedbackNumericalRangeQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
      implements FeedbackNumericalRangeQuestionDetails {
  
    minScale: number = 1;
    maxScale: number = 5;
    step: number = 0.5;
    questionText: string = '';
    questionType: FeedbackQuestionType = FeedbackQuestionType.NUMRANGE;
  
    constructor(apiOutput: FeedbackNumericalRangeQuestionDetails) {
      super();
      this.minScale = apiOutput.minScale;
      this.maxScale = apiOutput.maxScale;
      this.step = apiOutput.step;
      this.questionText = apiOutput.questionText;
    }

    getQuestionCsvHeaders(): string[] {
        return ['Feedback', 'Start', 'End'];
      }
  
    getQuestionCsvStats(question: QuestionOutput): string[][] {
      const statsRows: string[][] = [];
  
      const statsCalculation: NumRangeQuestionStatisticsCalculation = new NumRangeQuestionStatisticsCalculation(this);
      this.populateQuestionStatistics(statsCalculation, question);
      if (statsCalculation.responses.length === 0) {
        // skip stats for no response
        return [];
      }
      statsCalculation.calculateStatistics();
  
      const header: string[] = ['Team', 'Recipient', 'Average Start', 'Minimum Start', 'Maximum Start',
          'Average End', 'Minimum End', 'Maximum End'];
      const shouldShowAvgExcludingSelf: boolean =
          this.shouldShowAverageExcludingSelfInCsvStats(question, statsCalculation);
      if (shouldShowAvgExcludingSelf) {
        header.push('Average Start excluding self response');
        header.push('Average End excluding self response');
      }
      statsRows.push(header);
  
      for (const team of Object.keys(statsCalculation.teamToRecipientToScores).sort()) {
        for (const recipient of Object.keys(statsCalculation.teamToRecipientToScores[team]).sort()) {
          const stats: any = statsCalculation.teamToRecipientToScores[team][recipient];
          const currRow: string[] = [
            team,
            recipient,
            String(stats.averageStart),
            String(stats.minStart),
            String(stats.maxStart),
            String(stats.averageEnd),
            String(stats.minEnd),
            String(stats.maxEnd),
          ];
          if (shouldShowAvgExcludingSelf) {
            currRow.push(String(stats.averageStartExcludingSelf));
            currRow.push(String(stats.averageEndExcludingSelf));
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
        question: QuestionOutput, statsCalculation: NumRangeQuestionStatisticsCalculation): boolean {
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
  