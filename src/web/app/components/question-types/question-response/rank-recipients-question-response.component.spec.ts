import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RankRecipientsQuestionResponseComponent } from './rank-recipients-question-response.component';

describe('RankRecipientsQuestionResponseComponent', () => {
  let component: RankRecipientsQuestionResponseComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionResponseComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RankRecipientsQuestionResponseComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankRecipientsQuestionResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
