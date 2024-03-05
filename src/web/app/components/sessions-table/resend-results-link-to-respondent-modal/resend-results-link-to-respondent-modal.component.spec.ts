import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ResendResultsLinkToRespondentModalComponent } from './resend-results-link-to-respondent-modal.component';
import { createBuilder } from '../../../../test-helpers/generic-builder';
import { StudentListInfoTableRowModel, InstructorListInfoTableRowModel }
  from '../respondent-list-info-table/respondent-list-info-table-model';
import { RespondentListInfoTableComponent } from '../respondent-list-info-table/respondent-list-info-table.component';

@Component({ selector: 'tm-ajax-preload', template: '' })
class AjaxPreloadComponent {}

describe('ResendResultsLinkToRespondentModalComponent', () => {
  let component: ResendResultsLinkToRespondentModalComponent;
  let fixture: ComponentFixture<ResendResultsLinkToRespondentModalComponent>;

  const studentModelBuilder = createBuilder<StudentListInfoTableRowModel>({
    email: 'student@gmail.com',
    name: 'Student',
    teamName: 'Team A',
    sectionName: 'Section 1',
    hasSubmittedSession: false,
    isSelected: false,
  });

  const instructorModelBuilder = createBuilder<InstructorListInfoTableRowModel>({
    email: 'instructor@gmail.com',
    name: 'Instructor',
    hasSubmittedSession: false,
    isSelected: false,
  });

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ResendResultsLinkToRespondentModalComponent,
        AjaxPreloadComponent,
        RespondentListInfoTableComponent,
      ],
      imports: [
        HttpClientTestingModule,
        FormsModule,
      ],
      providers: [NgbActiveModal],
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
      studentModelBuilder.email('student1@gmail.com').isSelected(true).build(),
      studentModelBuilder.email('student2@gmail.com').isSelected(false).build(),
      studentModelBuilder.email('student3@gmail.com').isSelected(true).build(),
    ];

    const instructorListInfoTableRowModels: InstructorListInfoTableRowModel[] = [
      instructorModelBuilder.email('instructor1@gmail.com').isSelected(false).build(),
      instructorModelBuilder.email('instructor2@gmail.com').isSelected(true).build(),
    ];

    const expectedModels = [
      studentModelBuilder.email('student1@gmail.com').isSelected(true).build(),
      studentModelBuilder.email('student3@gmail.com').isSelected(true).build(),
      instructorModelBuilder.email('instructor2@gmail.com').isSelected(true).build(),
    ];

    component.studentListInfoTableRowModels = studentListInfoTableRowModels;
    component.instructorListInfoTableRowModels = instructorListInfoTableRowModels;

    const result = component.collateRespondentsToSendHandler();

    expect(result).toEqual(expectedModels);
  });
});
