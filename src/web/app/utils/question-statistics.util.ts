import {
  ConstsumOptionsQuestionStatistics,
  ConstsumRecipientsQuestionStatistics,
  NumScaleQuestionStatistics,
  RankOptionsQuestionStatistics,
  RankRecipientsQuestionStatistics,
  Response,
} from '../../types/question-statistics.model';
import {
  FeedbackConstantSumOptionsQuestionDetails,
  FeedbackConstantSumOptionsResponseDetails,
  FeedbackConstantSumRecipientsResponseDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsResponseDetails,
  QuestionRecipientType,
} from '../../types/api-output';
import {
  RANK_OPTIONS_ANSWER_NOT_SUBMITTED,
} from '../../types/feedback-response-details';

export function calculateConstsumOptionsQuestionStatistics(
  question: FeedbackConstantSumOptionsQuestionDetails,
  responses: Response<FeedbackConstantSumOptionsResponseDetails>[],
): ConstsumOptionsQuestionStatistics {
  const stats: ConstsumOptionsQuestionStatistics = {
    pointsPerOption: {},
    totalPointsPerOption: {},
    averagePointsPerOption: {},
  };

  const options: string[] = question.constSumOptions;
  for (const option of options) {
    stats.pointsPerOption[option] = [];
  }
  for (const response of responses) {
    const answers: number[] = response.responseDetails.answers;
    for (let i = 0; i < options.length; i += 1) {
      const option: string = options[i];
      const answer: number = answers[i];
      stats.pointsPerOption[option].push(answer);
    }
  }
  for (const option of Object.keys(stats.pointsPerOption)) {
    stats.pointsPerOption[option].sort((a: number, b: number) => a - b);
    const answers: number[] = stats.pointsPerOption[option];
    const sum: number = answers.reduce((a: number, b: number) => a + b, 0);
    stats.totalPointsPerOption[option] = sum;
    stats.averagePointsPerOption[option] = +(answers.length === 0 ? 0 : sum / answers.length).toFixed(2);
  }

  return stats;
}

export function calculateConstsumRecipientsQuestionStatistics(
  responses: Response<FeedbackConstantSumRecipientsResponseDetails>[],
  recipientType: QuestionRecipientType,
): ConstsumRecipientsQuestionStatistics {
  const stats: ConstsumRecipientsQuestionStatistics = {
    emailToTeamName: {},
    emailToName: {},
    pointsPerOption: {},
    totalPointsPerOption: {},
    averagePointsPerOption: {},
    averagePointsExcludingSelf: {},
  };

  const pointsPerOptionToSelf: Record<string, number> = {};

  const isRecipientTeam: boolean =
    recipientType === QuestionRecipientType.TEAMS || recipientType === QuestionRecipientType.TEAMS_EXCLUDING_SELF;

  for (const response of responses) {
    const identifier: string = isRecipientTeam ? response.recipient : (response.recipientEmail ?? response.recipient);

    stats.pointsPerOption[identifier] = stats.pointsPerOption[identifier] ?? [];
    stats.pointsPerOption[identifier].push(response.responseDetails.answers[0]);

    if (response.giver === response.recipient) {
      pointsPerOptionToSelf[identifier] = response.responseDetails.answers[0];
    }

    if (!stats.emailToTeamName[identifier]) {
      stats.emailToTeamName[identifier] = isRecipientTeam ? '' : response.recipientTeam;
    }
    if (!stats.emailToName[identifier]) {
      stats.emailToName[identifier] = response.recipient;
    }
  }

  for (const recipient of Object.keys(stats.pointsPerOption)) {
    stats.pointsPerOption[recipient].sort((a: number, b: number) => a - b);
    const answers: number[] = stats.pointsPerOption[recipient];
    const sum: number = answers.reduce((a: number, b: number) => a + b, 0);
    stats.totalPointsPerOption[recipient] = sum;
    stats.averagePointsPerOption[recipient] = +(answers.length === 0 ? 0 : sum / answers.length).toFixed(2);
    stats.averagePointsExcludingSelf[recipient] = stats.averagePointsPerOption[recipient];
    if (stats.averagePointsPerOption[recipient] && pointsPerOptionToSelf[recipient] !== undefined) {
      stats.averagePointsExcludingSelf[recipient] = +(
        answers.length === 1 ? 0 : (sum - pointsPerOptionToSelf[recipient]) / (answers.length - 1)
      ).toFixed(2);
    }
  }

  return stats;
}

