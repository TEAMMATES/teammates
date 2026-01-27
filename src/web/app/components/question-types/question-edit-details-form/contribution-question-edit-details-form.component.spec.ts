import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ContributionQuestionEditDetailsFormComponent } from './contribution-question-edit-details-form.component';
import { TeammatesRouterModule } from '../../teammates-router/teammates-router.module';

describe('ContributionQuestionEditDetailsFormComponent', () => {
  let component: ContributionQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<ContributionQuestionEditDetailsFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        RouterModule.forRoot([]),
        TeammatesRouterModule,
      ],
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
