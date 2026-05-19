import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { CustomPrivilegeSettingPanelComponent } from './custom-privilege-setting-panel.component';

describe('CustomPrivilegeSettingPanelComponent', () => {
  let component: CustomPrivilegeSettingPanelComponent;
  let fixture: ComponentFixture<CustomPrivilegeSettingPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [NgbActiveModal],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomPrivilegeSettingPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
