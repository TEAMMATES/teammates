import { Pipe, PipeTransform } from '@angular/core';
import { QuestionGiverType, QuestionRecipientType } from '../../../types/api-output';

/**
 * Pipe to handle the display of {@link QuestionGiverType} from giver's perspective.
 */
@Pipe({ name: 'giverTypeDescription' })
export class GiverTypeDescriptionPipe implements PipeTransform {
  /**
   * Transforms {@link QuestionGiverType} to a simple description from giver's perspective.
   */
  transform(type: QuestionGiverType): any {
    switch (type) {
      case QuestionGiverType.SELF:
        return 'Feedback session creator (i.e., me)';
      case QuestionGiverType.STUDENTS:
        return 'Students in this course';
      case QuestionGiverType.INSTRUCTORS:
        return 'Instructors in this course';
      case QuestionGiverType.TEAMS:
        return 'Teams in this course';
      default:
        return 'Unknown';
    }
  }
}

/**
 * Pipe to handle the display of {@link QuestionRecipientType} from recipient's perspective.
 */
@Pipe({ name: 'recipientTypeDescription' })
export class RecipientTypeDescriptionPipe implements PipeTransform {
  /**
   * Transforms {@link QuestionRecipientType} to a simple description from recipient's perspective.
   */
  transform(type: QuestionRecipientType): any {
    switch (type) {
      case QuestionRecipientType.SELF:
        return 'Giver (Self feedback)';
      case QuestionRecipientType.STUDENTS:
        return 'Students in the course';
      case QuestionRecipientType.STUDENTS_EXCLUDING_SELF:
        return 'Other students in the course';
      case QuestionRecipientType.STUDENTS_IN_SAME_SECTION:
        return 'Other students in the same section';
      case QuestionRecipientType.INSTRUCTORS:
        return 'Instructors in the course';
      case QuestionRecipientType.TEAMS:
        return 'Teams in the course';
      case QuestionRecipientType.TEAMS_EXCLUDING_SELF:
        return 'Other teams in the course';
      case QuestionRecipientType.TEAMS_IN_SAME_SECTION:
        return 'Other teams in the same section';
      case QuestionRecipientType.OWN_TEAM:
        return "Giver's team";
      case QuestionRecipientType.OWN_TEAM_MEMBERS:
        return "Giver's team members";
      case QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF:
        return "Giver's team members and Giver";
      case QuestionRecipientType.NONE:
        return 'Nobody specific (For general class feedback)';
      default:
        return 'Unknown';
    }
  }
}

/**
 * Pipe to handle the simplified display of {@link QuestionRecipientType} from recipient's perspective.
 */
@Pipe({ name: 'recipientTypeSimplifiedDescription' })
export class RecipientTypeSimplifiedDescriptionPipe implements PipeTransform {
  /**
   * Transforms {@link QuestionRecipientType} to a simple description from recipient's perspective.
   */
  transform(type: QuestionRecipientType): string {
    switch (type) {
      case QuestionRecipientType.STUDENTS:
      case QuestionRecipientType.STUDENTS_EXCLUDING_SELF:
      case QuestionRecipientType.STUDENTS_IN_SAME_SECTION:
        return 'students';
      case QuestionRecipientType.INSTRUCTORS:
        return 'instructors';
      case QuestionRecipientType.TEAMS:
      case QuestionRecipientType.TEAMS_EXCLUDING_SELF:
      case QuestionRecipientType.TEAMS_IN_SAME_SECTION:
        return 'teams';
      default:
        return 'Unknown';
    }
  }
}
