export interface StudentExtensionTableColumnModel {
    sectionName: string;
    teamName: string;
    name: string;
    email: string;
    extensionDeadline: number;
    hasExtension: boolean;
    selected: boolean;
  }

  export interface InstructorExtensionTableColumnModel {
    institute?: string;
    name: string;
    email: string;
    extensionDeadline: number;
    hasExtension: boolean;
    selected: boolean;
  }
