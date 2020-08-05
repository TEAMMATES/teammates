import { Pipe, PipeTransform } from '@angular/core';
import { FeedbackParticipantType } from '../../../types/api-output';

/**
 * Transforms {@link FeedbackParticipantType} to a description for generated MCQ/MSQ choices.
 */
@Pipe({
  name: 'generatedChoice',
})
export class GeneratedChoicePipe implements PipeTransform {

  transform(type: FeedbackParticipantType): string {
    switch (type) {
      case FeedbackParticipantType.STUDENTS:
        return 'students';
      case FeedbackParticipantType.STUDENTS_EXCLUDING_SELF:
        return 'students (excluding self)';
      case FeedbackParticipantType.TEAMS:
        return 'teams';
      case FeedbackParticipantType.TEAMS_EXCLUDING_SELF:
        return 'teams (excluding own team)';
      case FeedbackParticipantType.INSTRUCTORS:
        return 'instructors';
      default:
        return 'unknown';
    }
  }

}
