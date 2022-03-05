import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationsTableComponent } from './notifications-table.component';

describe('NotificationsTableComponent', () => {
  let component: NotificationsTableComponent;
  let fixture: ComponentFixture<NotificationsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotificationsTableComponent],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
