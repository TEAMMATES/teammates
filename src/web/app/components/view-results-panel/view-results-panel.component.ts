import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FeedbackSession,
  SessionVisibleSetting,
  ResponseVisibleSetting,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
} from '../../../types/api-output';
import { SectionTabModel } from '../../pages-instructor/instructor-session-result-page/instructor-session-result-page.component'
import { InstructorSessionResultSectionType } from '../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { InstructorSessionResultViewType } from '../../pages-instructor/instructor-session-result-page/instructor-session-result-view-type.enum';

@Component({
  selector: 'tm-view-results-panel',
  templateUrl: './view-results-panel.component.html',
  styleUrls: ['./view-results-panel.component.scss']
})
export class ViewResultsPanelComponent implements OnInit {

  //enum
  InstructorSessionResultViewType: typeof InstructorSessionResultViewType = InstructorSessionResultViewType;

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
      isClosingEmailEnabled: true,
      isPublishedEmailEnabled: true,
      createdAtTimestamp: 0,
    };

  @Input()
  viewTooltipText: string = 'View results in different formats';

  @Input()
  viewType: string = InstructorSessionResultViewType.QUESTION;

  @Input()
  InstructorSessionResultSectionTypes: InstructorSessionResultSectionType[] = []

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
  handleViewTypeChangeEvent: EventEmitter<InstructorSessionResultViewType> = new EventEmitter<InstructorSessionResultViewType>()

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Handles view type changes.
   */
  handleViewTypeChangeHandler(newViewType: InstructorSessionResultViewType): void {
    this.handleViewTypeChangeEvent.emit(newViewType);
  }

}
