import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { TextRecipientSubmissionFormComponent } from './text-recipient-submission-form.component';

describe('TextRecipientSubmissionFormComponent', () => {
  let component: TextRecipientSubmissionFormComponent;
  let fixture: ComponentFixture<TextRecipientSubmissionFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TextRecipientSubmissionFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TextRecipientSubmissionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
