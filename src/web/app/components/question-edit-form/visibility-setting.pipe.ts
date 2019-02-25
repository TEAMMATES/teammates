import { Pipe, PipeTransform } from '@angular/core';
import { FeedbackVisibilityType } from '../../../types/api-output';
import { VisibilityControl } from '../../../types/visibility-control';

/**
 * Pipe to handle the simple display of {@link VisibilityControl}.
 */
@Pipe({
  name: 'visibilityControlName',
})
export class VisibilityControlNamePipe implements PipeTransform {

  /**
   * Transforms {@code type} to a simple name.
   */
  transform(type: VisibilityControl): any {
    switch (type) {
      case VisibilityControl.SHOW_RESPONSE:
        return 'Can see answer';
      case VisibilityControl.SHOW_RECIPIENT_NAME:
        return "Can see recipient's name";
      case VisibilityControl.SHOW_GIVER_NAME:
        return "Can see giver's name";
      default:
        return 'Unknown';
    }
  }

}

/**
 * Pipe to handle the detailed display of {@link FeedbackVisibilityType} in the context of
 * visibility control.
 */
@Pipe({
  name: 'visibilityTypeDescription',
})
export class VisibilityTypeDescriptionPipe implements PipeTransform {

  /**
   * Transforms {@code type} to a detailed description.
   */
  transform(type: FeedbackVisibilityType): any {
    switch (type) {
      case FeedbackVisibilityType.RECIPIENT:
        return 'Control what feedback recipient(s) can view';
      case FeedbackVisibilityType.INSTRUCTORS:
        return 'Control what instructors can view';
      case FeedbackVisibilityType.GIVER_TEAM_MEMBERS:
        return 'Control what team members of feedback giver can view';
      case FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS:
        return 'Control what team members of feedback recipients can view';
      case FeedbackVisibilityType.STUDENTS:
        return 'Control what other students can view';
      default:
        return 'Unknown';
    }
  }

}

/**
 * Pipe to handle the simple display of {@link FeedbackVisibilityType}.
 */
@Pipe({
  name: 'visibilityTypeName',
})
export class VisibilityTypeNamePipe implements PipeTransform {

  /**
   * Transforms {@code type} to a simple name.
   */
  transform(type: FeedbackVisibilityType): any {
    switch (type) {
      case FeedbackVisibilityType.RECIPIENT:
        return 'Recipient(s)';
      case FeedbackVisibilityType.INSTRUCTORS:
        return 'Instructors';
      case FeedbackVisibilityType.GIVER_TEAM_MEMBERS:
        return "Giver's Team Members";
      case FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS:
        return "Recipient's Team Members";
      case FeedbackVisibilityType.STUDENTS:
        return 'Other students';
      default:
        return 'Unknown';
    }
  }

}
