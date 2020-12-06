import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TeammatesRouterModule } from '../../../components/teammates-router/teammates-router.module';
import { ResponseModerationButtonComponent } from './response-moderation-button.component';

describe('ResponseModerationButtonComponent', () => {
  let component: ResponseModerationButtonComponent;
  let fixture: ComponentFixture<ResponseModerationButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ResponseModerationButtonComponent],
      imports: [RouterTestingModule.withRoutes([]), TeammatesRouterModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResponseModerationButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
