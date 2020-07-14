import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { InstructorStudentListPageComponent } from './instructor-student-list-page.component';
import { InstructorStudentListPageModule } from './instructor-student-list-page.module';

describe('InstructorStudentListPageComponent', () => {
  let component: InstructorStudentListPageComponent;
  let fixture: ComponentFixture<InstructorStudentListPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule,
        InstructorStudentListPageModule,
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
