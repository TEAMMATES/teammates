import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RankOptionsQuestionAdditionalInfoComponent } from './rank-options-question-additional-info.component';

describe('RankOptionsQuestionAdditionalInfoComponent', () => {
  let component: RankOptionsQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<RankOptionsQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RankOptionsQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
