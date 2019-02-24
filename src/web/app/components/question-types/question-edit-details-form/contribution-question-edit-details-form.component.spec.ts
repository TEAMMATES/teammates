import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { ContributionQuestionEditDetailsFormComponent } from './contribution-question-edit-details-form.component';

describe('ContributionQuestionEditDetailsFormComponent', () => {
  let component: ContributionQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<ContributionQuestionEditDetailsFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [ContributionQuestionEditDetailsFormComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
