import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
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
  sections: { id: string; name: string }[];
  privilege: InstructorPermissionSet;

  sessionLevel: InstructorSessionLevelPermission[];
}

/**
 * Instructor session-specific permission of a course.
 */
export interface InstructorSessionLevelPermission {
  sessionId: string;
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
  imports: [FormsModule],
})
export class CustomPrivilegeSettingPanelComponent {
  @Input()
  permission: InstructorOverallPermission = {
    privilege: {
      canModifyCourse: false,
      canModifySession: false,
      canModifyStudent: false,
      canModifyInstructor: false,
      canViewStudent: false,
      canModifySessionComments: false,
      canViewSession: false,
      canSubmitSession: false,
    },
    sectionLevel: [],
  };

  @Output()
  permissionChange: EventEmitter<InstructorOverallPermission> = new EventEmitter();

  @Input()
  allSections: { id: string; name: string }[] = [];

  @Input()
  allSessions: { id: string; name: string }[] = [];

  /**
   * Checks whether there is a section level permission for a given section.
   */
  hasSectionLevelPermission(sectionId: string): boolean {
    return this.permission.sectionLevel.some((sectionLevel: InstructorSectionLevelPermission) =>
      sectionLevel.sections.some((s) => s.id === sectionId),
    );
  }

  /**
   * Checks whether there is section level permission for all sections.
   */
  get hasSectionLevelPermissionForAllSections(): boolean {
    return (
      this.permission.sectionLevel.length >= this.allSections.length ||
      this.permission.sectionLevel
        .map((section: InstructorSectionLevelPermission) => section.sections.length)
        .reduce((prev: number, curr: number) => prev + curr, 0) >= this.allSections.length
    );
  }

  /**
   * Triggers overall permission change.
   */
  triggerOverallPermissionChange(privilegeName: string, shouldEnabled: boolean): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    permission.privilege[privilegeName as keyof InstructorPermissionSet] = shouldEnabled;

    this.permissionChange.emit(permission);
  }

  /**
   * Triggers section level permission change at index.
   */
  triggerSectionLevelPermissionChange(index: number, privilegeName: string, shouldEnabled: boolean): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    permission.sectionLevel[index].privilege[privilegeName as keyof InstructorPermissionSet] = shouldEnabled;

    this.permissionChange.emit(permission);
  }

  /**
   * Triggers session level permission change at index.
   */
  triggerSessionLevelPermissionChange(
    indexSection: number,
    indexSession: number,
    privilegeName: string,
    shouldEnabled: boolean,
  ): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    permission.sectionLevel[indexSection].sessionLevel[indexSession].privilege[
      privilegeName as keyof InstructorPermissionSet
    ] = shouldEnabled;

    this.permissionChange.emit(permission);
  }

  /**
   * Adds section level permission.
   */
  addSectionLevelPermission(): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    permission.sectionLevel.push({
      sections: [],
      privilege: {
        canModifyCourse: false,
        canModifySession: false,
        canModifyStudent: false,
        canModifyInstructor: false,
        canViewStudent: false,
        canModifySessionComments: false,
        canViewSession: false,
        canSubmitSession: false,
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
    permission.sectionLevel[index].sessionLevel = this.allSessions.map((session: { id: string; name: string }) => ({
      sessionId: session.id,
      sessionName: session.name,
      privilege: {
        canModifyCourse: false,
        canModifySession: false,
        canModifyStudent: false,
        canModifyInstructor: false,
        canViewStudent: false,
        canModifySessionComments: false,
        canViewSession: false,
        canSubmitSession: false,
      },
    }));

    this.permissionChange.emit(permission);
  }

  /**
   * Edits section to section level permission at index.
   */
  editSectionToSectionLevelPermission(index: number, sectionId: string, isAdding: boolean): void {
    const permission: InstructorOverallPermission = this.deepCopy(this.permission);
    const section = this.allSections.find((s) => s.id === sectionId);
    if (!section) {
      return;
    }
    const sections = permission.sectionLevel[index].sections;
    if (isAdding) {
      if (!sections.some((s) => s.id === sectionId)) {
        sections.push(section);
      }
    } else {
      permission.sectionLevel[index].sections = sections.filter((s) => s.id !== sectionId);
    }

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
    return structuredClone(obj);
  }
}
