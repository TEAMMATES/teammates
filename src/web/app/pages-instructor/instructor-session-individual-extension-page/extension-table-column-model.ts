import { InstructorPermissionRole } from '../../../types/api-output';

export interface StudentExtensionTableColumnModel {
  userId: string;
  sectionName: string;
  teamName: string;
  name: string;
  email: string;
  extensionDeadline: number;
  hasExtension: boolean;
  isSelected: boolean;
  hasSubmittedSession?: boolean;
}

export interface InstructorExtensionTableColumnModel {
  userId: string;
  name: string;
  email: string;
  role?: InstructorPermissionRole;
  extensionDeadline: number;
  hasExtension: boolean;
  isSelected: boolean;
  hasSubmittedSession?: boolean;
}
