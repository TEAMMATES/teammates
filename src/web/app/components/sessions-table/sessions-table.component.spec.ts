import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SessionsTableColumn, SessionsTableRowModel } from './sessions-table-model';
import { SessionsTableComponent } from './sessions-table.component';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  InstructorPermissionSet,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';

describe('SessionsTableComponent', () => {
  let component: SessionsTableComponent;
  let fixture: ComponentFixture<SessionsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

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
    feedbackSessionId: 'first-session-id',
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
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 1554967204,
  };

  const feedbackSession2: FeedbackSession = {
    feedbackSessionId: 'second-session-id',
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
    isClosingSoonEmailEnabled: false,
    isPublishedEmailEnabled: false,
    createdAtTimestamp: 1554967204,
  };

  const instructorCanEverything: InstructorPermissionSet = {
    canModifyCourse: true,
    canModifySession: true,
    canModifyStudent: true,
    canModifyInstructor: true,
    canViewStudent: true,
    canModifySessionComments: true,
    canViewSession: true,
    canSubmitSession: true,
  };

  const instructorCannotEverything: InstructorPermissionSet = {
    canModifyCourse: false,
    canModifySession: false,
    canModifyStudent: false,
    canModifyInstructor: false,
    canViewStudent: false,
    canModifySessionComments: false,
    canViewSession: false,
    canSubmitSession: false,
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
    component.sessionsTableRowModels = [sessionTable1, sessionTable2];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap like in sessions page with 2 sessions sorted by session name', () => {
    component.columnsToShow = [SessionsTableColumn.COURSE_ID];
    component.sessionsTableRowModels = [sessionTable1, sessionTable2];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should create column data when column is included', () => {
    component.columnsToShow = [SessionsTableColumn.COURSE_ID];

    const result = component.createColumnData({
      columnType: SessionsTableColumn.COURSE_ID,
      header: 'Course ID',
    });

    expect(result.length).toBe(1);
    expect(result[0].header).toBe('Course ID');
  });

  it('should return empty column data when column is not included', () => {
    component.columnsToShow = [];

    const result = component.createColumnData({
      columnType: SessionsTableColumn.COURSE_ID,
      header: 'Course ID',
    });

    expect(result).toEqual([]);
  });

  it('should create row data when column is included', () => {
    component.columnsToShow = [SessionsTableColumn.COURSE_ID];

    const result = component.createRowData({
      columnType: SessionsTableColumn.COURSE_ID,
      value: 'CS101',
    });

    expect(result.length).toBe(1);
    expect(result[0].value).toBe('CS101');
  });

  it('should return empty row data when column is not included', () => {
    component.columnsToShow = [];

    const result = component.createRowData({
      columnType: SessionsTableColumn.COURSE_ID,
      value: 'CS101',
    });

    expect(result).toEqual([]);
  });

  it('should emit sort event', () => {
    vi.spyOn(component.sortSessionsTableRowModelsEvent, 'emit');

    component.sortSessionsTableRowModelsEventHandler({
      sortBy: component.SortBy.COURSE_ID,
      sortOrder: component.SortOrder.ASC,
    });

    expect(component.sortSessionsTableRowModelsEvent.emit).toHaveBeenCalledWith({
      sortBy: component.SortBy.COURSE_ID,
      sortOrder: component.SortOrder.ASC,
    });
  });

  it('should set row clicked', () => {
    component.setRowClicked(1);

    expect(component.rowClicked).toBe(1);
  });

  it('should emit download session results event', () => {
    vi.spyOn(component.downloadSessionResultsEvent, 'emit');

    component.downloadSessionResults(0);

    expect(component.downloadSessionResultsEvent.emit).toHaveBeenCalledWith(0);
  });

  it('should emit resend results link event', () => {
    vi.spyOn(component.resendResultsLinkToStudentsEvent, 'emit');

    component.remindResultsLinkToStudent(1);

    expect(component.resendResultsLinkToStudentsEvent.emit).toHaveBeenCalledWith(1);
  });

  it('should include optional fields in column data when provided', () => {
    component.columnsToShow = [SessionsTableColumn.COURSE_ID];

    const result = component.createColumnData({
      columnType: SessionsTableColumn.COURSE_ID,
      header: 'Course ID',
      sortBy: component.SortBy.COURSE_ID,
      headerToolTip: 'tooltip',
      alignment: 'center',
      headerClass: 'test-class',
    });

    expect(result).toHaveLength(1);
    expect(result[0].sortBy).toBe(component.SortBy.COURSE_ID);
    expect(result[0].headerToolTip).toBe('tooltip');
    expect(result[0].alignment).toBe('center');
    expect(result[0].headerClass).toBe('test-class');
  });

  it('should include optional fields in row data when provided', () => {
    component.columnsToShow = [SessionsTableColumn.COURSE_ID];

    const customComponent = { component: {} as never, componentData: () => ({}) };
    const result = component.createRowData({
      columnType: SessionsTableColumn.COURSE_ID,
      value: 'CS101',
      displayValue: 'Display CS101',
      customComponent,
      style: "{ color: 'red' }",
    });

    expect(result).toHaveLength(1);
    expect(result[0].value).toBe('CS101');
    expect(result[0].displayValue).toBe('Display CS101');
    expect(result[0].customComponent).toBe(customComponent);
    expect(result[0].style).toEqual("{ color: 'red' }");
  });

  it('should create response rate component data and emit on click', () => {
    vi.spyOn(component.loadResponseRateEvent, 'emit');

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const result = (component as any).createCellWithResponseRateComponent(sessionTable1);
    const data = result.customComponent.componentData(0);

    expect(data.idx).toBe(0);
    expect(data.responseRate).toBe('8 / 9');
    expect(data.empty).toBe(false);
    expect(data.isLoading).toBe(false);

    data.onClick();

    expect(component.loadResponseRateEvent.emit).toHaveBeenCalledWith(0);
  });

  it('should create group button data and call setRowClicked callback', () => {
    const setRowClickedSpy = vi.spyOn(component, 'setRowClicked');

    component.sessionsTableRowModels = [sessionTable1];
    component.rowsData = [[]];
    component.columnsData = [];

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const result = (component as any).createCellWithGroupButtonsComponent(sessionTable1);
    const data = result.customComponent.componentData(0);

    data.setRowClicked();

    expect(setRowClickedSpy).toHaveBeenCalledWith(0);
  });
});
