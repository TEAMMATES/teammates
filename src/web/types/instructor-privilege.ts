import { InstructorPrivilege } from './api-output';

/**
 * The default instructor privilege.
 */
export const DEFAULT_INSTRUCTOR_PRIVILEGE: InstructorPrivilege = {
  canModifyCourse: true,
  canModifySession: true,
  canModifyStudent: true,
  canSubmitSessionInSections: true,
  canModifyInstructor: true,
  canViewStudentInSections: true,
  canModifySessionCommentsInSections: true,
  canViewSessionInSections: true,
};
