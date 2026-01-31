import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentResultTableComponent } from './student-result-table.component';

describe('StudentResultTableComponent', () => {
  let component: StudentResultTableComponent;
  let fixture: ComponentFixture<StudentResultTableComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentResultTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
