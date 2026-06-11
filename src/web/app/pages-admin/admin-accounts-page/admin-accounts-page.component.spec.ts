import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AdminAccountsPageComponent } from './admin-accounts-page.component';
import { AccountService } from '../../../services/account.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Account, JoinState } from '../../../types/api-output';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';

const DEFAULT_ACCOUNT: Account = {
  accountId: '63375cd0-db90-4e54-b7db-81ba478bdcbc',
  googleId: 'alice.google',
  name: 'Alice',
  email: 'alice@example.com',
  instructors: [
    {
      userId: '2f730f03-59fd-47df-93e3-f17fd5dce1dc',
      courseId: 'CS101',
      courseName: 'Programming Methodology',
      email: 'alice@example.com',
      name: 'Alice',
      role: undefined,
      joinState: JoinState.JOINED,
      key: '',
      institute: 'NUS',
    },
  ],
  students: [
    {
      userId: '1b401154-8b29-4912-a158-7b8c091ca8b9',
      courseId: 'CS102',
      courseName: 'Data Structures',
      email: 'alice@example.com',
      name: 'Alice',
      comments: '',
      teamId: 'team-1',
      teamName: 'Team 1',
      sectionId: 'section-b',
      sectionName: 'Section B',
      joinState: JoinState.JOINED,
      key: '',
      institute: 'NUS',
    },
  ],
};

describe('AdminAccountsPageComponent', () => {
  let component: AdminAccountsPageComponent;
  let fixture: ComponentFixture<AdminAccountsPageComponent>;
  let accountService: AccountService;
  let simpleModalService: SimpleModalService;
  let statusMessageService: StatusMessageService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    accountService = TestBed.inject(AccountService);
    simpleModalService = TestBed.inject(SimpleModalService);
    statusMessageService = TestBed.inject(StatusMessageService);

    vi.spyOn(accountService, 'getAccount').mockReturnValue(of(structuredClone(DEFAULT_ACCOUNT)));

    fixture = TestBed.createComponent(AdminAccountsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should unlink the account after confirmation', async () => {
    vi.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(createMockNgbModalRef());
    const unlinkSpy = vi.spyOn(accountService, 'unlinkAccount').mockReturnValue(
      of({
        message: 'Account unlinked successfully.',
      }),
    );
    const successSpy = vi.spyOn(statusMessageService, 'showSuccessToast').mockImplementation(() => {});

    component.unlinkStudentAccount(component.accountInfo.students[0]);
    await Promise.resolve();

    expect(unlinkSpy).toHaveBeenCalledWith('1b401154-8b29-4912-a158-7b8c091ca8b9');
    expect(component.accountInfo.students).toHaveLength(0);
    expect(successSpy).toHaveBeenCalledWith('The account has been successfully unlinked from the user profile.');
  });

  it('should unlink the instructor account after confirmation', async () => {
    vi.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(createMockNgbModalRef());
    const unlinkSpy = vi.spyOn(accountService, 'unlinkAccount').mockReturnValue(
      of({
        message: 'Account unlinked successfully.',
      }),
    );
    const successSpy = vi.spyOn(statusMessageService, 'showSuccessToast').mockImplementation(() => {});

    component.unlinkInstructorAccount(component.accountInfo.instructors[0]);
    await Promise.resolve();

    expect(unlinkSpy).toHaveBeenCalledWith('2f730f03-59fd-47df-93e3-f17fd5dce1dc');
    expect(component.accountInfo.instructors).toHaveLength(0);
    expect(successSpy).toHaveBeenCalledWith(
      'The account has been successfully unlinked from the user profile.',
    );
  });

  it('should show error message if unlinking fails', async () => {
    vi.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(createMockNgbModalRef());
    vi.spyOn(accountService, 'unlinkAccount').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message.',
        },
      })),
    );
    const errorSpy = vi.spyOn(statusMessageService, 'showErrorToast').mockImplementation(() => {});

    component.unlinkStudentAccount(component.accountInfo.students[0]);
    await Promise.resolve();

    expect(component.accountInfo.students).toHaveLength(1);
    expect(errorSpy).toHaveBeenCalledWith('This is the error message.');
  });
});
