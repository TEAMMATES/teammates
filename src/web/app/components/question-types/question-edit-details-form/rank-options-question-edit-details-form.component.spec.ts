import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RankOptionsQuestionEditDetailsFormComponent } from './rank-options-question-edit-details-form.component';

describe('RankOptionsQuestionEditDetailsFormComponent', () => {
  let component: RankOptionsQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<RankOptionsQuestionEditDetailsFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
