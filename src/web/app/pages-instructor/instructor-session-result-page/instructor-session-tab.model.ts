import { FeedbackQuestion, QuestionOutput, ResponseOutput } from '../../../types/api-output';

/**
 * Per section view tab model.
 */
export interface SectionTabModel {
  questions: QuestionOutput[];
  hasPopulated: boolean;
  errorMessage?: string;
  isTabExpanded: boolean;
}

/**
 * Per question view tab model.
 */
export interface QuestionTabModel {
  question: FeedbackQuestion;
  responses: ResponseOutput[];
  statistics: string; // TODO will define types later
  hasPopulated: boolean;
  errorMessage?: string;
  isTabExpanded: boolean;
}
