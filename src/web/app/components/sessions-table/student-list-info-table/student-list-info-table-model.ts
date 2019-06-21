/**
 * The model for a row of the student list info table.
 */
export interface StudentListInfoTableRowModel {
  email: string;
  name: string;
  teamName: string;
  sectionName: string;

  hasSubmittedSession: boolean;

  isSelected: boolean;
}

export interface InstructorListInfoTableRowModel {
  email: string;
  name: string;

  hasSubmittedSession: boolean;

  isSelected: boolean;
}
