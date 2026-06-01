import { instructorRoleToName } from './instructor-role-name.util';
import { InstructorPermissionRole } from '../../types/api-output';

describe('instructorRoleToName', () => {
  it('should return "Co-owner" for COOWNER role', () => {
    expect(instructorRoleToName(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER)).toBe('Co-owner');
  });

  it('should return "Manager" for MANAGER role', () => {
    expect(instructorRoleToName(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_MANAGER)).toBe('Manager');
  });

  it('should return "Observer" for OBSERVER role', () => {
    expect(instructorRoleToName(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_OBSERVER)).toBe('Observer');
  });

  it('should return "Tutor" for TUTOR role', () => {
    expect(instructorRoleToName(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_TUTOR)).toBe('Tutor');
  });

  it('should return "Custom" for CUSTOM role', () => {
    expect(instructorRoleToName(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)).toBe('Custom');
  });

  it('should return "Custom" for unrecognized role', () => {
    expect(instructorRoleToName('UNKNOWN' as InstructorPermissionRole)).toBe('Custom');
  });
});
