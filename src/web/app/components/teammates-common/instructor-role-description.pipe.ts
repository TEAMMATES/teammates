import { Pipe, PipeTransform } from '@angular/core';
import { InstructorPermissionRole } from '../../../types/api-output';

/**
 * Pipe to handle the transformation of an InstructorPermissionRole to a description.
 */
@Pipe({
  name: 'instructorRoleDescription',
})
export class InstructorRoleDescriptionPipe implements PipeTransform {

  /**
   * Transforms InstructorPermissionRole to a description.
   */
  transform(role: InstructorPermissionRole): any {
    switch (role) {
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER:
        return 'Co-owner: Can do everything.';
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_MANAGER:
        return 'Manager: Can do everything except for deleting/restoring the course.';
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_OBSERVER:
        return 'Observer: Can only view information(students, submissions, comments etc.). '
            + 'Cannot edit/delete/submit anything.';
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_TUTOR:
        return 'Tutor: Can view student details, give/view comments, submit/view responses for sessions.';
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM:
      default:
        return 'Custom: No access by default. Any access needs to be granted explicitly.';
    }
  }

}
