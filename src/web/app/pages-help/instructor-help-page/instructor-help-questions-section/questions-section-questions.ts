/**
 * Unique identifiers for each question in the questions section of instructor help page
 */
export enum QuestionsSectionQuestions {
  /**
   * Essay Questions
   */
  ESSAY = 'essay-questions',

  /**
   * Multiple Choice (Single Answer) Questions
   */
  SINGLE_ANSWER_MCQ = 'single-answer-mcq',

  /**
   * Multiple Choice (Multiple Answers) Questions
   */
  MULTIPLE_ANSWER_MCQ = 'multiple-answer-mcq',

  /**
   * Numerical Scale Questions
   */
  NUMERICAL_SCALE = 'numerical-scale',

  /**
   * Distribute Points (Among Options) Questions
   */
  POINTS_OPTIONS = 'points-options',

  /**
   * Distribute Points (Among Recipients) Questions
   */
  POINTS_RECIPIENTS = 'points-recipients',

  /**
   * Team Contribution Questions
   */
  CONTRIBUTION = 'contribution',

  /**
   * Rubric Questions
   */
  RUBRIC = 'rubric',

  /**
   * Rank (Options) Questions
   */
  RANK_OPTIONS = 'rank-options',

  /**
   * Rank (Recipients) Questions
   */
  RANK_RECIPIENTS = 'rank-recipients',
}
