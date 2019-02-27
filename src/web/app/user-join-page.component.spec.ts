import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { UserJoinPageComponent } from './user-join-page.component';

describe('UserJoinPageComponent', () => {
  let component: UserJoinPageComponent;
  let fixture: ComponentFixture<UserJoinPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserJoinPageComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserJoinPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
