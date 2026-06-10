import { CourseSection, FeedbackQuestion, QuestionOutput, ResponseOutput } from '../../../types/api-output';

// NO_SPECIFIC_SECTION_ID and NO_SPECIFIC_SECTION_NAME are used to represent instructors and general recipients not associated with any section.
export const NO_SPECIFIC_SECTION_ID = 'No Specific Section';
export const NO_SPECIFIC_SECTION_NAME = 'Instructors / General Recipients';

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