export function calculateNumScaleQuestionStatistics(
  responses: Response<FeedbackNumericalScaleResponseDetails>[],
): NumScaleQuestionStatistics {
  const stats: NumScaleQuestionStatistics = {
    teamToRecipientToScores: {},
    recipientEmails: {},
  };

  for (const response of responses) {
    const { giver }: { giver: string } = response;
    const { recipient }: { recipient: string } = response;
    const { recipientTeam }: { recipientTeam: string } = response;
    stats.teamToRecipientToScores[recipientTeam] = stats.teamToRecipientToScores[recipientTeam] ?? {};
    stats.teamToRecipientToScores[recipientTeam][recipient] = stats.teamToRecipientToScores[recipientTeam][
      recipient
    ] ?? { responses: [] };
    stats.teamToRecipientToScores[recipientTeam][recipient].responses.push({
      answer: response.responseDetails.answer,
      isSelf: giver === recipient,
    });

    stats.recipientEmails[recipient] = stats.recipientEmails[recipient] ?? response.recipientEmail ?? '';
  }

  for (const team of Object.keys(stats.teamToRecipientToScores)) {
    for (const recipient of Object.keys(stats.teamToRecipientToScores[team])) {
      const recipientStats = stats.teamToRecipientToScores[team][recipient];
      const answersAsArray: number[] = recipientStats.responses.map((resp) => resp.answer);
      recipientStats.max = Math.max(...answersAsArray);
      recipientStats.min = Math.min(...answersAsArray);
      const average: number = answersAsArray.reduce((a: number, b: number) => a + b, 0) / answersAsArray.length;
      recipientStats.average = +average.toFixed(2); // Show integers without dp, truncate fractions to 2dp

      const answersExcludingSelfAsArray: number[] = recipientStats.responses
        .filter((resp: { answer: number; isSelf: boolean }) => !resp.isSelf)
        .map((resp: { answer: number; isSelf: boolean }) => resp.answer);
      if (answersExcludingSelfAsArray.length) {
        const averageExcludingSelf: number =
          answersExcludingSelfAsArray.reduce((a: number, b: number) => a + b, 0) / answersExcludingSelfAsArray.length;
        recipientStats.averageExcludingSelf = +averageExcludingSelf.toFixed(2);
      } else {
        recipientStats.averageExcludingSelf = 0;
      }
    }
  }

  return stats;
}

