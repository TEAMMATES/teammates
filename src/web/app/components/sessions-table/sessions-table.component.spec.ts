// src/web/app/components/sessions-table/sessions-table.component.spec.ts

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { SessionsTableColumn, SessionsTableRowModel } from './sessions-table-model';
import { SessionsTableComponent } from './sessions-table.component';
import { SessionsTableModule } from './sessions-table.module';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  InstructorPermissionSet,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Course,
} from '../../../types/api-output';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { CopySessionModalComponent } from '../copy-session-modal/copy-session-modal.component';
import { SortBy, SortOrder } from '../../../types/sort-properties';

// Remove unused imports
// import { of } from 'rxjs';
// import { SimpleModalType } from '../simple-modal/simple-modal-type';

// Mock Pipes
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'formatDateBrief' })
class MockFormatDateBriefPipe implements PipeTransform {
  transform(_value: number, _timeZone: string): string {
    return 'Mock Format Date Brief';
  }
}

@Pipe({ name: 'formatDateDetail' })
class MockFormatDateDetailPipe implements PipeTransform {
  transform(_value: number, _timeZone: string): string {
    return 'Mock Format Date Detail';
  }
}

@Pipe({ name: 'publishStatusName' })
class MockPublishStatusNamePipe implements PipeTransform {
  transform(_value: FeedbackSessionPublishStatus): string {
    return 'Mock Publish Status Name';
  }
}

@Pipe({ name: 'publishStatusTooltip' })
class MockPublishStatusTooltipPipe implements PipeTransform {
  transform(_value: FeedbackSessionPublishStatus): string {
    return 'Mock Publish Status Tooltip';
  }
}

@Pipe({ name: 'submissionStatusTooltip' })
class MockSubmissionStatusTooltipPipe implements PipeTransform {
  transform(_status: FeedbackSessionSubmissionStatus, _deadlines: any): string {
    return 'Mock Submission Status Tooltip';
  }
}

@Pipe({ name: 'submissionStatusName' })
class MockSubmissionStatusNamePipe implements PipeTransform {
  transform(_status: FeedbackSessionSubmissionStatus, _deadlines: any): string {
    return 'Mock Submission Status Name';
  }
}

