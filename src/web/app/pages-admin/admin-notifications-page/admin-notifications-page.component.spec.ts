import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminNotificationsPageComponent } from './admin-notifications-page.component';
import { AdminNotificationsPageModule } from './admin-notifications-page.module';

describe('AdminNotificationsPageComponent', () => {
  let component: AdminNotificationsPageComponent;
  let fixture: ComponentFixture<AdminNotificationsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminNotificationsPageModule],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminNotificationsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