export function calculateRankOptionsQuestionStatistics(
  question: FeedbackRankOptionsQuestionDetails,
  responses: Response<FeedbackRankOptionsResponseDetails>[],
): RankOptionsQuestionStatistics {
  const stats: RankOptionsQuestionStatistics = {
    ranksReceivedPerOption: {},
    rankPerOption: {},
  };

  const options: string[] = question.options;
  for (const option of options) {
    stats.ranksReceivedPerOption[option] = [];
  }
  for (const response of responses) {
    const answers: number[] = normalizeRanks(response.responseDetails.answers);
    for (let i = 0; i < options.length; i += 1) {
      const option: string = options[i];
      const answer: number = answers[i];
      if (answer === RANK_OPTIONS_ANSWER_NOT_SUBMITTED) {
        // skip option not ranked
        continue;
      }
      stats.ranksReceivedPerOption[option].push(answer);
    }
  }

  const averageRanksReceivedPerOptions: Record<string, number> = {};
  for (const option of Object.keys(stats.ranksReceivedPerOption)) {
    stats.ranksReceivedPerOption[option].sort((a: number, b: number) => a - b);
    const answers: number[] = stats.ranksReceivedPerOption[option];
    const sum: number = answers.reduce((a: number, b: number) => a + b, 0);
    if (answers.length === 0) {
      // skip options which has no answer collected
      continue;
    }
    averageRanksReceivedPerOptions[option] = sum / answers.length;
  }

  const optionsOrderedByRank: string[] = Object.keys(averageRanksReceivedPerOptions).sort((a: string, b: string) => {
    return averageRanksReceivedPerOptions[a] - averageRanksReceivedPerOptions[b];
  });

  for (let i = 0; i < optionsOrderedByRank.length; i += 1) {
    const option: string = optionsOrderedByRank[i];
    if (i === 0) {
      stats.rankPerOption[option] = 1;
      continue;
    }
    const rank: number = averageRanksReceivedPerOptions[option];
    const optionBefore: string = optionsOrderedByRank[i - 1];
    const rankBefore: number = averageRanksReceivedPerOptions[optionBefore];
    if (rank === rankBefore) {
      // If the average rank is the same, the overall rank will be the same
      stats.rankPerOption[option] = stats.rankPerOption[optionBefore];
    } else {
      // Otherwise, the rank is as determined by the order
      stats.rankPerOption[option] = i + 1;
    }
  }

  return stats;
}

function normalizeRanks(ranks: number[]): number[] {
  const rankMapping: Record<number, number> = {};
  rankMapping[RANK_OPTIONS_ANSWER_NOT_SUBMITTED] = RANK_OPTIONS_ANSWER_NOT_SUBMITTED;

  const rankCopy: number[] = structuredClone(ranks);
  rankCopy.sort((a: number, b: number) => a - b);

  let normalizedRank = 1;
  for (const rank of rankCopy) {
    if (!rankMapping[rank]) {
      rankMapping[rank] = normalizedRank;
      normalizedRank += 1;
    }
  }
  return ranks.map((rank: number) => rankMapping[rank]);
}

export function calculateRankRecipientsQuestionStatistics(
  responses: Response<FeedbackRankRecipientsResponseDetails>[],
  recipientType: QuestionRecipientType,
): RankRecipientsQuestionStatistics {
  const stats: RankRecipientsQuestionStatistics = {
    emailToTeamName: {},
    emailToName: {},
    ranksReceivedPerOption: {},
    selfRankPerOption: {},
    rankPerOption: {},
    rankPerOptionExcludeSelf: {},
    rankPerOptionInTeam: {},
    rankPerOptionInTeamExcludeSelf: {},
  };

  const ranksReceivedPerOptionExcludeSelf: Record<string, number[]> = {};

  const isRecipientTeam: boolean =
    recipientType === QuestionRecipientType.TEAMS || recipientType === QuestionRecipientType.TEAMS_EXCLUDING_SELF;

  const isRecipientOwnTeamMember: boolean =
    recipientType === QuestionRecipientType.OWN_TEAM_MEMBERS ||
    recipientType === QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF;

  const teamMembersPerTeam: Record<string, string[]> = {};

  for (const response of responses) {
    const identifier: string = isRecipientTeam ? response.recipient : (response.recipientEmail ?? response.recipient);

    stats.ranksReceivedPerOption[identifier] = stats.ranksReceivedPerOption[identifier] ?? [];
    stats.ranksReceivedPerOption[identifier].push(response.responseDetails.answer);

    if (response.recipient === response.giver) {
      stats.selfRankPerOption[identifier] = response.responseDetails.answer;
    } else {
      ranksReceivedPerOptionExcludeSelf[identifier] = ranksReceivedPerOptionExcludeSelf[identifier] ?? [];
      ranksReceivedPerOptionExcludeSelf[identifier].push(response.responseDetails.answer);
    }

    if (!stats.emailToTeamName[identifier]) {
      stats.emailToTeamName[identifier] = isRecipientTeam ? '' : response.recipientTeam;
    }
    if (!stats.emailToName[identifier]) {
      stats.emailToName[identifier] = response.recipient;
    }

    if (isRecipientOwnTeamMember) {
      teamMembersPerTeam[response.recipientTeam] = teamMembersPerTeam[response.recipientTeam] ?? [];
      if (!teamMembersPerTeam[response.recipientTeam].includes(identifier)) {
        teamMembersPerTeam[response.recipientTeam].push(identifier);
      }
    }
  }

  for (const option of Object.keys(stats.ranksReceivedPerOption)) {
    stats.ranksReceivedPerOption[option].sort((a: number, b: number) => a - b);
  }
  stats.rankPerOption = calculateRankPerOption(stats.ranksReceivedPerOption);
  stats.rankPerOptionExcludeSelf = calculateRankPerOption(ranksReceivedPerOptionExcludeSelf);

  if (isRecipientOwnTeamMember) {
    stats.rankPerOptionInTeam = calculateRankPerOptionInTeam(stats.ranksReceivedPerOption, teamMembersPerTeam);
    stats.rankPerOptionInTeamExcludeSelf = calculateRankPerOptionInTeam(
      ranksReceivedPerOptionExcludeSelf,
      teamMembersPerTeam,
    );
  }

  return stats;
}

