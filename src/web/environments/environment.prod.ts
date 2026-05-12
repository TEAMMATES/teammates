import { APPLICATION_VERSION } from './application-version';
import { config } from './config';
import { Environment } from './environment.model';

/**
 * Environment variables for production mode.
 */
export const environment: Environment = {
  ...config,
  version: APPLICATION_VERSION,
  production: true,
  backendUrl: '',
  frontendUrl: '',
  withCredentials: false,
};
