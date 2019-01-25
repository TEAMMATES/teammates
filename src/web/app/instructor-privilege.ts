/**
 * Instructor privilege.
 */
export interface InstructorPrivilege {
  canModifyCourse: boolean;
  canModifySession: boolean;
  canModifyStudent: boolean;
  canSubmitSessionInSections: boolean;
}

/**
 * The default instructor privilege.
 */
export const defaultInstructorPrivilege: InstructorPrivilege = {
  canModifyCourse: true,
  canModifySession: true,
  canModifyStudent: true,
  canSubmitSessionInSections: true,
};
