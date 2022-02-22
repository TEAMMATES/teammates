import { DefaultInstructorPermissions } from './api-const';
import { InstructorPermissionSet } from './api-output';

/**
 * Structure for default co-owner privileges.
 */
export const DEFAULT_PRIVILEGE_COOWNER: () => InstructorPermissionSet =
    (): InstructorPermissionSet => {
      return JSON.parse(DefaultInstructorPermissions.COOWNER);
    };

/**
 * Structure for default manager privileges.
 */
export const DEFAULT_PRIVILEGE_MANAGER: () => InstructorPermissionSet =
    (): InstructorPermissionSet => {
      return JSON.parse(DefaultInstructorPermissions.MANAGER);
    };

/**
 * Structure for default observer privileges.
 */
export const DEFAULT_PRIVILEGE_OBSERVER: () => InstructorPermissionSet =
    (): InstructorPermissionSet => {
      return JSON.parse(DefaultInstructorPermissions.OBSERVER);
    };

/**
 * Structure for default tutor privileges.
 */
export const DEFAULT_PRIVILEGE_TUTOR: () => InstructorPermissionSet =
    (): InstructorPermissionSet => {
      return JSON.parse(DefaultInstructorPermissions.TUTOR);
    };

/**
 * The default instructor privilege.
 */
export const DEFAULT_INSTRUCTOR_PRIVILEGE: () => InstructorPermissionSet =
    (): InstructorPermissionSet => {
      return JSON.parse(DefaultInstructorPermissions.CUSTOM);
    };
