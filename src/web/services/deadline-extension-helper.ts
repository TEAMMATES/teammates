/**
 * Deadline Extension utility functions.
 */
export class DeadlineExtensionHelper {
  public static hasOngoingExtension(deadlines: {
    studentDeadlines: Record<string, number>,
    instructorDeadlines: Record<string, number>,
  }): boolean {
    const timeNow = Date.now();
    const allDeadlines: Record<string, number> = { ...deadlines.studentDeadlines, ...deadlines.instructorDeadlines };
    return Object.values(allDeadlines).some((deadlineTimestamp) => deadlineTimestamp > timeNow);
  }
}
