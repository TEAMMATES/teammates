import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { RejectWithReasonModalComponent } from './admin-reject-with-reason-modal.component';
import {
  FeedbackSessionsGroup, InstructorAccountSearchResult,
  SearchService,
} from '../../../../services/search.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { createBuilder } from '../../../../test-helpers/generic-builder';

const DEFAULT_FEEDBACK_SESSION_GROUP: FeedbackSessionsGroup = {
  sessionName: {
    feedbackSessionUrl: 'sessionUrl',
    startTime: 'startTime',
    endTime: 'endTime',
  },
};

const instructorAccountSearchResultBuilder = createBuilder<InstructorAccountSearchResult>({
  name: 'name',
  email: 'email',
  googleId: 'googleId',
  courseId: 'courseId',
  courseName: 'courseName',
  isCourseDeleted: false,
  institute: 'institute',
  courseJoinLink: 'courseJoinLink',
  homePageLink: 'homePageLink',
  manageAccountLink: 'manageAccountLink',
  showLinks: false,
  awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
});

describe('RejectWithReasonModal', () => {
  let searchService: SearchService;
  let statusMessageService: StatusMessageService;
  let fixture: ComponentFixture<RejectWithReasonModalComponent>;
  let component: RejectWithReasonModalComponent;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
      ],
      providers: [NgbActiveModal, SearchService, StatusMessageService],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RejectWithReasonModalComponent);
    searchService = TestBed.inject(SearchService);
    statusMessageService = TestBed.inject(StatusMessageService);
    fixture.detectChanges();
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show empty title and body', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('replaceGoogleId: should set the googleId to an empty string if no instructor accounts are found', () => {
    jest.spyOn(searchService, 'searchAdmin').mockReturnValue(of({
      students: [],
      instructors: [],
      accountRequests: [],
    }));

    component.replaceGoogleId();

    expect(component.existingAccount.googleId).toEqual('');
  });

  it('replaceGoogleId: should set the googleId to the instructor accounts googleId '
  + 'if an instructor account is found', () => {
    const testInstructor = instructorAccountSearchResultBuilder.googleId('instructorGoogleId').build();

    jest.spyOn(searchService, 'searchAdmin').mockReturnValue(of({
      students: [],
      instructors: [testInstructor],
      accountRequests: [],
    }));

    component.replaceGoogleId();

    expect(component.existingAccount.googleId).toEqual('instructorGoogleId');
  });

  it('reject: should show error message when title is empty upon submitting', () => {
    component.rejectionReasonTitle = '';
    fixture.detectChanges();

    const spyStatusMessageService = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('Please provide a title for the rejection email.');
    });

    const rejectButton: any = fixture.debugElement.query(By.css('#btn-confirm-reject-request'));
    rejectButton.nativeElement.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('reject: should show error message when body is empty upon submitting', () => {
    component.rejectionReasonBody = '';
    fixture.detectChanges();

    const spyStatusMessageService = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('Please provide an email body for the rejection email.');
    });

    const rejectButton: any = fixture.debugElement.query(By.css('#btn-confirm-reject-request'));
    rejectButton.nativeElement.click();
    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('reject: should close modal with data', () => {
    const spyActiveModal = jest.spyOn(component.activeModal, 'close');
    component.rejectionReasonTitle = 'Rejection Title';
    component.rejectionReasonBody = 'Rejection Body';
    fixture.detectChanges();
    component.reject();
    expect(spyActiveModal).toHaveBeenCalled();
    expect(spyActiveModal).toHaveBeenCalledWith({
      rejectionReasonTitle: 'Rejection Title',
      rejectionReasonBody: 'Rejection Body',
    });
  });
});
