import { Pipe, PipeTransform } from '@angular/core';
import {
  FeedbackParticipantType,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';

/**
 * Pipe to handle the display of {@code FeedbackVisibilityType} in visibility message.
 */
@Pipe({
  name: 'visibilityEntityName',
})
export class VisibilityEntityNamePipe implements PipeTransform {

  /**
   * Transform the {@code FeedbackVisibilityType} to a name.
   *
   * @param visibilityType type to transform
   * @param questionRecipientType if the visibility is {@link FeedbackVisibilityType.RECIPIENT},
   * the param should be provided in order to know the real recipient
   * @param numberOfEntitiesToGiveFeedbackToSetting used to determines the plural form of the name
   * @param customNumberOfEntitiesToGiveFeedbackTo used to determines the plural form of the name
   */
  transform(visibilityType: FeedbackVisibilityType,
            questionRecipientType?: FeedbackParticipantType,
            numberOfEntitiesToGiveFeedbackToSetting?: NumberOfEntitiesToGiveFeedbackToSetting,
            customNumberOfEntitiesToGiveFeedbackTo?: number): string {
    switch (visibilityType) {
      case FeedbackVisibilityType.RECIPIENT: {
        // get entity name
        let recipientEntityName: string = '';
        switch (questionRecipientType) {
          case FeedbackParticipantType.INSTRUCTORS:
            recipientEntityName = 'instructor';
            break;
          case FeedbackParticipantType.STUDENTS:
          case FeedbackParticipantType.STUDENTS_EXCLUDING_SELF:
          case FeedbackParticipantType.STUDENTS_IN_SAME_SECTION:
          case FeedbackParticipantType.OWN_TEAM_MEMBERS:
          case FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF:
            recipientEntityName = 'student';
            break;
          case FeedbackParticipantType.TEAMS:
          case FeedbackParticipantType.TEAMS_EXCLUDING_SELF:
          case FeedbackParticipantType.TEAMS_IN_SAME_SECTION:
          case FeedbackParticipantType.OWN_TEAM:
            recipientEntityName = 'team';
            break;
          default:
            return 'unknown';
        }

        if ([FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.STUDENTS,
          FeedbackParticipantType.STUDENTS_EXCLUDING_SELF, FeedbackParticipantType.TEAMS,
          FeedbackParticipantType.TEAMS_EXCLUDING_SELF]
            .includes(questionRecipientType)) {
          // if questionRecipientType is one of certain participant type, add the plural form
          if (numberOfEntitiesToGiveFeedbackToSetting === NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED
              || (numberOfEntitiesToGiveFeedbackToSetting === NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM
                  && customNumberOfEntitiesToGiveFeedbackTo !== undefined
                  && customNumberOfEntitiesToGiveFeedbackTo > 1)) {
            recipientEntityName = `${recipientEntityName}s`;
          }
        }

        return `The receiving ${recipientEntityName}`;
      }
      case FeedbackVisibilityType.GIVER_TEAM_MEMBERS:
        return 'Your team members';
      case FeedbackVisibilityType.INSTRUCTORS:
        return 'Instructors in this course';
      case FeedbackVisibilityType.STUDENTS:
        return 'Other students in the course';
      case FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS:
        return "The recipient's team members";
      default:
        return 'Unknown';
    }
  }

}
