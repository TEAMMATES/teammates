import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RankRecipientsQuestionAdditionalInfoComponent } from './rank-recipients-question-additional-info.component';

describe('RankRecipientsQuestionAdditionalInfoComponent', () => {
  let component: RankRecipientsQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RankRecipientsQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankRecipientsQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
