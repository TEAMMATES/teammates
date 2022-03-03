import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RankRecipientsQuestionConstraintComponent } from './rank-recipients-question-constraint.component';

describe('RankRecipientsQuestionConstraintComponent', () => {
  let component: RankRecipientsQuestionConstraintComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionConstraintComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RankRecipientsQuestionConstraintComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankRecipientsQuestionConstraintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
