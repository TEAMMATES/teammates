import {
  InstructorExtensionTableColumnModel,
  StudentExtensionTableColumnModel,
} from '../app/pages-instructor/instructor-session-individual-extension-page/extension-table-column-model';
import { FeedbackSession, Instructor, Student } from '../types/api-output';

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

  public static getDeadlinesBeforeOrEqualToEndTime(
    deadlines: Record<string, number>,
    submissionEndTime: number,
  ): Record<string, number> {
    const deadlinesBeforeOrEqualToEndTime: Record<string, number> = {};
    for (const [emailOfIndividual, deadlineOfIndividual] of Object.entries(deadlines)) {
      if (deadlineOfIndividual <= submissionEndTime) {
        deadlinesBeforeOrEqualToEndTime[emailOfIndividual] = deadlineOfIndividual;
      }
    }
    return deadlinesBeforeOrEqualToEndTime;
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

  public static getUpdatedDeadlinesForCreation(
    selectedIndividuals: StudentExtensionTableColumnModel[] | InstructorExtensionTableColumnModel[],
    deadlinesToCopyFrom: Record<string, number>,
    extensionTimestamp?: number,
  ): Record<string, number> {
    const record: Record<string, number> = { ...deadlinesToCopyFrom };
    selectedIndividuals.forEach((x) => {
      record[x.email] = extensionTimestamp!;
    });
    return record;
  }

  public static getUpdatedDeadlinesForDeletion(
    selectedIndividuals: StudentExtensionTableColumnModel[] | InstructorExtensionTableColumnModel[],
    deadlinesToCopyFrom: Record<string, number>,
  ): Record<string, number> {
    const record: Record<string, number> = { ...deadlinesToCopyFrom };
    selectedIndividuals.forEach((x) => {
      delete record[x.email];
    });
    return record;
  }

  public static getOngoingUserFeedbackSessionEndingTimestamp(feedbackSession: FeedbackSession): number {
    if (DeadlineExtensionHelper.hasUserOngoingExtension(feedbackSession)) {
      return feedbackSession.submissionEndWithExtensionTimestamp!;
    }
    return feedbackSession.submissionEndTimestamp;
  }

  public static hasUserOngoingExtension(feedbackSession: FeedbackSession): boolean {
    const extensionTimestamp = feedbackSession.submissionEndWithExtensionTimestamp;
    return this.hasUserExtension(feedbackSession) && extensionTimestamp! > Date.now();
  }

  public static getUserFeedbackSessionEndingTimestamp(feedbackSession: FeedbackSession): number {
    if (DeadlineExtensionHelper.hasUserExtension(feedbackSession)) {
      return feedbackSession.submissionEndWithExtensionTimestamp!;
    }
    return feedbackSession.submissionEndTimestamp;
  }

  public static hasUserExtension(feedbackSession: FeedbackSession): boolean {
    const extensionTimestamp = feedbackSession.submissionEndWithExtensionTimestamp;
    return extensionTimestamp !== undefined && extensionTimestamp > feedbackSession.submissionEndTimestamp;
  }
}
