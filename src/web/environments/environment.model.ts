export interface AppConfig {
  supportEmail: string;
  captchaSiteKey: string;
  maintenance: boolean;
}

export interface Environment extends AppConfig {
  version: string;
  production: boolean;
  backendUrl: string;
  frontendUrl: string;
  withCredentials: boolean;
}
