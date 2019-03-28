import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MsqFieldComponent } from './msq-field/msq-field.component';
import { MsqQuestionEditDetailsFormComponent } from './msq-question-edit-details-form.component';

describe('MsqQuestionEditDetailsFormComponent', () => {
  let component: MsqQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<MsqQuestionEditDetailsFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        MsqQuestionEditDetailsFormComponent,
        MsqFieldComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
