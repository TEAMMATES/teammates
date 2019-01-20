/**
 * Instructor privilege.
 */
export interface InstructorPrivilege {
  canModifySession: boolean;
  canSubmitSessionInSections: boolean;
}

/**
 * The default instructor privilege.
 */
export const defaultInstructorPrivilege: InstructorPrivilege = {
  canModifySession: true,
  canSubmitSessionInSections: true,
};
