import { InjectionToken } from '@angular/core';

export interface LoginMethodButtonContext {
  readonly nextUrl: string;
}

export const LOGIN_METHOD_BUTTON_CONTEXT = new InjectionToken<LoginMethodButtonContext>('LOGIN_METHOD_BUTTON_CONTEXT');
