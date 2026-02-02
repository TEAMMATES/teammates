import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentViewResponsesComponent } from './student-view-responses.component';

describe('StudentViewResponsesComponent', () => {
  let component: StudentViewResponsesComponent;
  let fixture: ComponentFixture<StudentViewResponsesComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentViewResponsesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
