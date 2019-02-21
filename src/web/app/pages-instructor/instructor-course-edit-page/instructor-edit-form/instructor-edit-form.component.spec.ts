import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorEditFormComponent } from './instructor-edit-form.component';

describe('InstructorEditFormComponent', () => {
  let component: InstructorEditFormComponent;
  let fixture: ComponentFixture<InstructorEditFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstructorEditFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorEditFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
