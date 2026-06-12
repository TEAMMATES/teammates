import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { of, throwError } from 'rxjs';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { StudentAccountSearchResult } from '../../../../services/search.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { UserService } from '../../../../services/user.service';
import { createMockNgbModalRef } from '../../../../test-helpers/mock-ngb-modal-ref';
import { AdminSessionLinksModalComponent } from '../admin-session-links-modal/admin-session-links-modal.component';
import { AdminStudentSearchTableComponent } from './admin-student-search-table.component';

const DEFAULT_STUDENT_SEARCH_RESULT: StudentAccountSearchResult = {
  userId: '81c1aaee-24f6-46f4-a8c2-2bac0e287eb4',
  name: 'name',
  email: 'email',
  courseId: 'courseId',
  courseName: 'courseName',
  isCourseDeleted: false,
  institute: 'institute',
  manageAccountLink: 'manageAccountLink',
  section: 'section',
  team: 'team',
  comments: 'comments',
  profilePageLink: 'profilePageLink',
};

describe('AdminStudentSearchTableComponent', () => {
  let component: AdminStudentSearchTableComponent;
  let fixture: ComponentFixture<AdminStudentSearchTableComponent>;
  let userService: UserService;
  let statusMessageService: StatusMessageService;
  let simpleModalService: SimpleModalService;
  let ngbModal: NgbModal;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminStudentSearchTableComponent);
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
    component.students = [{ ...DEFAULT_STUDENT_SEARCH_RESULT, isCourseDeleted: true }];
    fixture.detectChanges();

    expect(fixture.debugElement.nativeElement.querySelector('.bin-icon')).not.toBeNull();
  });

  it('should open session links modal for a student', () => {
    component.students = [DEFAULT_STUDENT_SEARCH_RESULT];
    fixture.detectChanges();

    const modalRef = createMockNgbModalRef({
      userId: '',
      userName: '',
      userTypeLabel: '',
    });
    const openSpy = vi.spyOn(ngbModal, 'open').mockReturnValue(modalRef);

    const showLinksButton: HTMLButtonElement =
      fixture.debugElement.nativeElement.querySelector('#show-student-links-0');
    showLinksButton.click();

    expect(openSpy).toHaveBeenCalledWith(AdminSessionLinksModalComponent, { size: 'xl' });
    expect(modalRef.componentInstance.userId).toBe(DEFAULT_STUDENT_SEARCH_RESULT.userId);
    expect(modalRef.componentInstance.userName).toBe(DEFAULT_STUDENT_SEARCH_RESULT.name);
    expect(modalRef.componentInstance.userTypeLabel).toBe('Student');
  });

  it('should show success message if student registration key is regenerated', async () => {
    component.students = [DEFAULT_STUDENT_SEARCH_RESULT];
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
      fixture.debugElement.nativeElement.querySelector('#regenerate-student-key-0');
    regenerateButton.click();
    await fixture.whenStable();

    expect(userService.regenerateUserKey).toHaveBeenCalledWith(DEFAULT_STUDENT_SEARCH_RESULT.userId);
    expect(successSpy).toHaveBeenCalledWith('success');
    expect(component.isRegeneratingStudentKeys[0]).toBe(false);
  });

  it('should show error message if student registration key regeneration fails', async () => {
    component.students = [DEFAULT_STUDENT_SEARCH_RESULT];
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
      fixture.debugElement.nativeElement.querySelector('#regenerate-student-key-0');
    regenerateButton.click();
    await fixture.whenStable();

    expect(errorSpy).toHaveBeenCalledWith('This is the error message.');
    expect(component.isRegeneratingStudentKeys[0]).toBe(false);
  });
});
