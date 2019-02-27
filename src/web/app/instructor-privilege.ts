/**
 * Instructor privilege.
 */
export interface InstructorPrivilege {
  canModifyCourse: boolean;
  canModifySession: boolean;
  canModifyStudent: boolean;
  canSubmitSessionInSections: boolean;
}

/**
 * The default instructor privilege.
 */
export const defaultInstructorPrivilege: InstructorPrivilege = {
  canModifyCourse: true,
  canModifySession: true,
  canModifyStudent: true,
  canSubmitSessionInSections: true,
};

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
  [key: string]: boolean;
}

/**
 * The format for instructor section privileges.
 */
export interface SectionLevelPrivileges {
  canviewstudentinsection: boolean;
  canviewsessioninsection: boolean;
  cansubmitsessioninsection: boolean;
  canmodifysessioncommentinsection: boolean;
  [key: string]: boolean;
}

/**
 * The format for instructor session privileges.
 */
export interface SessionLevelPrivileges {
  canviewsessioninsection: boolean;
  cansubmitsessioninsection: boolean;
  canmodifysessioncommentinsection: boolean;
  [key: string]: boolean;
}

/**
 * Possible instructor roles.
 */
export enum Role {
  /**
   * Co-owner instructor role.
   */
  COOWNER = 'Co-owner',

  /**
   * Manager instructor role.
   */
  MANAGER = 'Manager',

  /**
   * Observer instructor role.
   */
  OBSERVER = 'Observer',

  /**
   * Tutor instructor role.
   */
  TUTOR = 'Tutor',

  /**
   * Custom instructor role.
   */
  CUSTOM = 'Custom',
}

/**
 * Instructor privileges for default instructor roles.
 */
export class DefaultPrivileges {
  /**
   * Instructor privileges for co-owner role.
   */
  static COOWNER: DefaultPrivileges = new DefaultPrivileges(Role.COOWNER, {
    courseLevel: {
      canmodifycourse: true,
      canmodifyinstructor: true,
      canmodifysession: true,
      canmodifystudent: true,
      canviewstudentinsection: true,
      canviewsessioninsection: true,
      cansubmitsessioninsection: true,
      canmodifysessioncommentinsection: true,
    },
    sectionLevel: {},
    sessionLevel: {},
  });

  /**
   * Instructor privileges for manager role.
   */
  static MANAGER: DefaultPrivileges = new DefaultPrivileges(Role.MANAGER, {
    courseLevel: {
      canmodifycourse: false,
      canmodifyinstructor: true,
      canmodifysession: true,
      canmodifystudent: true,
      canviewstudentinsection: true,
      canviewsessioninsection: true,
      cansubmitsessioninsection: true,
      canmodifysessioncommentinsection: true,
    },
    sectionLevel: {},
    sessionLevel: {},
  });

  /**
   * Instructor privileges for observer role.
   */
  static OBSERVER: DefaultPrivileges = new DefaultPrivileges(Role.OBSERVER, {
    courseLevel: {
      canmodifycourse: false,
      canmodifyinstructor: false,
      canmodifysession: false,
      canmodifystudent: false,
      canviewstudentinsection: true,
      canviewsessioninsection: true,
      cansubmitsessioninsection: false,
      canmodifysessioncommentinsection: false,
    },
    sectionLevel: {},
    sessionLevel: {},
  });

  /**
   * Instructor privileges for tutor role.
   */
  static TUTOR: DefaultPrivileges = new DefaultPrivileges(Role.TUTOR, {
    courseLevel: {
      canmodifycourse: false,
      canmodifyinstructor: false,
      canmodifysession: false,
      canmodifystudent: false,
      canviewstudentinsection: true,
      canviewsessioninsection: true,
      cansubmitsessioninsection: true,
      canmodifysessioncommentinsection: false,
    },
    sectionLevel: {},
    sessionLevel: {},
  });

  /**
   * Instructor privileges for custom role.
   */
  static CUSTOM: DefaultPrivileges = new DefaultPrivileges(Role.CUSTOM, {
    courseLevel: {
      canmodifycourse: false,
      canmodifyinstructor: false,
      canmodifysession: false,
      canmodifystudent: false,
      canviewstudentinsection: false,
      canviewsessioninsection: false,
      cansubmitsessioninsection: false,
      canmodifysessioncommentinsection: false,
    },
    sectionLevel: {},
    sessionLevel: {},
  });

  /**
   * Constructs an instance of a default privilege.
   */
  constructor(private key: string, readonly value: Privileges) {
  }

  /**
   * Returns the 'enum' name.
   */
  toString(): string {
    return this.key;
  }
}

/**
 * The default instructor section level privileges
 */
export const defaultSectionLevelPrivileges: SectionLevelPrivileges = {
  canviewstudentinsection: true,
  canviewsessioninsection: true,
  cansubmitsessioninsection: true,
  canmodifysessioncommentinsection: true,
};
