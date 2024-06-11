import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { RejectWithReasonModalComponent } from './admin-reject-with-reason-modal.component';
import { StatusMessageService } from '../../../../services/status-message.service';

describe('RejectWithReasonModal', () => {
  let statusMessageService: StatusMessageService;
  let fixture: ComponentFixture<RejectWithReasonModalComponent>;
  let component: RejectWithReasonModalComponent;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [],
      imports: [
        HttpClientTestingModule,
      ],
      providers: [NgbActiveModal, StatusMessageService],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RejectWithReasonModalComponent);
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

  it('should show error message when title is empty upon submitting', () => {
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

  it('should show error message when body is empty upon submitting', () => {
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

  it('should close modal with data', () => {
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
