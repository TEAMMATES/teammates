import { FeedbackParticipantType, FeedbackVisibilityType } from '../types/api-output';
import { VisibilityControl } from '../types/visibility-control';

/* eslint-disable @typescript-eslint/no-non-null-assertion */
/**
 * The state machine for visibility settings for responses.
 */
export class VisibilityStateMachine {

  private visibility: Map<FeedbackVisibilityType, Map<VisibilityControl, boolean>> = new Map();

  private editability: Map<FeedbackVisibilityType, Map<VisibilityControl, boolean>> = new Map();

  private applicability: Set<FeedbackVisibilityType> = new Set();

  constructor(giverType: FeedbackParticipantType, recipientType: FeedbackParticipantType) {
    // init
    this.reset();
    // start from state
    this.startFromNewState(giverType, recipientType);
  }

  private reset(): void {
    this.visibility.clear();
    this.editability.clear();
    // set all visibilities as false
    // set all fields as applicable
    for (const visibilityTypeStr of Object.keys(FeedbackVisibilityType)) {
      const visibilityType: FeedbackVisibilityType =
          FeedbackVisibilityType[visibilityTypeStr as keyof typeof FeedbackVisibilityType];
      this.visibility.set(visibilityType, new Map());
      this.editability.set(visibilityType, new Map());
      this.applicability.add(visibilityType);
      for (const visibilityControlStr of Object.keys(VisibilityControl)) {
        const visibilityControl: VisibilityControl =
            VisibilityControl[visibilityControlStr as keyof typeof VisibilityControl];
        this.visibility.get(visibilityType)!.set(visibilityControl, false);
        this.editability.get(visibilityType)!.set(visibilityControl, true);
      }
    }

    // recipients' show recipient name cannot be edited
    this.editability.get(FeedbackVisibilityType.RECIPIENT)!.set(VisibilityControl.SHOW_RECIPIENT_NAME, false);
  }

  private resetVisibility(): void {
    for (const visibilityType of Array.from(this.visibility.keys())) {
      for (const visibilityControl of Array.from(this.visibility.get(visibilityType)!.keys())) {
        this.disallowToSee(visibilityType, visibilityControl);
      }
    }
  }

