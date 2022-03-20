export interface StudentExtensionTableColumnModel {
  sectionName: string;
  teamName: string;
  name: string;
  email: string;
  extensionDeadline: number;
  hasExtension: boolean;
  isSelected: boolean;
}

export interface InstructorExtensionTableColumnModel {
  institute?: string;
  name: string;
  email: string;
  extensionDeadline: number;
  hasExtension: boolean;
  isSelected: boolean;
}
