import { Pipe, PipeTransform } from '@angular/core';
import { InstructorPermissionRole } from '../../../types/api-output';

/**
 * Pipe to handle the transformation of an InstructorPermissionRole to a description.
 */
@Pipe({ name: 'instructorRoleDescription' })
export class InstructorRoleDescriptionPipe implements PipeTransform {
  /**
   * Transforms InstructorPermissionRole to a description.
   */
  transform(role: InstructorPermissionRole): string {
    switch (role) {
      case InstructorPermissionRole.COOWNER:
        return 'Co-owner: Can do everything.';
      case InstructorPermissionRole.MANAGER:
        return 'Manager: Can do everything except for deleting/restoring the course.';
      case InstructorPermissionRole.OBSERVER:
        return (
          'Observer: Can only view information(students, submissions, comments etc.). ' +
          'Cannot edit/delete/submit anything.'
        );
      case InstructorPermissionRole.TUTOR:
        return 'Tutor: Can view student details, give/view comments, submit/view responses for sessions.';
      case InstructorPermissionRole.CUSTOM:
      default:
        return 'Custom: No access by default. Any access needs to be granted explicitly.';
    }
  }
}
