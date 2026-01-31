import { ComponentFixture, TestBed } from '@angular/core/testing';

import {
  ConstsumRecipientsQuestionAdditionalInfoComponent,
} from './constsum-recipients-question-additional-info.component';

describe('ConstsumRecipientsQuestionAdditionalInfoComponent', () => {
  let component: ConstsumRecipientsQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<ConstsumRecipientsQuestionAdditionalInfoComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumRecipientsQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
