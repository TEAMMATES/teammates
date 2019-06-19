import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentListInfoTableComponent } from './student-list-info-table.component';

describe('StudentListInfoTableComponent', () => {
  let component: StudentListInfoTableComponent;
  let fixture: ComponentFixture<StudentListInfoTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [StudentListInfoTableComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentListInfoTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
