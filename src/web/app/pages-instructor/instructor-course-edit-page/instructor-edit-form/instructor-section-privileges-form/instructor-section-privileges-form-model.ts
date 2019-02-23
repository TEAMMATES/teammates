import { SectionLevelPrivileges, SessionLevelPrivileges } from '../../instructor-privileges-model';

/**
 * The form model of an instructor edit section privileges form.
 */
export interface InstructorSectionPrivilegesFormFormModel {
  sections: { [section: string]: boolean };
  sectionLevel: SectionLevelPrivileges;
  instructorSessionPrivilegesFormFormModels: InstructorSessionPrivilegesFormFormModel[];

  isSessionPrivilegesVisible: boolean;
}

/**
 * The form model of an instructor edit session privileges form.
 */
export interface InstructorSessionPrivilegesFormFormModel {
  sessionName: string;
  sessionLevel: SessionLevelPrivileges;
}