function calculateRankPerOption(ranksReceivedPerOption: Record<string, number[]>): Record<string, number> {
  const averageRanksReceivedPerOptions: Record<string, number> = {};
  for (const option of Object.keys(ranksReceivedPerOption)) {
    const answers: number[] = ranksReceivedPerOption[option];
    const sum: number = answers.reduce((a: number, b: number) => a + b, 0);
    if (answers.length === 0) {
      // skip recipient which has no answer collected
      continue;
    }
    averageRanksReceivedPerOptions[option] = answers.length === 0 ? 0 : sum / answers.length;
  }

  const optionsOrderedByRank: string[] = Object.keys(averageRanksReceivedPerOptions).sort((a: string, b: string) => {
    return averageRanksReceivedPerOptions[a] - averageRanksReceivedPerOptions[b];
  });

  const rankPerOption: Record<string, number> = {};

  for (let i = 0; i < optionsOrderedByRank.length; i += 1) {
    const option: string = optionsOrderedByRank[i];
    if (i === 0) {
      rankPerOption[option] = 1;
      continue;
    }
    const rank: number = averageRanksReceivedPerOptions[option];
    const optionBefore: string = optionsOrderedByRank[i - 1];
    const rankBefore: number = averageRanksReceivedPerOptions[optionBefore];
    if (rank === rankBefore) {
      // If the average rank is the same, the overall rank will be the same
      rankPerOption[option] = rankPerOption[optionBefore];
    } else {
      // Otherwise, the rank is as determined by the order
      rankPerOption[option] = i + 1;
    }
  }

  return rankPerOption;
}

function calculateRankPerOptionInTeam(
  ranksReceivedPerOption: Record<string, number[]>,
  teamMembersPerTeam: Record<string, string[]>,
): Record<string, number> {
  const teams: string[] = Object.keys(teamMembersPerTeam);

  return teams
    .map((team: string) => teamMembersPerTeam[team])
    .map((teamMembers: string[]) =>
      teamMembers.reduce((ranksReceivedPerOptionInTeam: Record<string, number[]>, teamMember: string) => {
        ranksReceivedPerOptionInTeam[teamMember] = ranksReceivedPerOption[teamMember] ?? [];
        return ranksReceivedPerOptionInTeam;
      }, {}),
    )
    .map(calculateRankPerOption)
    .reduce((rankPerOptionInTeam: Record<string, number>, rankPerOptionInEachTeam: Record<string, number>) => {
      return { ...rankPerOptionInTeam, ...rankPerOptionInEachTeam };
    }, {});
}

