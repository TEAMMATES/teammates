import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ViewPrivilegesModalComponent } from './view-privileges-modal.component';

describe('ViewPrivilegesModalComponent', () => {
  let component: ViewPrivilegesModalComponent;
  let fixture: ComponentFixture<ViewPrivilegesModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ViewPrivilegesModalComponent],
      imports: [FormsModule],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewPrivilegesModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
