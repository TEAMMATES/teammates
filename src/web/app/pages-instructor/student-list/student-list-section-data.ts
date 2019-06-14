/**
 * Contains a list of students in a section.
 */
export interface StudentListSectionData {
  sectionName: string;
  isAllowedToViewStudentInSection: boolean;
  isAllowedToModifyStudent: boolean;
  students: StudentListStudentData[];
}

/**
 * Contains details about a student to be displayed in the list.
 */
export interface StudentListStudentData {
  name: string;
  email: string;
  status: string;
  team: string;
  photoUrl?: string;
}

/**
 * Flattened data which contains details about a student and their section.
 * The data is flattened to allow sorting of the table.
 */
export interface FlatStudentListData {
  name: string;
  email: string;
  status: string;
  team: string;
  photoUrl?: string;
  sectionName: string;
  isAllowedToViewStudentInSection: boolean;
  isAllowedToModifyStudent: boolean;
}

/**
 * Sort criteria for the students table.
 */
export enum SortBy {
  /**
   * Nothing.
   */
  NONE,

  /**
   * Section Name.
   */
  SECTION_NAME,

  /**
   * Team name.
   */
  TEAM_NAME,

  /**
   * Student Name.
   */
  STUDENT_NAME,

  /**
   * Status.
   */
  STATUS,

  /**
   * Email.
   */
  EMAIL,
}

/**
 * Sort order for the students table.
 */
export enum SortOrder {
  /**
   * Descending sort order.
   */
  DESC,

  /**
   * Ascending sort order
   */
  ASC,
}
