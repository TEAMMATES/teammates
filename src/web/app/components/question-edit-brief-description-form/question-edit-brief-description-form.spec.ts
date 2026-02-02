import { ComponentFixture, TestBed } from '@angular/core/testing';
import { QuestionEditBriefDescriptionFormComponent } from './question-edit-brief-description-form.component';

describe('QuestionEditBriefDescriptionFormComponent', () => {
  let component: QuestionEditBriefDescriptionFormComponent;
  let fixture: ComponentFixture<QuestionEditBriefDescriptionFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionEditBriefDescriptionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
