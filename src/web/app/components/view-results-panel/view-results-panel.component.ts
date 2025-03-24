import { Component, EventEmitter, Input, Output } from '@angular/core';

import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import {
  SectionTabModel,
} from '../../pages-instructor/instructor-session-result-page/instructor-session-result-page.component';
import {
  InstructorSessionResultSectionType,
} from '../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import {
  InstructorSessionResultViewType,
} from '../../pages-instructor/instructor-session-result-page/instructor-session-result-view-type.enum';

/**
 * Displaying the view results panel.
 */
@Component({
  selector: 'tm-view-results-panel',
  templateUrl: './view-results-panel.component.html',
  styleUrls: ['./view-results-panel.component.scss'],
})
export class ViewResultsPanelComponent {

  // enum
  InstructorSessionResultSectionType: typeof InstructorSessionResultSectionType = InstructorSessionResultSectionType;
  InstructorSessionResultViewType: typeof InstructorSessionResultViewType = InstructorSessionResultViewType;

  viewTooltipText: string = 'View results in different formats';

  @Input()
  session: FeedbackSession = {
    courseId: '',
    timeZone: '',
    feedbackSessionName: '',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
    studentDeadlines: {},
    instructorDeadlines: {},
  };

  @Input()
  viewType: string = InstructorSessionResultViewType.QUESTION;

  @Input()
  section: string = '';

  @Input()
  sectionsModel: Record<string, SectionTabModel> = {};

  @Input()
  sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;

  @Input()
  groupByTeam: boolean = true;

  @Input()
  showStatistics: boolean = true;

  @Input()
  indicateMissingResponses: boolean = true;

  @Output()
  sectionChange: EventEmitter<string> = new EventEmitter<string>();

  @Output()
  viewTypeChange: EventEmitter<string> = new EventEmitter<string>();

  @Output()
  sectionTypeChange: EventEmitter<InstructorSessionResultSectionType> =
    new EventEmitter<InstructorSessionResultSectionType>();

  @Output()
  groupByTeamChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Output()
  showStatisticsChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Output()
  indicateMissingResponsesChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Output()
  collapseAllTabsEvent: EventEmitter<void> = new EventEmitter<void>();

  collapseAllTabsHandler(): void {
    this.collapseAllTabsEvent.emit();
  }

  /**
   * Handles view type changes.
   */
  handleViewTypeChange(newViewType: InstructorSessionResultViewType): void {
    if (this.viewType === newViewType) {
      // do nothing
      return;
    }
    this.viewTypeChange.emit(newViewType);

    // change tooltip text based on currently selected view type
    switch (this.viewType) {
      case InstructorSessionResultViewType.QUESTION:
        this.viewTooltipText = 'Group responses by question';
        break;
      case InstructorSessionResultViewType.GRQ:
        this.viewTooltipText = 'Group responses by giver, then by recipient, and then by question';
        break;
      case InstructorSessionResultViewType.RGQ:
        this.viewTooltipText = 'Group responses by recipient, then by giver, and then by question';
        break;
      case InstructorSessionResultViewType.GQR:
        this.viewTooltipText = 'Group responses by giver, then by question, and then by recipient';
        break;
      case InstructorSessionResultViewType.RQG:
        this.viewTooltipText = 'Group responses by recipient, then by question, and then by giver';
        break;
      default:
        this.viewTooltipText = 'View results in different formats';
    }

    // the expand all will be reset if the view type changed
    this.collapseAllTabsHandler();
  }

  handleSectionChange(newSection: string): void {
    this.section = newSection;
    this.sectionChange.emit(newSection);
  }

  handleSectionTypeChange(newSectionType: InstructorSessionResultSectionType): void {
    this.sectionTypeChange.emit(newSectionType);
  }

  handleGroupByTeamChange(newGroupByTeam: boolean): void {
    this.groupByTeamChange.emit(newGroupByTeam);
  }

  handleShowStatisticsChange(newShowStatistics: boolean): void {
    this.showStatisticsChange.emit(newShowStatistics);
  }

  handleIndicateMissingResponsesChange(newIndicateMissingResponsesChange: boolean): void {
    this.indicateMissingResponsesChange.emit(newIndicateMissingResponsesChange);
  }
}
