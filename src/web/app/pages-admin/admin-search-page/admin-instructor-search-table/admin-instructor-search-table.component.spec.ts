import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { of, throwError } from 'rxjs';
import { InstructorAccountSearchResult } from '../../../../services/search.service';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { UserService } from '../../../../services/user.service';
import { createMockNgbModalRef } from '../../../../test-helpers/mock-ngb-modal-ref';
import { AdminSessionLinksModalComponent } from '../admin-session-links-modal/admin-session-links-modal.component';
import { AdminInstructorSearchTableComponent } from './admin-instructor-search-table.component';

const DEFAULT_INSTRUCTOR_SEARCH_RESULT: InstructorAccountSearchResult = {
  userId: '42aca1be-044d-48c8-b27c-26c29daf512c',
  name: 'name',
  email: 'email',
  courseId: 'courseId',
  courseName: 'courseName',
  isCourseDeleted: false,
  institute: 'institute',
  manageAccountLink: 'manageAccountLink',
};

describe('AdminInstructorSearchTableComponent', () => {
  let component: AdminInstructorSearchTableComponent;
  let fixture: ComponentFixture<AdminInstructorSearchTableComponent>;
  let userService: UserService;
  let statusMessageService: StatusMessageService;
  let simpleModalService: SimpleModalService;
  let ngbModal: NgbModal;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminInstructorSearchTableComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService);
    statusMessageService = TestBed.inject(StatusMessageService);
    simpleModalService = TestBed.inject(SimpleModalService);
    ngbModal = TestBed.inject(NgbModal);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display deleted-course indicator', () => {
    component.instructors = [{ ...DEFAULT_INSTRUCTOR_SEARCH_RESULT, isCourseDeleted: true }];
    fixture.detectChanges();

    expect(fixture.debugElement.nativeElement.querySelector('.bin-icon')).not.toBeNull();
  });

  it('should open session links modal for an instructor', () => {
    component.instructors = [DEFAULT_INSTRUCTOR_SEARCH_RESULT];
    fixture.detectChanges();

    const modalRef = createMockNgbModalRef({
      userId: '',
      userName: '',
    });
    const openSpy = vi.spyOn(ngbModal, 'open').mockReturnValue(modalRef);

    const showLinksButton: HTMLButtonElement =
      fixture.debugElement.nativeElement.querySelector('#show-instructor-links-0');
    showLinksButton.click();

    expect(openSpy).toHaveBeenCalledWith(AdminSessionLinksModalComponent, { size: 'xl' });
    expect(modalRef.componentInstance.userId).toBe(DEFAULT_INSTRUCTOR_SEARCH_RESULT.userId);
    expect(modalRef.componentInstance.userName).toBe(DEFAULT_INSTRUCTOR_SEARCH_RESULT.name);
  });

  it('should show success message if instructor registration key is regenerated', async () => {
    component.instructors = [DEFAULT_INSTRUCTOR_SEARCH_RESULT];
    fixture.detectChanges();

    vi.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(createMockNgbModalRef());
    vi.spyOn(userService, 'regenerateUserKey').mockReturnValue(
      of({
        message: 'success',
        newRegistrationKey: 'newKey',
      }),
    );
    const successSpy = vi.spyOn(statusMessageService, 'showSuccessToast');

    const regenerateButton: HTMLButtonElement =
      fixture.debugElement.nativeElement.querySelector('#regenerate-instructor-key-0');
    regenerateButton.click();
    await fixture.whenStable();

    expect(userService.regenerateUserKey).toHaveBeenCalledWith(DEFAULT_INSTRUCTOR_SEARCH_RESULT.userId);
    expect(successSpy).toHaveBeenCalledWith('success');
    expect(component.isRegeneratingInstructorKeys[0]).toBe(false);
  });

  it('should show error message if instructor registration key regeneration fails', async () => {
    component.instructors = [DEFAULT_INSTRUCTOR_SEARCH_RESULT];
    fixture.detectChanges();

    vi.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(createMockNgbModalRef());
    vi.spyOn(userService, 'regenerateUserKey').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message.',
        },
      })),
    );
    const errorSpy = vi.spyOn(statusMessageService, 'showErrorToast');

    const regenerateButton: HTMLButtonElement =
      fixture.debugElement.nativeElement.querySelector('#regenerate-instructor-key-0');
    regenerateButton.click();
    await fixture.whenStable();

    expect(errorSpy).toHaveBeenCalledWith('This is the error message.');
    expect(component.isRegeneratingInstructorKeys[0]).toBe(false);
  });
});
