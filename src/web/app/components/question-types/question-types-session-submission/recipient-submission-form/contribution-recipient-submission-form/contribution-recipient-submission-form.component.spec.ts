import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { ContributionPointDescriptionPipe } from './contribution-point-description.pipe';
import { ContributionRecipientSubmissionFormComponent } from './contribution-recipient-submission-form.component';

describe('ContributionRecipientSubmissionFormComponent', () => {
  let component: ContributionRecipientSubmissionFormComponent;
  let fixture: ComponentFixture<ContributionRecipientSubmissionFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ContributionRecipientSubmissionFormComponent,
        ContributionPointDescriptionPipe,
      ],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionRecipientSubmissionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
