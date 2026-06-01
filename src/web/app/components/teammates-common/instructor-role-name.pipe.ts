import { Pipe, PipeTransform } from '@angular/core';
import { InstructorPermissionRole } from '../../../types/api-output';
import { instructorRoleToName } from '../../utils/instructor-role-name.util';

/**
 * Pipe to handle the transformation of an InstructorPermissionRole to a name.
 */
@Pipe({ name: 'instructorRoleName' })
export class InstructorRoleNamePipe implements PipeTransform {
  /**
   * Transforms InstructorPermissionRole to a name.
   */
  transform(role: InstructorPermissionRole): any {
    return instructorRoleToName(role);
  }
}
