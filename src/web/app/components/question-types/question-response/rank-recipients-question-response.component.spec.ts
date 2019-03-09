import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RankRecipientsQuestionResponseComponent } from './rank-recipients-question-response.component';

describe('RankRecipientsQuestionResponseComponent', () => {
  let component: RankRecipientsQuestionResponseComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionResponseComponent>;

  beforeEach(async(() => {
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
