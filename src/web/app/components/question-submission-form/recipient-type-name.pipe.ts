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
      case FeedbackParticipantType.OWN_TEAM:
        return 'Team';
      case FeedbackParticipantType.STUDENTS:
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
        return 'Unknown';
      default:
        return 'Unknown';
    }
  }

}
