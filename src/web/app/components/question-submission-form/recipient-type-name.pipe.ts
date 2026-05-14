import { Pipe, PipeTransform } from '@angular/core';
import { QuestionGiverType, QuestionRecipientType } from '../../../types/api-output';

/**
 * Pipe to handle the display of recipient types.
 */
@Pipe({ name: 'recipientTypeName' })
export class RecipientTypeNamePipe implements PipeTransform {
  /**
   * Transforms {@link FeedbackQuestionType} to a entity type name.
   */
  transform(recipientType: QuestionRecipientType, giverType: QuestionGiverType): string {
    switch (recipientType) {
      case QuestionRecipientType.TEAMS:
      case QuestionRecipientType.TEAMS_IN_SAME_SECTION:
      case QuestionRecipientType.TEAMS_EXCLUDING_SELF:
      case QuestionRecipientType.OWN_TEAM:
        return 'Team';
      case QuestionRecipientType.STUDENTS:
      case QuestionRecipientType.STUDENTS_IN_SAME_SECTION:
      case QuestionRecipientType.STUDENTS_EXCLUDING_SELF:
        return 'Student';
      case QuestionRecipientType.INSTRUCTORS:
        return 'Instructor';
      case QuestionRecipientType.OWN_TEAM_MEMBERS:
      case QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF:
        if (giverType === QuestionGiverType.STUDENTS) {
          return 'Student';
        }
        if (giverType === QuestionGiverType.INSTRUCTORS) {
          return 'Instructor';
        }
        if (giverType === QuestionGiverType.TEAMS) {
          return 'Student';
        }
        return 'Unknown';
      default:
        return 'Unknown';
    }
  }
}
