import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  ConstsumRecipientsQuestionAdditionalInfoComponent,
} from './constsum-recipients-question-additional-info.component';

describe('ConstsumRecipientsQuestionAdditionalInfoComponent', () => {
  let component: ConstsumRecipientsQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<ConstsumRecipientsQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumRecipientsQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumRecipientsQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
