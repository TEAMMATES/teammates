import { Pipe, PipeTransform } from '@angular/core';
import { QuestionRecipientType } from '../../../types/api-output';

/**
 * Transforms {@link QuestionRecipientType} to a description for generated MCQ/MSQ choices.
 */
@Pipe({ name: 'generatedChoice' })
export class GeneratedChoicePipe implements PipeTransform {
  transform(type: QuestionRecipientType): string {
    switch (type) {
      case QuestionRecipientType.STUDENTS:
        return 'students';
      case QuestionRecipientType.STUDENTS_EXCLUDING_SELF:
        return 'students (excluding self)';
      case QuestionRecipientType.TEAMS:
        return 'teams';
      case QuestionRecipientType.TEAMS_EXCLUDING_SELF:
        return 'teams (excluding own team)';
      case QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF:
        return 'team members';
      case QuestionRecipientType.OWN_TEAM_MEMBERS:
        return 'team members (excluding self)';
      case QuestionRecipientType.INSTRUCTORS:
        return 'instructors';
      default:
        return 'unknown';
    }
  }
}
