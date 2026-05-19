import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal.component';

describe('ViewRolePrivilegesModalComponent', () => {
  let component: ViewRolePrivilegesModalComponent;
  let fixture: ComponentFixture<ViewRolePrivilegesModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [NgbActiveModal],
    }).compileComponents();

    fixture = TestBed.createComponent(ViewRolePrivilegesModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