describe('SessionsTableComponent', () => {
  let component: SessionsTableComponent;
  let fixture: ComponentFixture<SessionsTableComponent>;
  let ngbModal: NgbModal;
  let simpleModalService: SimpleModalService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        SessionsTableModule,
        HttpClientTestingModule,
        RouterTestingModule,
        TeammatesRouterModule,
      ],
      declarations: [
        // Declare mock pipes
        MockFormatDateBriefPipe,
        MockFormatDateDetailPipe,
        MockPublishStatusNamePipe,
        MockPublishStatusTooltipPipe,
        MockSubmissionStatusTooltipPipe,
        MockSubmissionStatusNamePipe,
      ],
      providers: [
        NgbModal,
        SimpleModalService,
        // Provide mock pipes as services if necessary
        // If component injects pipes via constructor, need to provide them
        { provide: 'FormatDateBriefPipe', useClass: MockFormatDateBriefPipe },
        { provide: 'FormatDateDetailPipe', useClass: MockFormatDateDetailPipe },
        { provide: 'PublishStatusNamePipe', useClass: MockPublishStatusNamePipe },
        { provide: 'PublishStatusTooltipPipe', useClass: MockPublishStatusTooltipPipe },
        { provide: 'SubmissionStatusTooltipPipe', useClass: MockSubmissionStatusTooltipPipe },
        { provide: 'SubmissionStatusNamePipe', useClass: MockSubmissionStatusNamePipe },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionsTableComponent);
    component = fixture.componentInstance;
    ngbModal = TestBed.inject(NgbModal);
    simpleModalService = TestBed.inject(SimpleModalService);
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
    studentDeadlines: {},
    instructorDeadlines: {},
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
    studentDeadlines: {},
    instructorDeadlines: {},
  };

  const instructorCanEverything: InstructorPermissionSet = {
    canModifyCourse: true,
    canModifySession: true,
    canModifyStudent: true,
    canModifyInstructor: true,
    canViewStudentInSections: true,
    canModifySessionCommentsInSections: true,
    canViewSessionInSections: true,
    canSubmitSessionInSections: true,
  };

  const instructorCannotEverything: InstructorPermissionSet = {
    canModifyCourse: false,
    canModifySession: false,
    canModifyStudent: false,
    canModifyInstructor: false,
    canViewStudentInSections: false,
    canModifySessionCommentsInSections: false,
    canViewSessionInSections: false,
    canSubmitSessionInSections: false,
  };

  const mockCourse: Course = {
    courseId: 'GOT',
    courseName: 'Game of Thrones',
    timeZone: 'Asia/Singapore',
    institute: 'Institute',
    creationTimestamp: 1609459200,
    deletionTimestamp: 0, // Set to a valid number
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

  it('should call copySession when copySession is triggered', fakeAsync(() => {
    const spy = spyOn(component.copySessionEvent, 'emit');
    const modalRef = jasmine.createSpyObj('NgbModalRef', ['result', 'componentInstance']);
    modalRef.result = Promise.resolve({
      newFeedbackSessionName: 'Copied Session',
      targetCourses: ['Course1', 'Course2'],
    });
    modalRef.componentInstance = {};
    spyOn(ngbModal, 'open').and.returnValue(modalRef);

    component.sessionsTableRowModels = [sessionTable1];
    component.courseCandidates = [mockCourse];

    component.copySession(0);
    tick();

    expect(ngbModal.open).toHaveBeenCalledWith(CopySessionModalComponent);
    expect(spy).toHaveBeenCalledWith({
      newFeedbackSessionName: 'Copied Session',
      targetCourses: ['Course1', 'Course2'],
      sessionToCopyRowIndex: 0,
    });
  }));

  it('should set rowClicked when setRowClicked is called', () => {
    component.setRowClicked(2);
    expect(component.rowClicked).toBe(2);
  });

  it('should emit downloadSessionResultsEvent when downloadSessionResults is called', () => {
    const spy = spyOn(component.downloadSessionResultsEvent, 'emit');
    component.downloadSessionResults(1);
    expect(spy).toHaveBeenCalledWith(1);
  });

  it('should emit moveSessionToRecycleBinEvent when moveSessionToRecycleBin is called and confirmed', fakeAsync(() => {
    const spy = spyOn(component.moveSessionToRecycleBinEvent, 'emit');
    const modalRef = jasmine.createSpyObj('NgbModalRef', ['result']);
    modalRef.result = Promise.resolve();
    spyOn(simpleModalService, 'openConfirmationModal').and.returnValue(modalRef);

    component.sessionsTableRowModels = [sessionTable1, sessionTable2];
    component.setRowData();

    component.moveSessionToRecycleBin(0);
    tick();

    expect(simpleModalService.openConfirmationModal).toHaveBeenCalled();
    expect(spy).toHaveBeenCalledWith(0);
    expect(component.sessionsTableRowModels.length).toBe(1);
    expect(component.rowsData.length).toBe(1);
  }));

  it('should emit resendResultsLinkToStudentsEvent when remindResultsLinkToStudent is called', () => {
    const spy = spyOn(component.resendResultsLinkToStudentsEvent, 'emit');
    component.remindResultsLinkToStudent(1);
    expect(spy).toHaveBeenCalledWith(1);
  });

  it('should emit sendRemindersToAllNonSubmittersEvent when sendRemindersToAllNonSubmitters is called', () => {
    const spy = spyOn(component.sendRemindersToAllNonSubmittersEvent, 'emit');
    component.sendRemindersToAllNonSubmitters(1);
    expect(spy).toHaveBeenCalledWith(1);
  });

  it('should emit sendRemindersToSelectedNonSubmittersEvent when sendRemindersToSelectedNonSubmitters is called', () => {
    const spy = spyOn(component.sendRemindersToSelectedNonSubmittersEvent, 'emit');
    component.sendRemindersToSelectedNonSubmitters(1);
    expect(spy).toHaveBeenCalledWith(1);
  });

  it('should emit submitSessionAsInstructorEvent when onSubmitSessionAsInstructor is called', () => {
    const spy = spyOn(component.submitSessionAsInstructorEvent, 'emit');
    component.submitSessionAsInstructorEvent.emit(0);
    expect(spy).toHaveBeenCalledWith(0);
  });

  it('should emit publishSessionEvent when publishSession is called and confirmed', fakeAsync(() => {
    const spy = spyOn(component.publishSessionEvent, 'emit');
    const modalRef = jasmine.createSpyObj('NgbModalRef', ['result']);
    modalRef.result = Promise.resolve();
    spyOn(simpleModalService, 'openConfirmationModal').and.returnValue(modalRef);

    component.sessionsTableRowModels = [sessionTable1];
    component.setRowData();
    const rowIndex = 0;
    const rowData = component.rowsData[rowIndex];
    const columnsData = component.columnsData;

    component.publishSession(rowIndex, rowData, columnsData);
    tick();

    expect(simpleModalService.openConfirmationModal).toHaveBeenCalled();
    expect(spy).toHaveBeenCalledWith({ idx: rowIndex, rowData, columnsData });
  }));

  it('should emit unpublishSessionEvent when unpublishSession is called and confirmed', fakeAsync(() => {
    const spy = spyOn(component.unpublishSessionEvent, 'emit');
    const modalRef = jasmine.createSpyObj('NgbModalRef', ['result']);
    modalRef.result = Promise.resolve();
    spyOn(simpleModalService, 'openConfirmationModal').and.returnValue(modalRef);

    component.sessionsTableRowModels = [sessionTable1];
    component.setRowData();
    const rowIndex = 0;
    const rowData = component.rowsData[rowIndex];
    const columnsData = component.columnsData;

    component.unpublishSession(rowIndex, rowData, columnsData);
    tick();

    expect(simpleModalService.openConfirmationModal).toHaveBeenCalled();
    expect(spy).toHaveBeenCalledWith({ idx: rowIndex, rowData, columnsData });
  }));

  it('should emit loadResponseRateEvent when loadResponseRateEvent is emitted', () => {
    const spy = spyOn(component.loadResponseRateEvent, 'emit');
    component.loadResponseRateEvent.emit(0);
    expect(spy).toHaveBeenCalledWith(0);
  });

  it('should cover createRowData with displayValue and style', () => {
    const config = {
      value: 'Test Value',
      displayValue: 'Display Value',
      style: 'bold',
    };

    const result = component.createRowData(config);

    expect(result).toEqual([
      {
        value: 'Test Value',
        displayValue: 'Display Value',
        style: 'bold',
      },
    ]);
  });

  it('should cover createColumnData with headerToolTip, alignment, and headerClass', () => {
    const config = {
      header: 'Test Header',
      sortBy: SortBy.SESSION_NAME,
      headerToolTip: 'Header Tooltip',
      alignment: 'center' as 'center', // Use allowed value
      headerClass: 'header-class',
    };

    const result = component.createColumnData(config);

    expect(result).toEqual([
      {
        header: 'Test Header',
        sortBy: SortBy.SESSION_NAME,
        headerToolTip: 'Header Tooltip',
        alignment: 'center',
        headerClass: 'header-class',
      },
    ]);
  });

  it('should cover getDeadlines method', () => {
    const model: SessionsTableRowModel = sessionTable1;

    const result = component.getDeadlines(model);

    expect(result).toEqual({
      studentDeadlines: model.feedbackSession.studentDeadlines,
      instructorDeadlines: model.feedbackSession.instructorDeadlines,
    });
  });

  it('should emit sortSessionsTableRowModelsEvent when sortSessionsTableRowModelsEventHandler is called', () => {
    const spy = spyOn(component.sortSessionsTableRowModelsEvent, 'emit');
    const event = { sortBy: SortBy.SESSION_NAME, sortOrder: SortOrder.DESC };

    component.sortSessionsTableRowModelsEventHandler(event);

    expect(spy).toHaveBeenCalledWith(event);
  });
});
