import { EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Abstract component for all different view type components of instructor sessions result page.
 */
export abstract class InstructorSessionResultView implements OnInit {

  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() groupByTeam: boolean = true;
  @Input() showStatistics: boolean = true;
  @Input() indicateMissingResponses: boolean = true;
  @Input() session: FeedbackSession = {
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

  @Output() toggleAndLoadTab: EventEmitter<string> = new EventEmitter<string>();

  constructor(protected viewType: InstructorSessionResultViewType) {}

  ngOnInit(): void {
  }

}
