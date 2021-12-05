import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { InstructorSearchPageModule } from '../instructor-search-page.module';
import { StudentResultTableComponent } from './student-result-table.component';

describe('StudentResultTableComponent', () => {
  let component: StudentResultTableComponent;
  let fixture: ComponentFixture<StudentResultTableComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [InstructorSearchPageModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentResultTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
