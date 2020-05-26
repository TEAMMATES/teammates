import { Component, Input, OnChanges, OnInit } from '@angular/core';
import {
  FeedbackSession, FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
} from '../../../types/api-output';

/**
 * Instructor sessions results page No Response Panel.
 */
@Component({
  selector: 'tm-instructor-session-no-response-panel',
  templateUrl: './instructor-session-no-response-panel.component.html',
  styleUrls: ['./instructor-session-no-response-panel.component.scss'],
})
export class InstructorSessionNoResponsePanelComponent implements OnInit, OnChanges {

  @Input() noResponseStudents: Student[] = [];
  @Input() section: string = '';
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
  isTabExpanded: boolean = false;

  noResponseStudentsInSection: Student[] = [];
  constructor() { }

  ngOnInit(): void {
    this.filterStudentsBySection();
  }

  ngOnChanges(): void {
    this.filterStudentsBySection();
  }

  private filterStudentsBySection(): void {
    if (this.section) {
      this.noResponseStudentsInSection =
          this.noResponseStudents.filter((student: Student) => student.sectionName === this.section);
    } else {
      this.noResponseStudentsInSection = this.noResponseStudents;
    }
  }

  /**
   * Toggles the tab of the no response panel.
   */
  toggleTab(): void {
    this.isTabExpanded = !this.isTabExpanded;
  }

  /**
   * Expands the tab of the no response panel.
   */
  expandTab(): void {
    this.isTabExpanded = true;
  }

  /**
   * Collapses the tab of the no response panel.
   */
  collapseTab(): void {
    this.isTabExpanded = false;
  }

}
