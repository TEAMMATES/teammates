import { Pipe, PipeTransform } from '@angular/core';
import { FeedbackParticipantType } from '../../../types/api-output';

/**
 * Pipe to handle the display of {@code FeedbackParticipantType}.
 */
@Pipe({
  name: 'recipientTypeName',
})
export class RecipientTypeNamePipe implements PipeTransform {

  /**
   * Transforms {@link FeedbackQuestionType} to a entity type name.
   */
  transform(recipientType: FeedbackParticipantType, giverType: FeedbackParticipantType): string {
    switch (recipientType) {
      case FeedbackParticipantType.TEAMS:
      case FeedbackParticipantType.TEAMS_IN_SAME_SECTION:
      case FeedbackParticipantType.TEAMS_EXCLUDING_SELF:
      case FeedbackParticipantType.OWN_TEAM:
        return 'Team';
      case FeedbackParticipantType.STUDENTS:
      case FeedbackParticipantType.STUDENTS_IN_SAME_SECTION:
      case FeedbackParticipantType.STUDENTS_EXCLUDING_SELF:
        return 'Student';
      case FeedbackParticipantType.INSTRUCTORS:
        return 'Instructor';
      case FeedbackParticipantType.OWN_TEAM_MEMBERS:
      case FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF:
        if (giverType === FeedbackParticipantType.STUDENTS) {
          return 'Student';
        }
        if (giverType === FeedbackParticipantType.INSTRUCTORS) {
          return 'Instructor';
        }
        if (giverType === FeedbackParticipantType.TEAMS) {
          return 'Student';
        }
        return 'Unknown';
      default:
        return 'Unknown';
    }
  }

}
