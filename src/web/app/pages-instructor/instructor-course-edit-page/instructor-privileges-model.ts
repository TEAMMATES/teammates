/**
 * The format for instructor course privileges.
 */
export interface CourseLevelPrivileges {
  canmodifycourse: boolean;
  canmodifyinstructor: boolean;
  canmodifysession: boolean;
  canmodifystudent: boolean;
  canviewstudentinsection: boolean;
  canviewsessioninsection: boolean;
  cansubmitsessioninsection: boolean;
  canmodifysessioncommentinsection: boolean;
}

/**
 * The format for instructor section privileges.
 */
export interface SectionLevelPrivileges {
  canviewstudentinsection: boolean;
  canviewsessioninsection: boolean;
  cansubmitsessioninsection: boolean;
  canmodifysessioncommentinsection: boolean;
}

/**
 * The format for instructor session privileges.
 */
export interface SessionLevelPrivileges {
  canviewsessioninsection: boolean;
  cansubmitsessioninsection: boolean;
  canmodifysessioncommentinsection: boolean;
}

/**
 * The format for instructor privileges.
 */
export interface Privileges {
  courseLevel: CourseLevelPrivileges;

  // Maps a section name to section level privileges
  sectionLevel: { [section: string]: SectionLevelPrivileges };

  // Maps a section name to a map mapping the session name and session privileges
  sessionLevel: { [section: string]: { [session: string]: SessionLevelPrivileges } };
}

/**
 * Instructor privileges for default instructor roles.
 */
export interface DefaultPrivileges {
  [role: string]: Privileges;
}
