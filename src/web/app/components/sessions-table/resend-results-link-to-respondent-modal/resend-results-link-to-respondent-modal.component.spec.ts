import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ResendResultsLinkToRespondentModalComponent } from './resend-results-link-to-respondent-modal.component';
import { createBuilder } from '../../../../test-helpers/generic-builder';
import {
  StudentListInfoTableRowModel,
  InstructorListInfoTableRowModel,
} from '../respondent-list-info-table/respondent-list-info-table-model';

describe('ResendResultsLinkToRespondentModalComponent', () => {
  let component: ResendResultsLinkToRespondentModalComponent;
  let fixture: ComponentFixture<ResendResultsLinkToRespondentModalComponent>;

  const studentModelBuilder = createBuilder<StudentListInfoTableRowModel>({
    id: 'ee47e471-fbd8-478e-a350-51152802215b',
    email: 'student@gmail.com',
    name: 'Student',
    teamName: 'Team A',
    sectionName: 'Section 1',
    hasSubmittedSession: false,
    isSelected: false,
  });

  const instructorModelBuilder = createBuilder<InstructorListInfoTableRowModel>({
    id: '71a1ceac-d24a-4f34-9593-e25ecbeff847',
    email: 'instructor@gmail.com',
    name: 'Instructor',
    hasSubmittedSession: false,
    isSelected: false,
  });

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [
        NgbActiveModal,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResendResultsLinkToRespondentModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('collateRespondentsToSendHandler: should return an array containing only selected rows'
    + 'from student and instructor lists', () => {
    const studentListInfoTableRowModels: StudentListInfoTableRowModel[] = [
      studentModelBuilder.id('ddd32f6b-e86e-42a1-868b-0e9da107fce6').email('student1@gmail.com').isSelected(true).build(),
      studentModelBuilder.id('94e6a227-c731-482b-bd8d-acdbc74e3e53').email('student2@gmail.com').isSelected(false).build(),
      studentModelBuilder.id('c0bb4887-99c0-46fb-9bb2-b8ed8305021c').email('student3@gmail.com').isSelected(true).build(),
    ];

    const instructorListInfoTableRowModels: InstructorListInfoTableRowModel[] = [
      instructorModelBuilder.id('95404edf-53f6-4f93-9100-ac82beb651ea').email('instructor1@gmail.com').isSelected(false).build(),
      instructorModelBuilder.id('246f2052-1c08-48e7-b0cd-a4c052d0dda1').email('instructor2@gmail.com').isSelected(true).build(),
    ];

    const expectedModels = [
      studentModelBuilder.id('61aa0796-9513-49ae-9c48-eac426625b04').email('student1@gmail.com').isSelected(true).build(),
      studentModelBuilder.id('446f0b3f-d8f2-4afa-95bd-f703f5f3db5e').email('student3@gmail.com').isSelected(true).build(),
      instructorModelBuilder.id('24a0b376-0bd7-4ca4-8ed9-3f362b7176bd').email('instructor2@gmail.com').isSelected(true).build(),
    ];

    component.studentListInfoTableRowModels = studentListInfoTableRowModels;
    component.instructorListInfoTableRowModels = instructorListInfoTableRowModels;

    const result = component.collateRespondentsToSendHandler();

    expect(result).toEqual(expectedModels);
  });
});
