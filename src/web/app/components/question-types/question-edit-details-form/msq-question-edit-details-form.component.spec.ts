import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MsqQuestionEditDetailsFormComponent } from './msq-question-edit-details-form.component';

describe('MsqQuestionEditDetailsFormComponent', () => {
  let component: MsqQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<MsqQuestionEditDetailsFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
