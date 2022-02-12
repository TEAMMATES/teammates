import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { LoadingRetryModule } from '../../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../../components/panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../../../components/teammates-common/teammates-common.module';
import { CopyQuestionsFromOtherSessionsModalComponent } from './copy-questions-from-other-sessions-modal.component';

describe('CopyQuestionsFromOtherSessionsModalComponent', () => {
  let component: CopyQuestionsFromOtherSessionsModalComponent;
  let fixture: ComponentFixture<CopyQuestionsFromOtherSessionsModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CopyQuestionsFromOtherSessionsModalComponent],
      imports: [
        CommonModule,
        FormsModule,
        TeammatesCommonModule,
        PanelChevronModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        HttpClientTestingModule,
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
