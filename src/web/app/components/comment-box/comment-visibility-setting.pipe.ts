import { Pipe, PipeTransform } from '@angular/core';
import { CommentVisibilityType } from '../../../types/api-output';
import { CommentVisibilityControl } from '../../../types/comment-visibility-control';

/**
 * Pipe to handle the simple display of {@link CommentVisibilityControl}.
 */
@Pipe({
  name: 'commentVisibilityControlName',
})
export class CommentVisibilityControlNamePipe implements PipeTransform {
  /**
   * Transforms {@code type} to a simple name.
   */
  transform(type: CommentVisibilityControl): any {
    switch (type) {
      case CommentVisibilityControl.SHOW_COMMENT:
        return 'Can see this comment';
      case CommentVisibilityControl.SHOW_GIVER_NAME:
        return "Can see comment giver's name";
      default:
        return 'Unknown';
    }
  }
}

/**
 * Pipe to handle the detailed display of {@link CommentVisibilityType} in the context of
 * comment visibility control.
 */
@Pipe({
  name: 'commentVisibilityTypeDescription',
})
export class CommentVisibilityTypeDescriptionPipe implements PipeTransform {
  /**
   * Transforms {@code type} to a detailed description.
   */
  transform(type: CommentVisibilityType): any {
    switch (type) {
      case CommentVisibilityType.GIVER:
        return 'Control what response giver(s) can view';
      case CommentVisibilityType.RECIPIENT:
        return 'Control what response recipient(s) can view';
      case CommentVisibilityType.INSTRUCTORS:
        return 'Control what instructors can view';
      case CommentVisibilityType.GIVER_TEAM_MEMBERS:
        return 'Control what team members of response giver can view';
      case CommentVisibilityType.RECIPIENT_TEAM_MEMBERS:
        return 'Control what team members of response recipient(s) can view';
      case CommentVisibilityType.STUDENTS:
        return 'Control what other students in this course can view';
      default:
        return 'Unknown';
    }
  }
}

/**
 * Pipe to handle the simple display of {@link CommentVisibilityType}.
 */
@Pipe({
  name: 'commentVisibilityTypeName',
})
export class CommentVisibilityTypeNamePipe implements PipeTransform {
  /**
   * Transforms {@code type} to a simple name.
   */
  transform(type: CommentVisibilityType): any {
    switch (type) {
      case CommentVisibilityType.GIVER:
        return 'Response Giver(s)';
      case CommentVisibilityType.RECIPIENT:
        return 'Response Recipient(s)';
      case CommentVisibilityType.INSTRUCTORS:
        return 'Instructors';
      case CommentVisibilityType.GIVER_TEAM_MEMBERS:
        return "Response Giver's Team Members";
      case CommentVisibilityType.RECIPIENT_TEAM_MEMBERS:
        return "Response Recipient's Team Members";
      case CommentVisibilityType.STUDENTS:
        return 'Other students in this course';
      default:
        return 'Unknown';
    }
  }
}

/**
 * Pipe to handle join display of list of {@link CommentVisibilityType}.
 */
@Pipe({
  name: 'commentVisibilityTypesJointName',
})
export class CommentVisibilityTypesJointNamePipe implements PipeTransform {
  /**
   * Transforms {@code types} to a joint name.
   */
  transform(types: CommentVisibilityType[]): any {
    if (types.length === 0) {
      return 'nobody';
    }

    const commentVisibilityTypeNamePipe: CommentVisibilityTypeNamePipe = new CommentVisibilityTypeNamePipe();
    let hint: string = '';

    types.forEach((commentVisibilityType: CommentVisibilityType, i: number) => {
      hint += commentVisibilityTypeNamePipe.transform(commentVisibilityType).toLowerCase();

      if (i !== types.length - 1) {
        hint += ', and ';
      }
    });

    return hint;
  }
}
