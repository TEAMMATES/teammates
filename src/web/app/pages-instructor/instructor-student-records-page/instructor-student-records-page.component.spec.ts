import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { InstructorStudentRecordsPageComponent } from './instructor-student-records-page.component';

describe('InstructorStudentRecordsPageComponent', () => {
  let component: InstructorStudentRecordsPageComponent;
  let fixture: ComponentFixture<InstructorStudentRecordsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorStudentRecordsPageComponent],
      imports: [RouterTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorStudentRecordsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
