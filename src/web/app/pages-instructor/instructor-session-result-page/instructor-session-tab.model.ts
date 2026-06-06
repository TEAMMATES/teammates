import { CourseSection, FeedbackQuestion, QuestionOutput, ResponseOutput } from '../../../types/api-output';

export const DEFAULT_SECTION_ID = 'None';
export const DEFAULT_SECTION_NAME = 'None';

/**
 * Per section view tab model.
 */
export interface SectionTabModel {
  section: CourseSection;
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
