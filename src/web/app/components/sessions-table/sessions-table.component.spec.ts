// Angular imports
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Pipe, PipeTransform } from '@angular/core';
import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
  waitForAsync,
} from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
// Third-party imports
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
// Application imports
import { SessionsTableColumn, SessionsTableRowModel } from './sessions-table-model';
import { SessionsTableComponent } from './sessions-table.component';
import { SessionsTableModule } from './sessions-table.module';
import { SimpleModalService } from '../../../services/simple-modal.service';
// Types and models
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  InstructorPermissionSet,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { CopySessionModalComponent } from '../copy-session-modal/copy-session-modal.component';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
// Mock Pipes
@Pipe({ name: 'formatDateBrief' })
class MockFormatDateBriefPipe implements PipeTransform {
  transform(): string {
    return 'Mock Format Date Brief';
  }
}
@Pipe({ name: 'formatDateDetail' })
class MockFormatDateDetailPipe implements PipeTransform {
  transform(): string {
    return 'Mock Format Date Detail';
  }
}
@Pipe({ name: 'publishStatusName' })
class MockPublishStatusNamePipe implements PipeTransform {
  transform(): string {
    return 'Mock Publish Status Name';
  }
}
@Pipe({ name: 'publishStatusTooltip' })
class MockPublishStatusTooltipPipe implements PipeTransform {
  transform(): string {
    return 'Mock Publish Status Tooltip';
  }
}
@Pipe({ name: 'submissionStatusTooltip' })
class MockSubmissionStatusTooltipPipe implements PipeTransform {
  transform(): string {
    return 'Mock Submission Status Tooltip';
  }
}
@Pipe({ name: 'submissionStatusName' })
class MockSubmissionStatusNamePipe implements PipeTransform {
  transform(): string {
    return 'Mock Submission Status Name';
  }
}
describe('SessionsTableComponent', () => {
  let component: SessionsTableComponent;
  let fixture: ComponentFixture<SessionsTableComponent>;
  let ngbModal: NgbModal;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SessionsTableComponent,
        MockFormatDateBriefPipe,
        MockFormatDateDetailPipe,
        MockPublishStatusNamePipe,
        MockPublishStatusTooltipPipe,
        MockSubmissionStatusTooltipPipe,
        MockSubmissionStatusNamePipe,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        SessionsTableModule,
        TeammatesRouterModule,
      ],
      providers: [
        SimpleModalService,
        { provide: NgbModal, useValue: ngbModal },
      ],
    }).compileComponents();
  }));
  beforeEach(() => {
    fixture = TestBed.createComponent(SessionsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  it('should create', () => {
    expect(component).toBeTruthy();
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
  it('should call copySession when triggered', fakeAsync(() => {
    const spy = jest.spyOn(component.copySessionEvent, 'emit');
    const modalRef = {
      result: Promise.resolve({
        newFeedbackSessionName: 'Copied Session',
        targetCourses: ['Course1', 'Course2'],
      }),
      componentInstance: {},
    } as any;
    jest.spyOn(ngbModal, 'open').mockReturnValue(modalRef);
    component.copySession(0);
    tick();
    expect(ngbModal.open).toHaveBeenCalledWith(CopySessionModalComponent);
    expect(spy).toHaveBeenCalledWith({
      newFeedbackSessionName: 'Copied Session',
      targetCourses: ['Course1', 'Course2'],
      sessionToCopyRowIndex: 0,
    });
  }));
});
