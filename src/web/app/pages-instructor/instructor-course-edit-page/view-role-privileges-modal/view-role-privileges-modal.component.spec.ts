import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { InstructorRoleTitlePipe } from '../instructor-edit-panel/instructor-role-title.pipe';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal.component';

describe('ViewRolePrivilegesModalComponent', () => {
  let component: ViewRolePrivilegesModalComponent;
  let fixture: ComponentFixture<ViewRolePrivilegesModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ViewRolePrivilegesModalComponent,
        InstructorRoleTitlePipe,
      ],
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
