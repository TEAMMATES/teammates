import { Pipe, PipeTransform } from '@angular/core';
import { FeedbackParticipantType } from '../../../types/api-output';

/**
 * Pipe to handle the display of {@link FeedbackParticipantType} from giver's perspective.
 */
@Pipe({
  name: 'giverTypeDescription',
})
export class GiverTypeDescriptionPipe implements PipeTransform {

  /**
   * Transforms {@link FeedbackParticipantType} to a simple description from giver's perspective.
   */
  transform(type: FeedbackParticipantType): any {
    switch (type) {
      case FeedbackParticipantType.SELF:
        return 'Feedback session creator (i.e., me)';
      case FeedbackParticipantType.STUDENTS:
        return 'Students in this course';
      case FeedbackParticipantType.INSTRUCTORS:
        return 'Instructors in this course';
      case FeedbackParticipantType.TEAMS:
        return 'Teams in this course';
      default:
        return 'Unknown';
    }
  }

}

/**
 * Pipe to handle the display of {@link FeedbackParticipantType} from recipient's perspective.
 */
@Pipe({
  name: 'recipientTypeDescription',
})
export class RecipientTypeDescriptionPipe implements PipeTransform {

  /**
   * Transforms {@link FeedbackParticipantType} to a simple description from recipient's perspective.
   */
  transform(type: FeedbackParticipantType): any {
    switch (type) {
      case FeedbackParticipantType.SELF:
        return 'Giver (Self feedback)';
      case FeedbackParticipantType.STUDENTS:
        return 'Other students in the course';
      case FeedbackParticipantType.INSTRUCTORS:
        return 'Instructors in the course';
      case FeedbackParticipantType.TEAMS:
        return 'Other teams in the course';
      case FeedbackParticipantType.OWN_TEAM:
        return "Giver's team";
      case FeedbackParticipantType.OWN_TEAM_MEMBERS:
        return "Giver's team members";
      case FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF:
        return "Giver's team members and Giver";
      case FeedbackParticipantType.NONE:
        return 'Nobody specific (For general class feedback)';
      default:
        return 'Unknown';
    }
  }

}
