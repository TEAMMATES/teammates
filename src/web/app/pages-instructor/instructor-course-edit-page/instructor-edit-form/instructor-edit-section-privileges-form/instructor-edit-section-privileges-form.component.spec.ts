import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorEditSectionPrivilegesFormComponent } from './instructor-edit-section-privileges-form.component';

describe('InstructorEditSectionPrivilegesFormComponent', () => {
  let component: InstructorEditSectionPrivilegesFormComponent;
  let fixture: ComponentFixture<InstructorEditSectionPrivilegesFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstructorEditSectionPrivilegesFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorEditSectionPrivilegesFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
