import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal.component';

describe('ViewRolePrivilegesModalComponent', () => {
  let component: ViewRolePrivilegesModalComponent;
  let fixture: ComponentFixture<ViewRolePrivilegesModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewRolePrivilegesModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
