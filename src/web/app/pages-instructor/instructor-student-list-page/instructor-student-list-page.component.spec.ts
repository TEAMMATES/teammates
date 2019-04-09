import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { InstructorStudentListPageComponent } from './instructor-student-list-page.component';

@Component({ selector: 'tm-student-list', template: '' })
class StudentListStubComponent {
  @Input() courseId: string = '';
  @Input() useGrayHeading: boolean = true;
  @Input() sections: Object[] = [];
  @Input() listOfStudentsToHide: string[] = [];
  @Input() isHideTableHead: boolean = false;
  @Input() enableRemindButton: boolean = false;
}

describe('InstructorStudentListPageComponent', () => {
  let component: InstructorStudentListPageComponent;
  let fixture: ComponentFixture<InstructorStudentListPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorStudentListPageComponent,
        StudentListStubComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule,
        MatSnackBarModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorStudentListPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
