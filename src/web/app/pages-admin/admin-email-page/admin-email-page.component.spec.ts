import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminEmailPageComponent } from './admin-email-page.component';

describe('AdminEmailPageComponent', () => {
  let component: AdminEmailPageComponent;
  let fixture: ComponentFixture<AdminEmailPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AdminEmailPageComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminEmailPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