  private startFromNewState(giverType: FeedbackParticipantType, recipientType: FeedbackParticipantType): void {
    this.reset();
    // disable according to giver
    switch (giverType) {
      case FeedbackParticipantType.STUDENTS:
        // all options enabled when giverType is STUDENTS (subject to options disabled by recipientType)
        break;
      case FeedbackParticipantType.SELF:
      case FeedbackParticipantType.INSTRUCTORS:
        // GIVER_TEAM_MEMBERS is disabled for SELF and INSTRUCTORS because it is the same as INSTRUCTORS
        this.applicability.delete(FeedbackVisibilityType.GIVER_TEAM_MEMBERS);
        break;
      case FeedbackParticipantType.TEAMS:
        // GIVER_TEAM_MEMBERS is disabled for TEAMS because it is the same as TEAMS
        this.applicability.delete(FeedbackVisibilityType.GIVER_TEAM_MEMBERS);
        break;
      default:
        throw new Error('Unexpected giverType');
    }
    // disable according to recipient
    switch (recipientType) {
      case FeedbackParticipantType.SELF:
        // RECIPIENT is disabled because self-feedback is always visible to giver
        this.applicability.delete(FeedbackVisibilityType.RECIPIENT);
        // RECIPIENT_TEAM_MEMBERS is disabled because it is the same as GIVER_TEAM_MEMBERS
        this.applicability.delete(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS);
        break;
      case FeedbackParticipantType.STUDENTS:
      case FeedbackParticipantType.STUDENTS_EXCLUDING_SELF:
      case FeedbackParticipantType.STUDENTS_IN_SAME_SECTION:
        // all options enabled when recipientType is STUDENTS (subject to options disabled by giverType)
        break;
      case FeedbackParticipantType.OWN_TEAM:
        // RECIPIENT and RECIPIENT_TEAM_MEMBERS are disabled because they are the same as GIVER_TEAM_MEMBERS
        this.applicability.delete(FeedbackVisibilityType.RECIPIENT);
        this.applicability.delete(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS);
        break;
      case FeedbackParticipantType.INSTRUCTORS:
        // RECIPIENT_TEAM_MEMBERS is disabled because it is the same as INSTRUCTORS
        this.applicability.delete(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS);
        break;
      case FeedbackParticipantType.TEAMS:
      case FeedbackParticipantType.TEAMS_EXCLUDING_SELF:
      case FeedbackParticipantType.TEAMS_IN_SAME_SECTION:
        // RECIPIENT_TEAM_MEMBERS is disabled because it is the same as RECIPIENT
        this.applicability.delete(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS);
        break;
      case FeedbackParticipantType.OWN_TEAM_MEMBERS:
      case FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF:
        // RECIPIENT_TEAM_MEMBERS is disabled for OWN_TEAM_MEMBERS and OWN_TEAM_MEMBERS_INCLUDING_SELF
        // because it is the same as GIVER_TEAM_MEMBERS
        this.applicability.delete(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS);
        break;
      case FeedbackParticipantType.NONE:
        // RECIPIENT and RECIPIENT_TEAM_MEMBERS are disabled because there are no recipients
        this.applicability.delete(FeedbackVisibilityType.RECIPIENT);
        this.applicability.delete(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS);
        break;
      default:
        throw new Error('Unexpected recipientType');
    }
    // disable according to combination
    if ((giverType === FeedbackParticipantType.SELF || giverType === FeedbackParticipantType.INSTRUCTORS)
        && recipientType === FeedbackParticipantType.SELF) {
      // RECIPIENT_TEAM_MEMBERS is disabled because it is the same as INSTRUCTORS
      this.applicability.delete(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS);
    }

    if (giverType === FeedbackParticipantType.TEAMS
        && recipientType === FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF) {
      // RECIPIENT is disabled because this is almost like a self-feedback where giver can always see the response
      this.applicability.delete(FeedbackVisibilityType.RECIPIENT);
    }
  }

  /**
   * Clears existing visibility settings and applied the given visibility settings.
   */
  applyVisibilitySettings(visibilitySettings: { [TKey in VisibilityControl]: FeedbackVisibilityType[] }): void {
    this.resetVisibility();
    for (const visibilityType of visibilitySettings.SHOW_RESPONSE) {
      this.allowToSee(visibilityType, VisibilityControl.SHOW_RESPONSE);
    }
    for (const visibilityType of visibilitySettings.SHOW_GIVER_NAME) {
      this.allowToSee(visibilityType, VisibilityControl.SHOW_GIVER_NAME);
    }
    for (const visibilityType of visibilitySettings.SHOW_RECIPIENT_NAME) {
      this.allowToSee(visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME);
    }
  }

  /**
   * Allows the {@code visibilityType} to have the {@code VisibilityControl}.
   */
  allowToSee(visibilityType: FeedbackVisibilityType, visibilityControl: VisibilityControl): void {
    if (!this.isCellEditable(visibilityType, visibilityControl) || !this.isVisibilityTypeApplicable(visibilityType)) {
      return;
    }
    this.visibility.get(visibilityType)!.set(visibilityControl, true);
    switch (visibilityControl) {
      case VisibilityControl.SHOW_RESPONSE:
        if (visibilityType === FeedbackVisibilityType.RECIPIENT) {
          // once the response is visible for recipient, the name should also be visible or it does not make sense.
          this.visibility.get(visibilityType)!.set(VisibilityControl.SHOW_RECIPIENT_NAME, true);
        }
        break;
      case VisibilityControl.SHOW_GIVER_NAME:
      case VisibilityControl.SHOW_RECIPIENT_NAME:
        // you cannot only show just giver name or recipient name
        this.visibility.get(visibilityType)!.set(VisibilityControl.SHOW_RESPONSE, true);
        break;
      default:
    }
  }

