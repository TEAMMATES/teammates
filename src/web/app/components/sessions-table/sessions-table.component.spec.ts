import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  InstructorPrivilege,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { SortBy } from '../../../types/sort-properties';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { SessionsTableColumn, SessionsTableRowModel } from './sessions-table-model';
import { SessionsTableComponent } from './sessions-table.component';
import { SessionsTableModule } from './sessions-table.module';

describe('SessionsTableComponent', () => {
  let component: SessionsTableComponent;
  let fixture: ComponentFixture<SessionsTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [SessionsTableModule, HttpClientTestingModule, RouterTestingModule, TeammatesRouterModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  const feedbackSession1: FeedbackSession = {
    courseId: 'GOT',
    timeZone: 'Asia/Singapore',
    feedbackSessionName: 'Season 8 Review',
    instructions: 'Fill up all',
    submissionStartTimestamp: 1555232400,
    submissionEndTimestamp: 1555332400,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 1554967204,
  };

  const feedbackSession2: FeedbackSession = {
    courseId: 'GOT',
    timeZone: 'Asia/Singapore',
    feedbackSessionName: 'Season 7 Review',
    instructions: 'Fill up all',
    submissionStartTimestamp: 1554232400,
    submissionEndTimestamp: 1554332400,
    gracePeriod: 100,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.LATER,
    submissionStatus: FeedbackSessionSubmissionStatus.CLOSED,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingEmailEnabled: false,
    isPublishedEmailEnabled: false,
    createdAtTimestamp: 1554967204,
  };

  const instructorCanEverything: InstructorPrivilege = {
    canModifyCourse: true,
    canModifySession: true,
    canModifyStudent: true,
    canModifyInstructor: true,
    canViewStudentInSections: true,
    canModifySessionCommentsInSections: true,
    canViewSessionInSections: true,
    canSubmitSessionInSections: true,
  };

  const instructorCannotEverything: InstructorPrivilege = {
    canModifyCourse: false,
    canModifySession: false,
    canModifyStudent: false,
    canModifyInstructor: false,
    canViewStudentInSections: false,
    canModifySessionCommentsInSections: false,
    canViewSessionInSections: false,
    canSubmitSessionInSections: false,
  };

  const sessionTable1: SessionsTableRowModel = {
    feedbackSession: feedbackSession1,
    responseRate: '8 / 9',
    isLoadingResponseRate: false,
    instructorPrivilege: instructorCanEverything,
  };

  const sessionTable2: SessionsTableRowModel = {
    feedbackSession: feedbackSession2,
    responseRate: '',
    isLoadingResponseRate: true,
    instructorPrivilege: instructorCannotEverything,
  };

  it('should snap like in home page with 2 sessions sorted by start date', () => {
    component.columnsToShow = [SessionsTableColumn.START_DATE, SessionsTableColumn.END_DATE];
    component.sessionsTableRowModelsSortBy = SortBy.SESSION_START_DATE;
    component.sessionsTableRowModels = [sessionTable1, sessionTable2];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap like in sessions page with 2 sessions sorted by session name', () => {
    component.columnsToShow = [SessionsTableColumn.COURSE_ID];
    component.sessionsTableRowModelsSortBy = SortBy.SESSION_NAME;
    component.sessionsTableRowModels = [sessionTable1, sessionTable2];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
