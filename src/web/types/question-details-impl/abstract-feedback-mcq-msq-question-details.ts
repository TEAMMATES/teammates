// tslint:disable-next-line:max-line-length
import { McqMsqQuestionStatisticsCalculation } from '../../app/components/question-types/question-statistics/question-statistics-calculation/mcq-msq-question-statistics-calculation';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Abstract class for MCQ/MSQ question detail.
 */
export abstract class AbstractFeedbackMcqMsqQuestionDetails extends AbstractFeedbackQuestionDetails {

  protected getQuestionCsvStatsFrom(
      statsCalculation: McqMsqQuestionStatisticsCalculation, hasAssignedWeights: boolean): string[][] {
    const statsRows: string[][] = [];

    if (hasAssignedWeights) {
      statsRows.push(['Choice', 'Weight', 'Response Count', 'Percentage (%)', 'Weighted Percentage (%)']);
    } else {
      statsRows.push(['Choice', 'Response Count', 'Percentage (%)']);
    }

    Object.keys(statsCalculation.answerFrequency).sort().forEach((answer: string) => {
      if (hasAssignedWeights) {
        statsRows.push([
          answer,
          String(statsCalculation.weightPerOption[answer]),
          String(statsCalculation.answerFrequency[answer]),
          String(statsCalculation.percentagePerOption[answer]),
          String(statsCalculation.weightedPercentagePerOption[answer]),
        ]);
      } else {
        statsRows.push([
          answer,
          String(statsCalculation.answerFrequency[answer]),
          String(statsCalculation.percentagePerOption[answer]),
        ]);
      }
    });

    if (!hasAssignedWeights) {
      return statsRows;
    }

    // generate per recipient stats
    statsRows.push([], ['Per Recipient Statistics']);

    statsRows.push([
      'Team', 'Recipient Name',
      ...Object.keys(statsCalculation.weightPerOption)
          .map((choice: string) => `${choice} [${statsCalculation.weightPerOption[choice]}]`),
      'Total', 'Average']);

    Object.keys(statsCalculation.perRecipientResponses).sort().forEach((recipient: string) => {
      const recipientResponses: any = statsCalculation.perRecipientResponses[recipient];
      statsRows.push([
        recipientResponses.recipientTeam,
        recipientResponses.recipient,
        ...Object.keys(statsCalculation.weightPerOption)
            .map((choice: string) => String(recipientResponses.responses[choice])),
        String(recipientResponses.total),
        String(recipientResponses.average),
      ]);
    });

    return statsRows;
  }
}