  /**
   * Disallows the {@code visibilityType} to have the {@code VisibilityControl}.
   */
  disallowToSee(visibilityType: FeedbackVisibilityType, visibilityControl: VisibilityControl): void {
    if (!this.isCellEditable(visibilityType, visibilityControl) || !this.isVisibilityTypeApplicable(visibilityType)) {
      return;
    }
    this.visibility.get(visibilityType)!.set(visibilityControl, false);
    switch (visibilityControl) {
      case VisibilityControl.SHOW_RESPONSE:
        // giver name and recipient name should be removed together
        this.visibility.get(visibilityType)!.set(VisibilityControl.SHOW_GIVER_NAME, false);
        this.visibility.get(visibilityType)!.set(VisibilityControl.SHOW_RECIPIENT_NAME, false);
        break;
      default:
    }
  }

  /**
   * Checks whether the {@code visibilityType} is applicable under current state.
   */
  isVisibilityTypeApplicable(visibilityType: FeedbackVisibilityType): boolean {
    return this.applicability.has(visibilityType);
  }

  /**
   * Checks whether there is visibility control for {@code visibilityType}.
   */
  hasAnyVisibilityControl(visibilityType: FeedbackVisibilityType): boolean {
    return Array.from(this.visibility.get(visibilityType)!.values()).some((ele: boolean) => ele);
  }

  /**
   * Checks whether there is visibility control for all visibility type.
   */
  hasAnyVisibilityControlForAll(): boolean {
    for (const feedbackVisibilityTypeStr of Object.keys(FeedbackVisibilityType)) {
      const feedbackVisibilityType: FeedbackVisibilityType =
          FeedbackVisibilityType[feedbackVisibilityTypeStr as keyof typeof FeedbackVisibilityType];
      if (this.hasAnyVisibilityControl(feedbackVisibilityType)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether the ({@code visibilityType}, {@code visibilityControl}) is editable or not (i.e. call
   * allowToSee()/disallowToSee() will grant the visibility control).
   */
  isCellEditable(visibilityType: FeedbackVisibilityType, visibilityControl: VisibilityControl): boolean {
    return this.editability.get(visibilityType)!.get(visibilityControl)!;
  }

  /**
   * Checks whether the {@code visibilityType} has the {@code visibilityControl} or not.
   */
  isVisible(visibilityType: FeedbackVisibilityType, visibilityControl: VisibilityControl): boolean {
    return this.visibility.get(visibilityType)!.get(visibilityControl)!;
  }

  /**
   * Gets the visibility type for a certain {@code visibilityControl}.
   */
  getVisibilityTypesUnderVisibilityControl(visibilityControl: VisibilityControl): FeedbackVisibilityType[] {
    const visibilityTypes: FeedbackVisibilityType[] = [];
    for (const visibilityTypeStr of Object.keys(FeedbackVisibilityType)) {
      const visibilityType: FeedbackVisibilityType =
          FeedbackVisibilityType[visibilityTypeStr as keyof typeof FeedbackVisibilityType];
      if (this.isVisible(visibilityType, visibilityControl)) {
        visibilityTypes.push(visibilityType);
      }
    }
    return visibilityTypes;
  }

  /**
   * Gets the visibility control for a certain {@code visibilityType}.
   */
  getVisibilityControlUnderVisibilityType(visibilityType: FeedbackVisibilityType)
      : { [TKey in VisibilityControl]: boolean } {
    return {
      SHOW_RESPONSE: this.isVisible(visibilityType, VisibilityControl.SHOW_RESPONSE),
      SHOW_GIVER_NAME: this.isVisible(visibilityType, VisibilityControl.SHOW_GIVER_NAME),
      SHOW_RECIPIENT_NAME: this.isVisible(visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME),
    };
  }
}
/* eslint-enable @typescript-eslint/no-non-null-assertion */
