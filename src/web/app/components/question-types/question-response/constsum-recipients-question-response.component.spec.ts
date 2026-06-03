import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstsumRecipientsQuestionResponseComponent } from './constsum-recipients-question-response.component';

describe('ConstsumRecipientsQuestionResponseComponent', () => {
  let component: ConstsumRecipientsQuestionResponseComponent;
  let fixture: ComponentFixture<ConstsumRecipientsQuestionResponseComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumRecipientsQuestionResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
