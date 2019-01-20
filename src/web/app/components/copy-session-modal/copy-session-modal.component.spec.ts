import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CopySessionModalComponent } from './copy-session-modal.component';

describe('CopySessionModalComponent', () => {
  let component: CopySessionModalComponent;
  let fixture: ComponentFixture<CopySessionModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CopySessionModalComponent],
      imports: [
        FormsModule,
      ],
      providers: [
        NgbActiveModal,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CopySessionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
