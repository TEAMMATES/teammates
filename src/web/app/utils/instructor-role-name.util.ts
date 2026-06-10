import { InstructorPermissionRole } from '../../types/api-output';
export function instructorRoleToName(role: InstructorPermissionRole): string {
  switch (role) {
    case InstructorPermissionRole.COOWNER:
      return 'Co-owner';
    case InstructorPermissionRole.MANAGER:
      return 'Manager';
    case InstructorPermissionRole.OBSERVER:
      return 'Observer';
    case InstructorPermissionRole.TUTOR:
      return 'Tutor';
    case InstructorPermissionRole.CUSTOM:
    default:
      return 'Custom';
  }
}
