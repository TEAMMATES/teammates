import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { InstructorData } from './instructor-data';

import { NewInstructorDataRowComponent } from './new-instructor-data-row.component';

describe('NewInstructorDataRowComponent', () => {
  let component: NewInstructorDataRowComponent;
  let fixture: ComponentFixture<NewInstructorDataRowComponent>;
  let expectedInstructorData: InstructorData;
  let expectedIndex: number;
  let expectedActiveRequests: number;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [NewInstructorDataRowComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewInstructorDataRowComponent);
    component = fixture.componentInstance;
    expectedInstructorData = {
      name: 'Instructor',
      email: 'instructor@instruct.or',
      institution: 'Institutional Institution of Institute',
      status: 'PENDING',
    };
    expectedIndex = 0;
    expectedActiveRequests = 0;
    component.instructor = expectedInstructorData;
    component.index = expectedIndex;
    component.activeRequests = expectedActiveRequests;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
