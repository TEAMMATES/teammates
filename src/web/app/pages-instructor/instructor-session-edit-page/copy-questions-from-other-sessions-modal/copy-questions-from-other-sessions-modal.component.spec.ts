import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TeammatesCommonModule } from '../../../components/teammates-common/teammates-common.module';
import { CopyQuestionsFromOtherSessionsModalComponent } from './copy-questions-from-other-sessions-modal.component';

describe('CopyQuestionsFromOtherSessionsModalComponent', () => {
  let component: CopyQuestionsFromOtherSessionsModalComponent;
  let fixture: ComponentFixture<CopyQuestionsFromOtherSessionsModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CopyQuestionsFromOtherSessionsModalComponent],
      imports: [
        CommonModule,
        FormsModule,
        TeammatesCommonModule,
      ],
      providers: [
        NgbActiveModal,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyQuestionsFromOtherSessionsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
