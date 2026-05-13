import {
  ConstsumOptionsQuestionStatistics,
  ConstsumRecipientsQuestionStatistics,
  ContributionQuestionStatistics,
  McqQuestionStatistics,
  MsqQuestionStatistics,
  NumScaleQuestionStatistics,
  RankOptionsQuestionStatistics,
  RankRecipientsQuestionStatistics,
  Response,
  RubricPerRecipientStats,
  RubricQuestionStatistics,
} from '../../types/question-statistics.model';
import {
  ContributionStatistics,
  ContributionStatisticsEntry,
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackParticipantType,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsResponseDetails,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
} from '../../types/api-output';
import {
  CONTRIBUTION_POINT_NOT_SUBMITTED,
  MSQ_ANSWER_NONE_OF_THE_ABOVE,
  NO_VALUE,
  RANK_OPTIONS_ANSWER_NOT_SUBMITTED,
  RUBRIC_ANSWER_NOT_CHOSEN,
} from '../../types/feedback-response-details';

export function calculateConstsumOptionsQuestionStatistics(
  question: FeedbackConstantSumQuestionDetails,
  responses: Response<FeedbackConstantSumResponseDetails>[],
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
  responses: Response<FeedbackConstantSumResponseDetails>[],
  recipientType: FeedbackParticipantType,
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
    recipientType === FeedbackParticipantType.TEAMS || recipientType === FeedbackParticipantType.TEAMS_EXCLUDING_SELF;

  for (const response of responses) {
    const identifier: string = isRecipientTeam ? response.recipient : response.recipientEmail || response.recipient;

    stats.pointsPerOption[identifier] = stats.pointsPerOption[identifier] || [];
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

export function calculateContributionQuestionStatistics(
  responses: Response<FeedbackContributionResponseDetails>[],
  statistics: string,
  isStudent: boolean,
): ContributionQuestionStatistics {
  const stats: ContributionQuestionStatistics = {
    emailToTeamName: {},
    emailToName: {},
    emailToDiff: {},
    questionOverallStatistics: {
      results: {},
    },
    questionStatisticsForStudent: {
      claimed: 0,
      perceived: 0,
      claimedOthers: {},
      claimedOthersValues: [],
      perceivedOthers: [],
    },
  };

  if (!statistics) {
    return stats;
  }

  const statisticsObject: ContributionStatistics = JSON.parse(statistics);
  if (isStudent) {
    const results: ContributionStatisticsEntry[] = Object.values(statisticsObject.results);
    if (results.length) {
      stats.questionStatisticsForStudent = {
        ...results[0],
        claimedOthersValues: Object.values(results[0].claimedOthers).sort((a: number, b: number) => b - a),
      };
    }
  } else {
    for (const response of responses) {
      // the recipient email will always exist for contribution question when viewing by instructors
      if (!response.recipientEmail) {
        continue;
      }

      if (!stats.emailToTeamName[response.recipientEmail]) {
        stats.emailToTeamName[response.recipientEmail] = response.recipientTeam;
      }
      if (!stats.emailToName[response.recipientEmail]) {
        stats.emailToName[response.recipientEmail] = response.recipient;
      }
    }

    stats.questionOverallStatistics = statisticsObject;

    for (const email of Object.keys(stats.emailToName)) {
      const statisticsForEmail: ContributionStatisticsEntry = stats.questionOverallStatistics.results[email];
      const { claimed }: { claimed: number } = statisticsForEmail;
      const { perceived }: { perceived: number } = statisticsForEmail;
      if (claimed < 0 || perceived < 0) {
        stats.emailToDiff[email] = CONTRIBUTION_POINT_NOT_SUBMITTED;
      } else {
        stats.emailToDiff[email] = perceived - claimed;
      }
    }
  }

  return stats;
}

export function calculateMcqQuestionStatistics(
  question: FeedbackMcqQuestionDetails,
  responses: Response<FeedbackMcqResponseDetails>[],
): McqQuestionStatistics {
  const stats: McqQuestionStatistics = {
    answerFrequency: {},
    percentagePerOption: {},
    weightPerOption: {},
    weightedPercentagePerOption: {},
    perRecipientResponses: {},
  };

  for (const answer of question.mcqChoices) {
    stats.answerFrequency[answer] = 0;
  }
  if (question.otherEnabled) {
    stats.answerFrequency['Other'] = 0;
  }
  for (const response of responses) {
    const isOther: boolean = response.responseDetails.isOther;
    const key: string = isOther ? 'Other' : response.responseDetails.answer;
    stats.answerFrequency[key] = (stats.answerFrequency[key] || 0) + 1;
  }

  if (question.hasAssignedWeights) {
    for (let i = 0; i < question.mcqChoices.length; i += 1) {
      const option: string = question.mcqChoices[i];
      const weight: number = question.mcqWeights[i];
      stats.weightPerOption[option] = weight;
    }
    if (question.otherEnabled) {
      stats.weightPerOption['Other'] = question.mcqOtherWeight;
    }

    let totalWeightedResponseCount = 0;
    for (const answer of Object.keys(stats.answerFrequency)) {
      const weight: number = stats.weightPerOption[answer];
      const weightedAnswer: number = weight * stats.answerFrequency[answer];
      totalWeightedResponseCount += weightedAnswer;
    }

    for (const answer of Object.keys(stats.weightPerOption)) {
      const weight: number = stats.weightPerOption[answer];
      const frequency: number = stats.answerFrequency[answer];
      const weightedPercentage: number =
        totalWeightedResponseCount === 0 ? 0 : 100 * ((frequency * weight) / totalWeightedResponseCount);
      stats.weightedPercentagePerOption[answer] = +weightedPercentage.toFixed(2);
    }
  }

  for (const answer of Object.keys(stats.answerFrequency)) {
    if (responses.length === 0) {
      stats.percentagePerOption[answer] = 0;
    } else {
      const percentage: number = (100 * stats.answerFrequency[answer]) / responses.length;
      stats.percentagePerOption[answer] = +percentage.toFixed(2);
    }
  }

  if (question.hasAssignedWeights) {
    const perRecipientResponse: Record<string, Record<string, number>> = {};
    const recipientEmails: Record<string, string> = {};
    const recipientToTeam: Record<string, string> = {};
    for (const response of responses) {
      perRecipientResponse[response.recipient] = perRecipientResponse[response.recipient] || {};
      recipientEmails[response.recipient] = recipientEmails[response.recipient] || response.recipientEmail || '';
      for (const choice of question.mcqChoices) {
        perRecipientResponse[response.recipient][choice] = 0;
      }
      if (question.otherEnabled) {
        perRecipientResponse[response.recipient]['Other'] = 0;
      }
      recipientToTeam[response.recipient] = response.recipientTeam;
    }
    for (const response of responses) {
      const isOther: boolean = response.responseDetails.isOther;
      const answer: string = isOther ? 'Other' : response.responseDetails.answer;
      perRecipientResponse[response.recipient][answer] += 1;
    }

    for (const recipient of Object.keys(perRecipientResponse)) {
      const responses: Record<string, number> = perRecipientResponse[recipient];
      let total = 0;
      let average = 0;
      let numOfResponsesForRecipient = 0;
      for (const answer of Object.keys(responses)) {
        const responseCount: number = responses[answer];
        const weight: number = stats.weightPerOption[answer];
        total += responseCount * weight;
        numOfResponsesForRecipient += responseCount;
      }
      average = numOfResponsesForRecipient ? total / numOfResponsesForRecipient : 0;

      stats.perRecipientResponses[recipient] = {
        recipient,
        recipientEmail: recipientEmails[recipient],
        total: +total.toFixed(5),
        average: +average.toFixed(2),
        recipientTeam: recipientToTeam[recipient],
        responses: perRecipientResponse[recipient],
      };
    }
  }

  return stats;
}

export function calculateMsqQuestionStatistics(
  question: FeedbackMsqQuestionDetails,
  responses: Response<FeedbackMsqResponseDetails>[],
): MsqQuestionStatistics {
  const stats: MsqQuestionStatistics = {
    answerFrequency: {},
    percentagePerOption: {},
    weightPerOption: {},
    weightedPercentagePerOption: {},
    perRecipientResponses: {},
    hasAnswers: false,
  };

  for (const answer of question.msqChoices) {
    stats.answerFrequency[answer] = 0;
  }
  if (question.otherEnabled) {
    stats.answerFrequency['Other'] = 0;
  }
  for (const response of responses) {
    updateResponseCountPerOptionForMsqResponse(question, response.responseDetails, stats.answerFrequency);
  }
  const numOfAnswers: number = Object.values(stats.answerFrequency).reduce(
    (prev: number, curr: number) => prev + curr,
    0,
  );
  stats.hasAnswers = numOfAnswers !== 0;
  if (!stats.hasAnswers) {
    return stats;
  }

  if (question.hasAssignedWeights) {
    for (let i = 0; i < question.msqChoices.length; i += 1) {
      const option: string = question.msqChoices[i];
      const weight: number = question.msqWeights[i];
      stats.weightPerOption[option] = weight;
    }
    if (question.otherEnabled) {
      stats.weightPerOption['Other'] = question.msqOtherWeight;
    }

    let totalWeightedResponseCount = 0;
    for (const answer of Object.keys(stats.answerFrequency)) {
      const weight: number = stats.weightPerOption[answer];
      const weightedAnswer: number = weight * stats.answerFrequency[answer];
      totalWeightedResponseCount += weightedAnswer;
    }

    for (const answer of Object.keys(stats.weightPerOption)) {
      const weight: number = stats.weightPerOption[answer];
      const frequency: number = stats.answerFrequency[answer];
      const weightedPercentage: number =
        totalWeightedResponseCount === 0 ? 0 : 100 * ((frequency * weight) / totalWeightedResponseCount);
      stats.weightedPercentagePerOption[answer] = +weightedPercentage.toFixed(2);
    }
  }

  for (const answer of Object.keys(stats.answerFrequency)) {
    const percentage: number = numOfAnswers ? (100 * stats.answerFrequency[answer]) / numOfAnswers : 0;
    stats.percentagePerOption[answer] = +percentage.toFixed(2);
  }

  // per recipient stats is only available when weights are enabled
  if (!question.hasAssignedWeights) {
    return stats;
  }

  const perRecipientResponse: Record<string, Record<string, number>> = {};
  const recipientToTeam: Record<string, string> = {};
  const recipientEmails: Record<string, string> = {};
  const recipientNames: Record<string, string> = {};
  for (const response of responses) {
    const responseEmail = response.recipientEmail;
    if (!responseEmail) {
      continue;
    }
    perRecipientResponse[responseEmail] = perRecipientResponse[responseEmail] || {};
    recipientEmails[responseEmail] = recipientEmails[responseEmail] || responseEmail || '';
    recipientNames[responseEmail] = recipientNames[responseEmail] || response.recipient || '';
    for (const choice of question.msqChoices) {
      perRecipientResponse[responseEmail][choice] = 0;
    }
    if (question.otherEnabled) {
      perRecipientResponse[responseEmail]['Other'] = 0;
    }
    recipientToTeam[responseEmail] = response.recipientTeam;
  }
  for (const response of responses) {
    const email = response.recipientEmail;
    if (!email) {
      continue;
    }
    updateResponseCountPerOptionForMsqResponse(question, response.responseDetails, perRecipientResponse[email]);
  }

  for (const recipient of Object.keys(perRecipientResponse)) {
    const responses: Record<string, number> = perRecipientResponse[recipient];
    let total = 0;
    let average = 0;
    let numOfResponsesForRecipient = 0;
    for (const answer of Object.keys(responses)) {
      const responseCount: number = responses[answer];
      const weight: number = stats.weightPerOption[answer];
      total += responseCount * weight;
      numOfResponsesForRecipient += responseCount;
    }
    average = numOfResponsesForRecipient ? total / numOfResponsesForRecipient : 0;

    stats.perRecipientResponses[recipient] = {
      recipient: recipientNames[recipient],
      recipientEmail: recipientEmails[recipient],
      total: +total.toFixed(5),
      average: +average.toFixed(2),
      recipientTeam: recipientToTeam[recipient],
      responses: perRecipientResponse[recipient],
    };
  }

  return stats;
}

/**
 * Updates the number of responses per option for each response in responseCountPerOption map.
 */
function updateResponseCountPerOptionForMsqResponse(
  question: FeedbackMsqQuestionDetails,
  responseDetails: FeedbackMsqResponseDetails,
  responseCountPerOption: Record<string, number>,
): void {
  if (responseDetails.isOther) {
    responseCountPerOption['Other'] = (responseCountPerOption['Other'] || 0) + 1;
  }

  for (const answer of responseDetails.answers) {
    if (answer === MSQ_ANSWER_NONE_OF_THE_ABOVE) {
      // ignore 'None of the above' answer
      continue;
    }
    if (!question.msqChoices.includes(answer) && question.generateOptionsFor === FeedbackParticipantType.NONE) {
      // ignore other answer if any
      continue;
    }
    responseCountPerOption[answer] = (responseCountPerOption[answer] || 0) + 1;
  }
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
    stats.teamToRecipientToScores[recipientTeam] = stats.teamToRecipientToScores[recipientTeam] || {};
    stats.teamToRecipientToScores[recipientTeam][recipient] = stats.teamToRecipientToScores[recipientTeam][
      recipient
    ] || { responses: [] };
    stats.teamToRecipientToScores[recipientTeam][recipient].responses.push({
      answer: response.responseDetails.answer,
      isSelf: giver === recipient,
    });

    stats.recipientEmails[recipient] = stats.recipientEmails[recipient] || response.recipientEmail || '';
  }

  for (const team of Object.keys(stats.teamToRecipientToScores)) {
    for (const recipient of Object.keys(stats.teamToRecipientToScores[team])) {
      const recipientStats: any = stats.teamToRecipientToScores[team][recipient];
      const answersAsArray: number[] = recipientStats.responses.map((resp: any) => resp.answer);
      recipientStats.max = Math.max(...answersAsArray);
      recipientStats.min = Math.min(...answersAsArray);
      const average: number = answersAsArray.reduce((a: number, b: number) => a + b, 0) / answersAsArray.length;
      recipientStats.average = +average.toFixed(2); // Show integers without dp, truncate fractions to 2dp

      const answersExcludingSelfAsArray: number[] = recipientStats.responses
        .filter((resp: any) => !resp.isSelf)
        .map((resp: any) => resp.answer);
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

  const rankCopy: number[] = JSON.parse(JSON.stringify(ranks));
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
  recipientType: FeedbackParticipantType,
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
    recipientType === FeedbackParticipantType.TEAMS || recipientType === FeedbackParticipantType.TEAMS_EXCLUDING_SELF;

  const isRecipientOwnTeamMember: boolean =
    recipientType === FeedbackParticipantType.OWN_TEAM_MEMBERS ||
    recipientType === FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;

  const teamMembersPerTeam: Record<string, string[]> = {};

  for (const response of responses) {
    const identifier: string = isRecipientTeam ? response.recipient : response.recipientEmail || response.recipient;

    stats.ranksReceivedPerOption[identifier] = stats.ranksReceivedPerOption[identifier] || [];
    stats.ranksReceivedPerOption[identifier].push(response.responseDetails.answer);

    if (response.recipient === response.giver) {
      stats.selfRankPerOption[identifier] = response.responseDetails.answer;
    } else {
      ranksReceivedPerOptionExcludeSelf[identifier] = ranksReceivedPerOptionExcludeSelf[identifier] || [];
      ranksReceivedPerOptionExcludeSelf[identifier].push(response.responseDetails.answer);
    }

    if (!stats.emailToTeamName[identifier]) {
      stats.emailToTeamName[identifier] = isRecipientTeam ? '' : response.recipientTeam;
    }
    if (!stats.emailToName[identifier]) {
      stats.emailToName[identifier] = response.recipient;
    }

    if (isRecipientOwnTeamMember) {
      teamMembersPerTeam[response.recipientTeam] = teamMembersPerTeam[response.recipientTeam] || [];
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
        ranksReceivedPerOptionInTeam[teamMember] = ranksReceivedPerOption[teamMember] || [];
        return ranksReceivedPerOptionInTeam;
      }, {}),
    )
    .map(calculateRankPerOption)
    .reduce((rankPerOptionInTeam: Record<string, number>, rankPerOptionInEachTeam: Record<string, number>) => {
      return { ...rankPerOptionInTeam, ...rankPerOptionInEachTeam };
    }, {});
}

export function calculateRubricQuestionStatistics(
  question: FeedbackRubricQuestionDetails,
  responses: Response<FeedbackRubricResponseDetails>[],
  isStudent: boolean,
): RubricQuestionStatistics {
  const stats: RubricQuestionStatistics = {
    subQuestions: question.rubricSubQuestions,
    choices: question.rubricChoices,
    hasWeights: question.hasAssignedWeights,
    weights: question.rubricWeightsForEachCell,
    answers: [],
    isWeightStatsVisible: false,
    percentages: [],
    subQuestionWeightAverage: [],
    answersExcludeSelf: [],
    percentagesExcludeSelf: [],
    subQuestionWeightAverageExcludeSelf: [],
    perRecipientStatsMap: {},
  };
  stats.isWeightStatsVisible = stats.hasWeights && stats.weights.length > 0 && stats.weights[0].length > 0;

  const emptyAnswers: number[][] = [];
  for (let i = 0; i < question.rubricSubQuestions.length; i += 1) {
    const subQuestionAnswers: number[] = [];
    for (let j = 0; j < question.rubricChoices.length; j += 1) {
      subQuestionAnswers.push(0);
    }
    emptyAnswers.push(subQuestionAnswers);
  }
  stats.answers = JSON.parse(JSON.stringify(emptyAnswers));
  stats.answersExcludeSelf = JSON.parse(JSON.stringify(emptyAnswers));

  for (const response of responses) {
    for (let i = 0; i < response.responseDetails.answer.length; i += 1) {
      const subAnswer: number = response.responseDetails.answer[i];
      if (subAnswer === RUBRIC_ANSWER_NOT_CHOSEN || (isStudent && response.recipient !== 'You')) {
        continue;
      }
      stats.answers[i][subAnswer] += 1;

      if (response.recipient !== response.giver) {
        stats.answersExcludeSelf[i][subAnswer] += 1;
      }
    }
  }

  stats.percentages = calculatePercentages(stats.answers);
  stats.percentagesExcludeSelf = calculatePercentages(stats.answersExcludeSelf);

  // only apply weights average if applicable
  if (!stats.isWeightStatsVisible) {
    return stats;
  }

  stats.subQuestionWeightAverage = calculateSubQuestionWeightAverage(stats, stats.answers);
  stats.subQuestionWeightAverageExcludeSelf = calculateSubQuestionWeightAverage(stats, stats.answersExcludeSelf);

  // calculate per recipient stats
  for (const response of responses) {
    stats.perRecipientStatsMap[response.recipientEmail || response.recipient] = stats.perRecipientStatsMap[
      response.recipientEmail || response.recipient
    ] || {
      recipientName: response.recipient,
      recipientEmail: response.recipientEmail,
      recipientTeam: response.recipientTeam,
      answers: JSON.parse(JSON.stringify(emptyAnswers)),
      answersSum: [],
      percentages: [],
      percentagesAverage: [],
      weightsAverage: [],
      areSubQuestionChosenWeightsAllNull: stats.subQuestions.map(() => true),
      subQuestionTotalChosenWeight: stats.subQuestions.map(() => 0),
      subQuestionWeightAverage: [],
    };
    for (let i = 0; i < response.responseDetails.answer.length; i += 1) {
      const subAnswer: number = response.responseDetails.answer[i];
      if (subAnswer === RUBRIC_ANSWER_NOT_CHOSEN) {
        continue;
      }
      stats.perRecipientStatsMap[response.recipientEmail || response.recipient].answers[i][subAnswer] += 1;
      if (stats.weights[i][subAnswer] !== null) {
        stats.perRecipientStatsMap[response.recipientEmail || response.recipient].subQuestionTotalChosenWeight[i] +=
          +stats.weights[i][subAnswer].toFixed(5);
        stats.perRecipientStatsMap[response.recipientEmail || response.recipient].areSubQuestionChosenWeightsAllNull[
          i
        ] = false;
      }
    }
  }

  for (const recipient of Object.keys(stats.perRecipientStatsMap)) {
    const perRecipientStats: RubricPerRecipientStats = stats.perRecipientStatsMap[recipient];

    // Answers sum = number of answers in each column
    perRecipientStats.answersSum = sumValidValuesByColumn(perRecipientStats.answers);
    perRecipientStats.percentages = calculatePercentages(perRecipientStats.answers);
    perRecipientStats.percentagesAverage = calculatePercentagesAverage(perRecipientStats.answersSum);
    perRecipientStats.subQuestionTotalChosenWeight = perRecipientStats.subQuestionTotalChosenWeight.map(
      (val: number, i: number) => (perRecipientStats.areSubQuestionChosenWeightsAllNull[i] ? NO_VALUE : val),
    );
    perRecipientStats.subQuestionWeightAverage = calculateSubQuestionWeightAverage(stats, perRecipientStats.answers);
    perRecipientStats.weightsAverage = calculateWeightsAverage(stats.weights);
    perRecipientStats.overallWeightedSum = calculateOverallWeightedSum(
      perRecipientStats.areSubQuestionChosenWeightsAllNull,
      perRecipientStats.subQuestionTotalChosenWeight,
    );
    // Overall weighted average = overall weighted sum / total number of responses with non-null weights
    perRecipientStats.overallWeightAverage =
      perRecipientStats.overallWeightedSum === NO_VALUE
        ? NO_VALUE
        : +(
            perRecipientStats.overallWeightedSum /
            calculateNumResponses(countResponsesByRowWithValidWeight(stats, perRecipientStats.answers))
          ).toFixed(2);
  }

  return stats;
}

// Number of responses for each sub question with non-null weights
function countResponsesByRowWithValidWeight(stats: RubricQuestionStatistics, answers: number[][]): number[] {
  const sums: number[] = [];
  for (let r = 0; r < answers.length; r += 1) {
    let sum = 0;
    for (let c = 0; c < answers[0].length; c += 1) {
      if (stats.weights[r][c] === null) {
        continue;
      }
      sum += answers[r][c];
    }
    sums[r] = sum;
  }
  return sums;
}

function calculateSubQuestionWeightAverage(stats: RubricQuestionStatistics, answers: number[][]): number[] {
  const sums: number[] = countResponsesByRowWithValidWeight(stats, answers);

  return answers.map((subQuestionAnswer: number[], subQuestionIdx: number): number => {
    if (sums[subQuestionIdx] === 0) {
      return NO_VALUE;
    }
    const weightAverage: number =
      subQuestionAnswer.reduce(
        (prevValue: number, currValue: number, currentIndex: number): number =>
          stats.weights[subQuestionIdx][currentIndex] === null
            ? prevValue
            : prevValue + currValue * stats.weights[subQuestionIdx][currentIndex],
        0,
      ) / sums[subQuestionIdx];
    return +weightAverage.toFixed(2);
  });
}

function calculatePercentages(answers: number[][]): number[][] {
  // Deep-copy the answers
  const percentages: number[][] = JSON.parse(JSON.stringify(answers));

  // Calculate sums for each row
  const sums: number[] = percentages.map((weightedAnswers: number[]) =>
    weightedAnswers.reduce((a: number, b: number) => a + b, 0),
  );

  // Calculate the percentages based on the entry of each cell and the sum of each row
  for (let i = 0; i < answers.length; i += 1) {
    for (let j = 0; j < answers[i].length; j += 1) {
      percentages[i][j] = sums[i] === 0 ? 0 : +((percentages[i][j] / sums[i]) * 100).toFixed(2);
    }
  }

  return percentages;
}

// Calculate sum of non-null values for each column
function sumValidValuesByColumn(matrix: number[][]): number[] {
  const sums: number[] = [];
  for (let c = 0; c < matrix[0].length; c += 1) {
    let sum = 0;
    for (let r = 0; r < matrix.length; r += 1) {
      sum += matrix[r][c] === null ? 0 : matrix[r][c];
    }
    sums[c] = sum;
  }
  return sums;
}

// Count number of non-null values for each column
function countValidValuesByColumn(matrix: number[][]): number[] {
  const counts: number[] = [];
  for (let c = 0; c < matrix[0].length; c += 1) {
    let count = 0;
    for (let r = 0; r < matrix.length; r += 1) {
      count += matrix[r][c] === null ? 0 : 1;
    }
    counts[c] = count;
  }
  return counts;
}

// Calculate non-null weight average for each column
function calculateWeightsAverage(weights: number[][]): number[] {
  const sums: number[] = sumValidValuesByColumn(weights);
  const counts: number[] = countValidValuesByColumn(weights);
  const averages: number[] = [];
  // Divide each weight sum by number of non-null weights
  for (let i = 0; i < sums.length; i += 1) {
    averages[i] = counts[i] ? +(sums[i] / counts[i]).toFixed(2) : NO_VALUE;
  }
  return averages;
}

// Calculate percentage average for each column
function calculatePercentagesAverage(answersSum: number[]): number[] {
  // Calculate total number of responses
  const numResponses = calculateNumResponses(answersSum);
  const averages: number[] = [];
  // Divide each column sum by total number of responses, then convert to percentage
  for (let i = 0; i < answersSum.length; i += 1) {
    averages[i] = numResponses === 0 ? 0 : +((answersSum[i] * 100) / numResponses).toFixed(2);
  }
  return averages;
}

// Calculate total number of responses
function calculateNumResponses(answersSum: number[]): number {
  return answersSum.reduce((a, b) => a + b);
}

// Overall weighted sum is sum of total chosen non-null weight for all sub questions
function calculateOverallWeightedSum(areChosenWeightsAllNull: boolean[], totalChosenWeights: number[]): number {
  if (areChosenWeightsAllNull.every(Boolean)) {
    return NO_VALUE;
  }
  let sum = 0;
  for (const totalChosenWeight of totalChosenWeights) {
    sum += totalChosenWeight === NO_VALUE ? 0 : totalChosenWeight;
  }
  return +sum.toFixed(2);
}
