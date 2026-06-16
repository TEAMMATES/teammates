import {
  InstructorExtensionTableColumnModel,
  StudentExtensionTableColumnModel,
} from '../app/pages-instructor/instructor-session-individual-extension-page/extension-table-column-model';
import { FeedbackSession, Instructor, Student } from '../types/api-output';

/**
 * Deadline Extension utility functions.
 */
export class DeadlineExtensionHelper {
  public static getDeadlinesBeforeOrEqualToEndTime(
    deadlines: Record<string, number>,
    submissionEndTime: number,
  ): Record<string, number> {
    const deadlinesBeforeOrEqualToEndTime: Record<string, number> = {};
    for (const [id, deadlineOfIndividual] of Object.entries(deadlines)) {
      if (deadlineOfIndividual <= submissionEndTime) {
        deadlinesBeforeOrEqualToEndTime[id] = deadlineOfIndividual;
      }
    }
    return deadlinesBeforeOrEqualToEndTime;
  }

  public static mapStudentsToStudentModels(
    students: Student[],
    userDeadlines: Record<string, number>,
    feedbackSessionEndingTimestamp: number,
  ): StudentExtensionTableColumnModel[] {
    return students.map((student) => {
      const studentData: StudentExtensionTableColumnModel = {
        userId: student.userId,
        sectionName: student.sectionName,
        teamName: student.teamName,
        name: student.name,
        email: student.email,
        extensionDeadline: feedbackSessionEndingTimestamp,
        hasExtension: false,
        isSelected: false,
      };

      if (student.userId in userDeadlines) {
        studentData.hasExtension = true;
        studentData.extensionDeadline = userDeadlines[student.userId];
      }
      return studentData;
    });
  }

  public static mapInstructorsToInstructorModels(
    instructors: Instructor[],
    userDeadlines: Record<string, number>,
    feedbackSessionEndingTimestamp: number,
  ): InstructorExtensionTableColumnModel[] {
    return instructors.map((instructor) => {
      const instructorData: InstructorExtensionTableColumnModel = {
        userId: instructor.userId,
        name: instructor.name,
        role: instructor.role,
        email: instructor.email,
        extensionDeadline: feedbackSessionEndingTimestamp,
        hasExtension: false,
        isSelected: false,
      };

      if (instructor.userId in userDeadlines) {
        instructorData.hasExtension = true;
        instructorData.extensionDeadline = userDeadlines[instructor.userId];
      }
      return instructorData;
    });
  }

  public static getUpdatedDeadlinesForCreation(
    selectedStudents: StudentExtensionTableColumnModel[],
    selectedInstructors: InstructorExtensionTableColumnModel[],
    deadlinesToCopyFrom: Record<string, number>,
    extensionTimestamp?: number,
  ): Record<string, number> {
    const record: Record<string, number> = { ...deadlinesToCopyFrom };
    selectedStudents.forEach((x) => {
      record[x.userId] = extensionTimestamp!;
    });
    selectedInstructors.forEach((x) => {
      record[x.userId] = extensionTimestamp!;
    });
    return record;
  }

  public static getUpdatedDeadlinesForDeletion(
    selectedStudents: StudentExtensionTableColumnModel[],
    selectedInstructors: InstructorExtensionTableColumnModel[],
    deadlinesToCopyFrom: Record<string, number>,
  ): Record<string, number> {
    const record: Record<string, number> = { ...deadlinesToCopyFrom };
    selectedStudents.forEach((x) => {
      delete record[x.userId];
    });
    selectedInstructors.forEach((x) => {
      delete record[x.userId];
    });
    return record;
  }

  public static getOngoingUserFeedbackSessionEndingTimestamp(
    feedbackSession: FeedbackSession,
    userDeadlineExtension?: number,
  ): number {
    if (DeadlineExtensionHelper.hasUserOngoingExtension(feedbackSession, userDeadlineExtension)) {
      return userDeadlineExtension!;
    }
    return feedbackSession.submissionEndTimestamp;
  }

  public static hasUserOngoingExtension(feedbackSession: FeedbackSession, userDeadlineExtension?: number): boolean {
    return this.hasUserExtension(feedbackSession, userDeadlineExtension) && userDeadlineExtension! > Date.now();
  }

  public static getUserFeedbackSessionEndingTimestamp(
    feedbackSession: FeedbackSession,
    userDeadlineExtension?: number,
  ): number {
    if (DeadlineExtensionHelper.hasUserExtension(feedbackSession, userDeadlineExtension)) {
      return userDeadlineExtension!;
    }
    return feedbackSession.submissionEndTimestamp;
  }

  public static hasUserExtension(feedbackSession: FeedbackSession, userDeadlineExtension?: number): boolean {
    return userDeadlineExtension !== undefined && userDeadlineExtension > feedbackSession.submissionEndTimestamp;
  }
}
