import { Pipe, PipeTransform } from '@angular/core';
import { InstructorPermissionRole } from '../../../types/api-output';

/**
 * Pipe to handle the transformation of an InstructorPermissionRole to a name.
 */
@Pipe({
  name: 'instructorRoleName',
})
export class InstructorRoleNamePipe implements PipeTransform {

  /**
   * Transforms InstructorPermissionRole to a name.
   */
  transform(role: InstructorPermissionRole): any {
    switch (role) {
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER:
        return 'Co-owner';
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_MANAGER:
        return 'Manager';
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_OBSERVER:
        return 'Observer';
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_TUTOR:
        return 'Tutor';
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM:
      default:
        return 'Custom';
    }
  }

}
