import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { ContributionQuestionDetailsFormComponent } from './contribution-question-details-form.component';

describe('ContributionQuestionDetailsFormComponent', () => {
  let component: ContributionQuestionDetailsFormComponent;
  let fixture: ComponentFixture<ContributionQuestionDetailsFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [ContributionQuestionDetailsFormComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
