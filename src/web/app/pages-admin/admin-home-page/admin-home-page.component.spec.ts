import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AdminHomePageComponent } from './admin-home-page.component';
import { InstructorData } from './instructor-data';
import { AccountService } from '../../../services/account.service';
import { LinkService } from '../../../services/link.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { AccountRequestStatus } from '../../../types/api-output';

const SINGLE_INSTRUCTOR_REQUIRED_FIELDS_TOAST =
    'Please fill in name, email, institution, and country before adding an instructor.';

describe('AdminHomePageComponent', () => {
  let component: AdminHomePageComponent;
  let fixture: ComponentFixture<AdminHomePageComponent>;
  let accountService: AccountService;
  let linkService: LinkService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminHomePageComponent);
    accountService = TestBed.inject(AccountService);
    linkService = TestBed.inject(LinkService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add one instructor to list if all fields are filled', () => {
    component.instructorName = 'Instructor Name';
    component.instructorEmail = 'instructor@example.com';
    component.instructorInstitution = 'Instructor Institution';
    component.instructorCountry = 'Test Country';
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor');
    button.click();

    expect(component.instructorName).toEqual('');
    expect(component.instructorEmail).toEqual('');
    expect(component.instructorInstitution).toEqual('');
    expect(component.instructorCountry).toEqual('');
    expect(component.instructorsConsolidated.length).toEqual(1);
    expect(component.instructorsConsolidated[0]).toEqual({
      email: 'instructor@example.com',
      institution: 'Instructor Institution',
      country: 'Test Country',
      name: 'Instructor Name',
      status: 'PENDING',
      isCurrentlyBeingEdited: false,
    });
  });

  it('should not add one instructor to list if some fields are empty', () => {
    const statusMessageService: StatusMessageService = TestBed.inject(StatusMessageService);
    const errorToastSpy: jest.SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast');

    component.instructorName = 'Instructor Name';
    component.instructorEmail = '';
    component.instructorInstitution = 'Instructor Institution';
    component.instructorCountry = 'Test Country';
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor');
    button.click();

    expect(component.instructorName).toEqual('Instructor Name');
    expect(component.instructorEmail).toEqual('');
    expect(component.instructorInstitution).toEqual('Instructor Institution');
    expect(component.instructorCountry).toEqual('Test Country');
    expect(component.instructorsConsolidated.length).toEqual(0);
    expect(errorToastSpy).toHaveBeenCalledWith(SINGLE_INSTRUCTOR_REQUIRED_FIELDS_TOAST);

    component.instructorName = '';
    component.instructorEmail = 'instructor@example.com';

    button.click();

    expect(component.instructorName).toEqual('');
    expect(component.instructorEmail).toEqual('instructor@example.com');
    expect(component.instructorInstitution).toEqual('Instructor Institution');
    expect(component.instructorCountry).toEqual('Test Country');
    expect(component.instructorsConsolidated.length).toEqual(0);

    component.instructorName = 'Instructor Name';
    component.instructorInstitution = '';
    component.instructorCountry = 'Test Country';

    button.click();

    expect(component.instructorName).toEqual('Instructor Name');
    expect(component.instructorEmail).toEqual('instructor@example.com');
    expect(component.instructorInstitution).toEqual('');
    expect(component.instructorCountry).toEqual('Test Country');
    expect(component.instructorsConsolidated.length).toEqual(0);

    component.instructorInstitution = 'Instructor Institution';
    component.instructorCountry = '';

    button.click();

    expect(component.instructorsConsolidated.length).toEqual(0);

    expect(errorToastSpy).toHaveBeenCalledTimes(4);
    expect(errorToastSpy.mock.calls).toEqual([
      [SINGLE_INSTRUCTOR_REQUIRED_FIELDS_TOAST],
      [SINGLE_INSTRUCTOR_REQUIRED_FIELDS_TOAST],
      [SINGLE_INSTRUCTOR_REQUIRED_FIELDS_TOAST],
      [SINGLE_INSTRUCTOR_REQUIRED_FIELDS_TOAST],
    ]);
  });

  it.each([
    { field: 'name' as const, scenario: 'whitespace only' },
    { field: 'email' as const, scenario: 'whitespace only' },
    { field: 'institution' as const, scenario: 'whitespace only' },
    { field: 'country' as const, scenario: 'whitespace only' },
  ])('should not add one instructor when $field is $scenario', ({ field }) => {
    const statusMessageService: StatusMessageService = TestBed.inject(StatusMessageService);
    const errorToastSpy: jest.SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast');

    component.instructorName = 'Instructor Name';
    component.instructorEmail = 'instructor@example.com';
    component.instructorInstitution = 'Instructor Institution';
    component.instructorCountry = 'Test Country';

    const whitespaceOnly: string = '   ';
    if (field === 'name') {
      component.instructorName = whitespaceOnly;
    } else if (field === 'email') {
      component.instructorEmail = whitespaceOnly;
    } else if (field === 'institution') {
      component.instructorInstitution = whitespaceOnly;
    } else {
      component.instructorCountry = whitespaceOnly;
    }

    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor');
    button.click();

    expect(errorToastSpy).toHaveBeenCalledTimes(1);
    expect(errorToastSpy).toHaveBeenCalledWith(SINGLE_INSTRUCTOR_REQUIRED_FIELDS_TOAST);
    expect(component.instructorsConsolidated.length).toEqual(0);
  });

  it('should show error toast when bulk instructor details are empty', () => {
    const statusMessageService: StatusMessageService = TestBed.inject(StatusMessageService);
    const errorToastSpy: jest.SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast');

    component.instructorDetails = '   ';
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(errorToastSpy).toHaveBeenCalledWith(
        'Enter instructor details in the format: Name | Email | Institution | Country.');
    expect(component.instructorsConsolidated.length).toEqual(0);
  });

  it('should only add valid instructor details in the single line field', () => {
    const statusMessageService: StatusMessageService = TestBed.inject(StatusMessageService);
    const warningToastSpy: jest.SpyInstance = jest.spyOn(statusMessageService, 'showWarningToast');

    component.instructorDetails = [
        'Instructor A | instructora@example.com | Institution A | Country A',
        'Instructor B | instructorb@example.com',
        'Instructor C | | instructorc@example.com',
        'Instructor D | instructord@example.com | Institution D | Country D',
        '| instructore@example.com | Institution E',
    ].join('\n');
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(warningToastSpy).toHaveBeenCalledWith(
        '3 line(s) could not be added. Each line must include name, email, institution, and country separated by | or tab.');
    expect(component.instructorDetails).toEqual([
      'Instructor B | instructorb@example.com',
      'Instructor C | | instructorc@example.com',
      '| instructore@example.com | Institution E',
    ].join('\r\n'));
    expect(component.instructorsConsolidated.length).toEqual(2);
    expect(component.instructorsConsolidated[0]).toEqual({
      email: 'instructora@example.com',
      institution: 'Institution A',
      country: 'Country A',
      name: 'Instructor A',
      status: 'PENDING',
      isCurrentlyBeingEdited: false,
    });
    expect(component.instructorsConsolidated[1]).toEqual({
      email: 'instructord@example.com',
      institution: 'Institution D',
      country: 'Country D',
      name: 'Instructor D',
      status: 'PENDING',
      isCurrentlyBeingEdited: false,
    });
  });

  it('should remove instructor out of queue if REMOVE is requested', () => {
    const instructorData: InstructorData = {
      name: 'Instructor A',
      email: 'instructora@example.com',
      institution: 'Sample Institution A',
      country: 'Test Country',
      status: 'PENDING',
      isCurrentlyBeingEdited: false,
      joinLink: 'This should not be displayed',
      message: 'This should not be displayed',
    };
    component.instructorsConsolidated = [instructorData];
    fixture.detectChanges();

    const index: number = 0;
    component.removeInstructor(index);

    expect(component.instructorsConsolidated.includes(instructorData)).toBeFalsy();
    expect(component.instructorsConsolidated.length).toEqual(0);
  });

  it('should add instructor and update field when successful', () => {
    component.instructorsConsolidated = [
      {
        name: 'Instructor A',
        email: 'instructora@example.com',
        institution: 'Sample Institution A',
        country: 'Singapore',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'This should not be displayed',
      },
    ];
    jest.spyOn(accountService, 'createAccountRequest').mockReturnValue(of({
      id: 'some.person@example.com%NUS',
      email: 'some.person@example.com',
      name: 'Some Person',
      institute: 'NUS',
      country: 'Singapore',
      status: AccountRequestStatus.APPROVED,
      registrationKey: 'registrationKey',
      createdAt: 528,
    }));
    jest.spyOn(linkService, 'generateAccountRegistrationLink')
        .mockReturnValue('http://localhost:4200/web/join?iscreatingaccount=true&key=registrationKey');
    fixture.detectChanges();

    const index: number = 0;
    component.addInstructor(index);

    expect(component.instructorsConsolidated[index].status).toEqual('SUCCESS');
    expect(component.instructorsConsolidated[index].joinLink)
        .toEqual('http://localhost:4200/web/join?iscreatingaccount=true&key=registrationKey');
    expect(component.activeRequests).toEqual(0);
  });

  it('should not add instructor and update field during failure', () => {
    component.instructorsConsolidated = [
      {
        name: 'Instructor A',
        email: 'instructora@example.com',
        institution: 'Sample Institution A',
        country: 'Singapore',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'This should not be displayed',
      },
    ];
    jest.spyOn(accountService, 'createAccountRequest').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message',
      },
    })));
    fixture.detectChanges();

    const index: number = 0;
    component.addInstructor(index);

    expect(component.instructorsConsolidated[index].status).toEqual('FAIL');
    expect(component.instructorsConsolidated[index].message).toEqual('This is the error message');
    expect(component.activeRequests).toEqual(0);
  });

  it('should enter edit mode for only the specified instructor', () => {
    component.instructorsConsolidated = [
      {
        name: 'Instructor A',
        email: 'instructora@example.com',
        institution: 'Sample Institution A',
        country: 'Test Country',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'This should not be displayed',
      },
      {
        name: 'Instructor B',
        email: 'instructorb@example.com',
        institution: 'Sample Institution B',
        country: 'Test Country',
        status: 'SUCCESS',
        statusCode: 200,
        isCurrentlyBeingEdited: false,
        joinLink: 'http://localhost:4200/web/join',
        message: 'This should not be displayed',
      },
      {
        name: 'Instructor C',
        email: 'instructorc@example.com',
        institution: 'Sample Institution C',
        country: 'Test Country',
        status: 'FAIL',
        statusCode: 400,
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'The instructor cannot be added for some reason',
      },
    ];

    const index: number = 2;
    component.setInstructorRowEditModeEnabled(index, true);

    for (let i: number = 0; i < component.instructorsConsolidated.length; i += 1) {
      expect(component.instructorsConsolidated[i].isCurrentlyBeingEdited).toEqual(i === index);
    }
  });

  it('should exit edit mode for only the specified instructor', () => {
    component.instructorsConsolidated = [
      {
        name: 'Instructor A',
        email: 'instructora@example.com',
        institution: 'Sample Institution A',
        country: 'Test Country',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'This should not be displayed',
      },
      {
        name: 'Instructor B',
        email: 'instructorb@example.com',
        institution: 'Sample Institution B',
        country: 'Test Country',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'This should not be displayed',
      },
      {
        name: 'Instructor C',
        email: 'instructorc@example.com',
        institution: 'Sample Institution C',
        country: 'Test Country',
        status: 'FAIL',
        statusCode: 400,
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'The instructor cannot be added for some reason',
      },
    ];
    for (let i: number = 0; i < component.instructorsConsolidated.length; i += 1) {
      component.setInstructorRowEditModeEnabled(i, true);
    }
    fixture.detectChanges();

    const index: number = 1;
    component.setInstructorRowEditModeEnabled(index, false);

    for (let i: number = 0; i < component.instructorsConsolidated.length; i += 1) {
      expect(component.instructorsConsolidated[i].isCurrentlyBeingEdited).toEqual(i !== index);
    }
  });

  it('should add all instructors when prompted', () => {
    component.instructorsConsolidated = [
      {
        name: 'Instructor A',
        email: 'instructora@example.com',
        institution: 'Sample Institution A',
        country: 'Test Country',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'This should not be displayed',
      },
      {
        name: 'Instructor B',
        email: 'instructorb@example.com',
        institution: 'Sample Institution B',
        country: 'Test Country',
        status: 'SUCCESS',
        statusCode: 200,
        isCurrentlyBeingEdited: false,
        joinLink: 'http://localhost:4200/web/join',
        message: 'This should not be displayed',
      },
      {
        name: 'Instructor C',
        email: 'instructorc@example.com',
        institution: 'Sample Institution C',
        country: 'Test Country',
        status: 'FAIL',
        statusCode: 400,
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'The instructor cannot be added for some reason',
      },
    ];
    // No need to spy here as this test only tests the number of active requests added
    // Testing of adding individual instructors have been done before
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-all-instructors');
    button.click();

    expect(component.instructorsConsolidated[0].status).toEqual('ADDING');
    expect(component.instructorsConsolidated[1].status).toEqual('SUCCESS');
    expect(component.instructorsConsolidated[2].status).toEqual('ADDING');
    expect(component.activeRequests).toEqual(2);
  });

  it('should add only instructors that are not currently in edit mode when trying to add all', () => {
    component.instructorsConsolidated = [
      {
        name: 'Instructor A',
        email: 'instructora@example.com',
        institution: 'Sample Institution A',
        country: 'Test Country',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'This should not be displayed',
      },
      {
        name: 'Instructor B',
        email: 'instructorb@example.com',
        institution: 'Sample Institution B',
        country: 'Test Country',
        status: 'PENDING',
        isCurrentlyBeingEdited: true,
        joinLink: 'This should not be displayed',
        message: 'This should not be displayed',
      },
      {
        name: 'Instructor C',
        email: 'instructorc@example.com',
        institution: 'Sample Institution C',
        country: 'Test Country',
        status: 'FAIL',
        statusCode: 400,
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'The instructor cannot be added for some reason',
      },
    ];
    fixture.detectChanges();

    const addAllButton: any = fixture.debugElement.nativeElement.querySelector('#add-all-instructors');
    addAllButton.click();

    expect(component.instructorsConsolidated[0].status).toEqual('ADDING');
    expect(component.instructorsConsolidated[1].status).toEqual('PENDING');
    expect(component.instructorsConsolidated[2].status).toEqual('ADDING');
    expect(component.activeRequests).toEqual(2);
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with some instructors details', () => {
    component.instructorsConsolidated = [
      {
        name: 'Instructor A',
        email: 'instructora@example.com',
        institution: 'Sample Institution A',
        country: 'Test Country',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'This should not be displayed',
      },
      {
        name: 'Instructor B',
        email: 'instructorb@example.com',
        institution: 'Sample Institution B',
        country: 'Test Country',
        status: 'SUCCESS',
        statusCode: 200,
        isCurrentlyBeingEdited: false,
        joinLink: 'http://localhost:4200/web/join',
        message: 'This should not be displayed',
      },
      {
        name: 'Instructor C',
        email: 'instructorc@example.com',
        institution: 'Sample Institution C',
        country: 'Test Country',
        status: 'FAIL',
        statusCode: 400,
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'The instructor cannot be added for some reason',
      },
    ];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with disabled adding instructor button if there are active requests', () => {
    component.instructorsConsolidated = [
      {
        name: 'Instructor A',
        email: 'instructora@example.com',
        institution: 'Sample Institution A',
        country: 'Test Country',
        status: 'ADDING',
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'This should not be displayed',
      },
      {
        name: 'Instructor B',
        email: 'instructorb@example.com',
        institution: 'Sample Institution B',
        country: 'Test Country',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
        joinLink: 'This should not be displayed',
        message: 'This should not be displayed',
      },
    ];
    component.activeRequests = 1;
    component.isAddingInstructors = true;

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should add multiple instructors split by tabs', () => {
    component.instructorDetails = `Instructor A   \t  instructora@example.com \t  Sample Institution A \t Test Country\n
     Instructor B \t instructorb@example.com \t Sample Institution B \t Test Country`;

    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(component.instructorsConsolidated.length).toEqual(2);
    expect(component.instructorsConsolidated[0]).toEqual(
      {
        name: 'Instructor A',
        email: 'instructora@example.com',
        institution: 'Sample Institution A',
        country: 'Test Country',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
      },
    );
    expect(component.instructorsConsolidated[1]).toEqual(
      {
        name: 'Instructor B',
        email: 'instructorb@example.com',
        institution: 'Sample Institution B',
        country: 'Test Country',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
      },
    );
  });

  it('should add multiple instructors split by vertical bars', () => {
    component.instructorDetails = `Instructor A | instructora@example.com | Sample Institution A | Test Country\n
        Instructor B | instructorb@example.com | Sample Institution B | Test Country`;

    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(component.instructorsConsolidated.length).toEqual(2);
    expect(component.instructorsConsolidated[0]).toEqual(
      {
        name: 'Instructor A',
        email: 'instructora@example.com',
        institution: 'Sample Institution A',
        country: 'Test Country',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
      },
    );
    expect(component.instructorsConsolidated[1]).toEqual(
      {
        name: 'Instructor B',
        email: 'instructorb@example.com',
        institution: 'Sample Institution B',
        country: 'Test Country',
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
      },
    );
  });
});
