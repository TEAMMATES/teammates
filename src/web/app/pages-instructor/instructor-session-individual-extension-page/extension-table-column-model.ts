import { InstructorPermissionRole } from '../../../types/api-request';

export interface StudentExtensionTableColumnModel {
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
  name: string;
  email: string;
  role?: InstructorPermissionRole;
  extensionDeadline: number;
  hasExtension: boolean;
  isSelected: boolean;
  hasSubmittedSession?: boolean;
}
