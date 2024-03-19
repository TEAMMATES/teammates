import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorRequestFormComponent } from './instructor-request-form.component';

describe('InstructorRequestFormComponent', () => {
  let component: InstructorRequestFormComponent;
  let fixture: ComponentFixture<InstructorRequestFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorRequestFormComponent]
    });
    fixture = TestBed.createComponent(InstructorRequestFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
