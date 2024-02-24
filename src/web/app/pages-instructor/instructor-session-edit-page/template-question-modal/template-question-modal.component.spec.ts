import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TemplateQuestionModalComponent } from './template-question-modal.component';
import { QuestionEditFormModule } from '../../../components/question-edit-form/question-edit-form.module';
import { TeammatesCommonModule } from '../../../components/teammates-common/teammates-common.module';

describe('TemplateQuestionModalComponent', () => {
  let component: TemplateQuestionModalComponent;
  let fixture: ComponentFixture<TemplateQuestionModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        CommonModule,
        FormsModule,
        QuestionEditFormModule,
        TeammatesCommonModule,
        HttpClientTestingModule,
        NgbModule,
      ],
      declarations: [
        TemplateQuestionModalComponent,
      ],
      providers: [
        NgbActiveModal,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TemplateQuestionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
