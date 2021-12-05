import { CommentVisibilityType, FeedbackVisibilityType } from '../types/api-output';
import { CommentVisibilityControl } from '../types/comment-visibility-control';
import { VisibilityControl } from '../types/visibility-control';

/* eslint-disable @typescript-eslint/no-non-null-assertion */
/**
 * The state machine for visibility settings for comments.
 */
export class CommentVisibilityStateMachine {

  private visibility: Map<CommentVisibilityType, Map<CommentVisibilityControl, boolean>> = new Map();

  private applicability: Set<CommentVisibilityType> = new Set();

  constructor(questionShowResponsesTo: FeedbackVisibilityType[]) {
    // init
    this.reset();
    // start from new state
    this.startFromNewState(questionShowResponsesTo);
  }

  private reset(): void {
    this.visibility.clear();
    // set all visibilities as false
    // set all fields as applicable
    for (const visibilityTypeStr of Object.keys(CommentVisibilityType)) {
      const visibilityType: CommentVisibilityType =
          CommentVisibilityType[visibilityTypeStr as keyof typeof CommentVisibilityType];
      this.visibility.set(visibilityType, new Map());
      this.applicability.add(visibilityType);
      for (const visibilityControlStr of Object.keys(VisibilityControl)) {
        const visibilityControl: CommentVisibilityControl =
            CommentVisibilityControl[visibilityControlStr as keyof typeof CommentVisibilityControl];
        this.visibility.get(visibilityType)!.set(visibilityControl, false);
      }
    }
  }

  private resetVisibility(): void {
    for (const visibilityType of Array.from(this.visibility.keys())) {
      for (const visibilityControl of Array.from(this.visibility.get(visibilityType)!.keys())) {
        this.disallowToSee(visibilityType, visibilityControl);
      }
    }
  }

  private startFromNewState(questionShowResponsesTo: FeedbackVisibilityType[]): void {
    this.reset();

    // the visibilities of comment should be a subset of response's visibilities

    if (!questionShowResponsesTo.includes(FeedbackVisibilityType.RECIPIENT)) {
      this.applicability.delete(CommentVisibilityType.RECIPIENT);
    }

    if (!questionShowResponsesTo.includes(FeedbackVisibilityType.GIVER_TEAM_MEMBERS)) {
      this.applicability.delete(CommentVisibilityType.GIVER_TEAM_MEMBERS);
    }

    if (!questionShowResponsesTo.includes(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS)) {
      this.applicability.delete(CommentVisibilityType.RECIPIENT_TEAM_MEMBERS);
    }

    if (!questionShowResponsesTo.includes(FeedbackVisibilityType.STUDENTS)) {
      this.applicability.delete(CommentVisibilityType.STUDENTS);
    }

    if (!questionShowResponsesTo.includes(FeedbackVisibilityType.INSTRUCTORS)) {
      this.applicability.delete(CommentVisibilityType.INSTRUCTORS);
    }
  }

  /**
   * Clears existing visibility settings and applied the given visibility settings.
   */
  applyVisibilitySettings(visibilitySettings: { [TKey in CommentVisibilityControl]: CommentVisibilityType[] }): void {
    this.resetVisibility();
    for (const visibilityType of visibilitySettings.SHOW_COMMENT) {
      this.allowToSee(visibilityType, CommentVisibilityControl.SHOW_COMMENT);
    }
    for (const visibilityType of visibilitySettings.SHOW_GIVER_NAME) {
      this.allowToSee(visibilityType, CommentVisibilityControl.SHOW_GIVER_NAME);
    }
  }

  /**
   * Allows the {@code visibilityType} to have the {@code VisibilityControl}.
   */
  allowToSee(visibilityType: CommentVisibilityType, visibilityControl: CommentVisibilityControl): void {
    if (!this.isVisibilityTypeApplicable(visibilityType)) {
      return;
    }
    this.visibility.get(visibilityType)!.set(visibilityControl, true);
    switch (visibilityControl) {
      case CommentVisibilityControl.SHOW_GIVER_NAME:
        // you cannot only show just giver name
        this.visibility.get(visibilityType)!.set(CommentVisibilityControl.SHOW_COMMENT, true);
        break;
      default:
    }
  }

  /**
   * Allows all applicable visibility types to see.
   */
  allowAllApplicableTypesToSee(): void {
    for (const visibilityTypeStr of Object.keys(CommentVisibilityType)) {
      const visibilityType: CommentVisibilityType =
          CommentVisibilityType[visibilityTypeStr as keyof typeof CommentVisibilityType];
      for (const visibilityControlStr of Object.keys(VisibilityControl)) {
        const visibilityControl: CommentVisibilityControl =
            CommentVisibilityControl[visibilityControlStr as keyof typeof CommentVisibilityControl];
        this.allowToSee(visibilityType, visibilityControl);
      }
    }
  }

  /**
   * Disallows the {@code visibilityType} to have the {@code visibilityControl}.
   */
  disallowToSee(visibilityType: CommentVisibilityType, visibilityControl: CommentVisibilityControl): void {
    if (!this.isVisibilityTypeApplicable(visibilityType)) {
      return;
    }
    this.visibility.get(visibilityType)!.set(visibilityControl, false);
    switch (visibilityControl) {
      case CommentVisibilityControl.SHOW_COMMENT:
        // giver name should be removed together
        this.visibility.get(visibilityType)!.set(CommentVisibilityControl.SHOW_GIVER_NAME, false);
        break;
      default:
    }
  }

  /**
   * Checks whether the {@code visibilityType} is applicable under current state.
   */
  isVisibilityTypeApplicable(visibilityType: CommentVisibilityType): boolean {
    return this.applicability.has(visibilityType);
  }

  /**
   * Checks whether the {@code visibilityType} has the {@code visibilityControl} or not.
   */
  isVisible(visibilityType: CommentVisibilityType, visibilityControl: CommentVisibilityControl): boolean {
    return this.visibility.get(visibilityType)!.get(visibilityControl)!;
  }

  /**
   * Gets the visibility type for a certain {@code visibilityControl}.
   */
  getVisibilityTypesUnderVisibilityControl(visibilityControl: CommentVisibilityControl): CommentVisibilityType[] {
    const visibilityTypes: CommentVisibilityType[] = [];
    for (const visibilityTypeStr of Object.keys(CommentVisibilityType)) {
      const visibilityType: CommentVisibilityType =
          CommentVisibilityType[visibilityTypeStr as keyof typeof CommentVisibilityType];
      if (this.isVisible(visibilityType, visibilityControl)) {
        visibilityTypes.push(visibilityType);
      }
    }
    return visibilityTypes;
  }
}
/* eslint-enable @typescript-eslint/no-non-null-assertion */
