import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RankRecipientsQuestionEditDetailsFormComponent } from './rank-recipients-question-edit-details-form.component';

describe('RankRecipientsQuestionEditDetailsFormComponent', () => {
  let component: RankRecipientsQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionEditDetailsFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(RankRecipientsQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
