import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NumScaleQuestionEditDetailsFormComponent } from './num-scale-question-edit-details-form.component';

describe('NumScaleQuestionEditDetailsFormComponent', () => {
  let component: NumScaleQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<NumScaleQuestionEditDetailsFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [NumScaleQuestionEditDetailsFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
