import {
  InstructorExtensionTableColumnModel,
  StudentExtensionTableColumnModel,
} from '../app/pages-instructor/instructor-session-individual-extension-page/extension-table-column-model';
import { Instructor, Student } from '../types/api-output';

export enum DeadlineHandlerType {
  CREATE,
  DELETE,
}

/**
 * Deadline Extension utility functions.
 */
export class DeadlineExtensionHelper {
  public static hasOngoingExtension(deadlines: {
    studentDeadlines: Record<string, number>,
    instructorDeadlines: Record<string, number>,
  }): boolean {
    const timeNow = Date.now();
    return Object.values(deadlines.studentDeadlines).some((deadlineTimestamp) => deadlineTimestamp > timeNow)
      || Object.values(deadlines.instructorDeadlines).some((deadlineTimestamp) => deadlineTimestamp > timeNow);
  }

  public static hasDeadlinesBeforeUpdatedEndTime(
    studentDeadlines: Record<string, number>,
    instructorDeadlines: Record<string, number>,
    newSubmissionEndTime: number,
  ): boolean {
    const allDeadlines: Record<string, number> = { ...studentDeadlines, ...instructorDeadlines };
    return Object.values(allDeadlines).some((deadlineTimestamp) => deadlineTimestamp < newSubmissionEndTime);
  }

  public static setDeadlinesBeforeEndTime(
    deadlines: Record<string, number>,
    submissionEndTime: number,
  ): Record<string, number> {
    const deadlinesToSet: Record<string, number> = {};
    for (const [emailOfIndividual, deadlineOfIndividual] of Object.entries(deadlines)) {
      if (deadlineOfIndividual < submissionEndTime) {
        deadlinesToSet[emailOfIndividual] = deadlineOfIndividual;
      }
    }
    return deadlinesToSet;
  }

  public static mapStudentsToStudentModels(students: Student[],
    studentDeadlines: Record<string, number>,
    feedbackSessionEndingTimestamp: number,
  ): StudentExtensionTableColumnModel[] {
    return students.map((student) => {
      const studentData: StudentExtensionTableColumnModel = {
        sectionName: student.sectionName,
        teamName: student.teamName,
        name: student.name,
        email: student.email,
        extensionDeadline: feedbackSessionEndingTimestamp,
        hasExtension: false,
        isSelected: false,
      };

      if (student.email in studentDeadlines) {
        studentData.hasExtension = true;
        studentData.extensionDeadline = studentDeadlines[student.email];
      }
      return studentData;
    });
  }

  public static mapInstructorsToInstructorModels(instructors: Instructor[],
    instructorDeadlines: Record<string, number>,
    feedbackSessionEndingTimestamp: number,
  ): InstructorExtensionTableColumnModel[] {
    return instructors.map((instructor) => {
      const instructorData: InstructorExtensionTableColumnModel = {
        name: instructor.name,
        role: instructor.role,
        email: instructor.email,
        extensionDeadline: feedbackSessionEndingTimestamp,
        hasExtension: false,
        isSelected: false,
      };

      if (instructor.email in instructorDeadlines) {
        instructorData.hasExtension = true;
        instructorData.extensionDeadline = instructorDeadlines[instructor.email];
      }
      return instructorData;
    });
  }

  public static getUpdatedDeadlines(
    selectedIndividuals: StudentExtensionTableColumnModel[] | InstructorExtensionTableColumnModel[],
    deadlinesToCopyFrom: Record<string, number>,
    updateDeadlinesType: DeadlineHandlerType,
    extensionTimestamp?: number,
  ): Record<string, number> {
    const record: Record<string, number> = { ...deadlinesToCopyFrom };

    if (updateDeadlinesType === DeadlineHandlerType.CREATE) {
      selectedIndividuals.forEach((x) => {
        record[x.email] = extensionTimestamp!;
      });
    } else {
      selectedIndividuals.forEach((x) => {
        delete record[x.email];
      });
    }
    return record;
  }
}
