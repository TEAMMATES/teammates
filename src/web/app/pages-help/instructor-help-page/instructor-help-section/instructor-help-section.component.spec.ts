import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorHelpSectionComponent } from './instructor-help-section.component';

describe('InstructorHelpSectionComponent', () => {
  let component: InstructorHelpSectionComponent;
  let fixture: ComponentFixture<InstructorHelpSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorHelpSectionComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
