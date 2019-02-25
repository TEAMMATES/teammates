import { config } from './config';

/**
 * Environment variables for production mode.
 */
export const environment: any = {
  ...config,
  production: true,
  backendUrl: '',
  frontendUrl: '',
  withCredentials: false,
};
