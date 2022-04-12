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
}
