import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { Observable, first } from 'rxjs';
import { InstructorRequestFormModel } from './instructor-request-form-model';
import { InstructorRequestFormComponent } from './instructor-request-form.component';
import { AccountService } from '../../../../services/account.service';
import { AccountCreateRequest, AccountVerificationRequestStatus } from '../../../../types/api-request';
import { AccountVerificationRequest } from '../../../../types/api-output';

describe('InstructorRequestFormComponent', () => {
  let component: InstructorRequestFormComponent;
  let fixture: ComponentFixture<InstructorRequestFormComponent>;
  let accountService: AccountService;
  const typicalModel: InstructorRequestFormModel = {
    name: 'John Doe',
    institution: 'Example Institution',
    country: 'Example Country',
    email: 'jd@example.edu',
    comments: '',
  };
  const typicalCreateRequest: AccountCreateRequest = {
    instructorEmail: typicalModel.email,
    instructorName: typicalModel.name,
    instructorInstitution: typicalModel.institution,
    instructorCountry: typicalModel.country,
  };
  const typicalAccountVerificationRequest: AccountVerificationRequest = {
    accountVerificationRequestId: 'id',
    email: typicalModel.email,
    name: typicalModel.name,
    institute: typicalModel.institution,
    country: typicalModel.country,
    status: AccountVerificationRequestStatus.PENDING,
    createdAt: 0,
  };

  const accountServiceStub: Partial<AccountService> = {
    createAccountVerificationRequest: () =>
      new Observable((subscriber) => {
        subscriber.next(typicalAccountVerificationRequest);
      }),
  };

  /**
   * Fills in form fields with the given data.
   *
   * @param data Data to fill form with.
   */
  function fillFormWith(data: InstructorRequestFormModel): void {
    component.name.setValue(data.name);
    component.institution.setValue(data.institution);
    component.country.setValue(data.country);
    component.email.setValue(data.email);
    component.comments.setValue(data.comments);
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [{ provide: AccountService, useValue: accountServiceStub }, provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorRequestFormComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    fixture.detectChanges();
    vi.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render correctly', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should run onSubmit() when submit button is clicked', () => {
    vi.spyOn(component, 'onSubmit');

    fillFormWith(typicalModel);
    const submitButton = fixture.debugElement.query(By.css('#submit-button'));
    submitButton.nativeElement.click();

    expect(component.onSubmit).toHaveBeenCalledTimes(1);
  });

  it('should emit requestSubmissionEvent with the correct data when form is submitted', () => {
    vi.spyOn(accountService, 'createAccountVerificationRequest').mockReturnValue(
      new Observable((subscriber) => {
        subscriber.next(typicalAccountVerificationRequest);
      }),
    );

    // Listen for emitted value
    let actualModel: InstructorRequestFormModel | null = null;
    component.requestSubmissionEvent.pipe(first()).subscribe((data: InstructorRequestFormModel) => {
      actualModel = data;
    });

    fillFormWith(typicalModel);
    component.onSubmit();

    expect(actualModel).toBeTruthy();
    expect(actualModel!.name).toBe(typicalModel.name);
    expect(actualModel!.institution).toBe(typicalModel.institution);
    expect(actualModel!.country).toBe(typicalModel.country);
    expect(actualModel!.email).toBe(typicalModel.email);
    expect(actualModel!.comments).toBe(typicalModel.comments);
  });

  it('should send the correct request data when form is submitted', () => {
    vi.spyOn(accountService, 'createAccountVerificationRequest').mockReturnValue(
      new Observable((subscriber) => {
        subscriber.next(typicalAccountVerificationRequest);
      }),
    );

    fillFormWith(typicalModel);
    component.onSubmit();

    expect(accountService.createAccountVerificationRequest).toHaveBeenCalledTimes(1);
    expect(accountService.createAccountVerificationRequest).toHaveBeenCalledWith(expect.objectContaining(typicalCreateRequest));
  });
});
