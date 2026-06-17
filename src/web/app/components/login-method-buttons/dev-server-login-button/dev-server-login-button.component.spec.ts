import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DevServerLoginButtonComponent } from './dev-server-login-button.component';

describe('DevServerLoginButtonComponent', () => {
  let component: DevServerLoginButtonComponent;
  let fixture: ComponentFixture<DevServerLoginButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({}).compileComponents();

    fixture = TestBed.createComponent(DevServerLoginButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit login event when clicked', () => {
    const loginSpy = vi.spyOn(component.login, 'emit');

    fixture.nativeElement.querySelector('button').click();

    expect(loginSpy).toHaveBeenCalledOnce();
  });
});
