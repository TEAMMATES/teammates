import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CustomPrivilegeSettingPanelComponent } from './custom-privilege-setting-panel.component';

describe('CustomPrivilegeSettingPanelComponent', () => {
  let component: CustomPrivilegeSettingPanelComponent;
  let fixture: ComponentFixture<CustomPrivilegeSettingPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CustomPrivilegeSettingPanelComponent],
      providers: [NgbActiveModal],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomPrivilegeSettingPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
