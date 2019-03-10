import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Component, Input } from '@angular/core';
import { StudentResultTableComponent } from './student-result-table.component';

@Component({ selector: 'tm-student-list', template: '' })
class StudentListStubComponent {
  @Input() courseId: string = '';
  @Input() useGrayHeading: boolean = true;
  @Input() sections: Object[] = [];
  @Input() enableRemindButton: boolean = false;
}

describe('StudentResultTableComponent', () => {
  let component: StudentResultTableComponent;
  let fixture: ComponentFixture<StudentResultTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        StudentResultTableComponent,
        StudentListStubComponent,
      ],
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
