import { Component, EventEmitter, Input, Output } from '@angular/core';
import { InstructorPermissionSet } from '../../../../types/api-output';

/**
 * Instructor overall permission of a course.
 */
export interface InstructorOverallPermission {
  privilege: InstructorPermissionSet;
  sectionLevel: InstructorSectionLevelPermission[];
}

/**
 * Instructor section-specific permission of a course.
 */
export interface InstructorSectionLevelPermission {
  sectionNames: string[];
  privilege: InstructorPermissionSet;

  sessionLevel: InstructorSessionLevelPermission[];
}

/**
 * Instructor session-specific permission of a course.
 */
export interface InstructorSessionLevelPermission {
  sessionName: string;
  privilege: InstructorPermissionSet;
}

/**
 * Panel for custom privilege of an instructor.
 */
@Component({
  selector: 'tm-custom-privilege-setting-panel',
  templateUrl: './custom-privilege-setting-panel.component.html',
  styleUrls: ['./custom-privilege-setting-panel.component.scss'],
})
export class CustomPrivilegeSettingPanelComponent {

  @Input()
  permission: InstructorOverallPermission = {
    privilege: {
      canModifyCourse: false,
      canModifySession: false,
      canModifyStudent: false,
      canModifyInstructor: false,
      canViewStudentInSections: false,
      canModifySessionCommentsInSections: false,
      canViewSessionInSections: false,
      canSubmitSessionInSections: false,
    },
    sectionLevel: [],
  };

  @Output()
  permissionChange: EventEmitter<InstructorOverallPermission> = new EventEmitter();

  @Input()
  allSections: string[] = [];

  @Input()
  allSessions: string[] = [];

  /**
   * Checks whether there is a section level permission for a give section.
   */
  hasSectionLevelPermission(sectionName: string): boolean {
    return this.permission.sectionLevel.some(
        (sectionLevel: InstructorSectionLevelPermission) => sectionLevel.sectionNames.includes(sectionName));
  }

  /**
   * Checks whether there is section level permission for all sections.
   */
  get hasSectionLevelPermissionForAllSections(): boolean {
    return this.permission.sectionLevel.length >= this.allSections.length
        || this.permission.sectionLevel
            .map((section: InstructorSectionLevelPermission) => section.sectionNames.length)
            .reduce((prev: number, curr: number) => prev + curr, 0) >= this.allSections.length;
  }

  /**
   * Triggers overall permission change.
   */
  triggerOverallPermissionChange(privilegeName: string, shouldEnabled: boolean): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    (permission.privilege as any)[privilegeName] = shouldEnabled;

    this.permissionChange.emit(permission);
  }

  /**
   * Triggers section level permission change at index.
   */
  triggerSectionLevelPermissionChange(index: number, privilegeName: string, shouldEnabled: boolean): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    (permission.sectionLevel[index].privilege as any)[privilegeName] = shouldEnabled;

    this.permissionChange.emit(permission);
  }

  /**
   * Triggers session level permission change at index.
   */
  triggerSessionLevelPermissionChange(
      indexSection: number, indexSession: number, privilegeName: string, shouldEnabled: boolean): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    (permission.sectionLevel[indexSection].sessionLevel[indexSession].privilege as any)[privilegeName] = shouldEnabled;

    this.permissionChange.emit(permission);
  }

  /**
   * Adds section level permission.
   */
  addSectionLevelPermission(): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    permission.sectionLevel.push({
      sectionNames: [],
      privilege: {
        canModifyCourse: false,
        canModifySession: false,
        canModifyStudent: false,
        canModifyInstructor: false,
        canViewStudentInSections: false,
        canModifySessionCommentsInSections: false,
        canViewSessionInSections: false,
        canSubmitSessionInSections: false,
      },

      sessionLevel: [],
    });

    this.permissionChange.emit(permission);
  }

  /**
   * Adds session level permission at section level permission.
   */
  addSessionLevelPermission(index: number): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    permission.sectionLevel[index].sessionLevel = this.allSessions.map((name: string) => ({
      sessionName: name,
      privilege: {
        canModifyCourse: false,
        canModifySession: false,
        canModifyStudent: false,
        canModifyInstructor: false,
        canViewStudentInSections: false,
        canModifySessionCommentsInSections: false,
        canViewSessionInSections: false,
        canSubmitSessionInSections: false,
      },
    }));

    this.permissionChange.emit(permission);
  }

  /**
   * Edits section to section level permission at index.
   */
  editSectionToSectionLevelPermission(index: number, sectionName: string, isAdding: boolean): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    const sectionNames: Set<string> = new Set(permission.sectionLevel[index].sectionNames);
    if (isAdding) {
      sectionNames.add(sectionName);
    } else {
      sectionNames.delete(sectionName);
    }
    permission.sectionLevel[index].sectionNames = Array.from(sectionNames);

    this.permissionChange.emit(permission);
  }

  /**
   * Deletes all session level permission at section level permission.
   */
  deleteSessionLevelPermission(index: number): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    permission.sectionLevel[index].sessionLevel = [];

    this.permissionChange.emit(permission);
  }

  /**
   * Removes section level permission at index
   */
  removeSectionLevelPermission(index: number): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    permission.sectionLevel.splice(index, 1);

    this.permissionChange.emit(permission);
  }

  private deepCopy<T>(obj: T): T {
    return JSON.parse(JSON.stringify(obj));
  }

}
