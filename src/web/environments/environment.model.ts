export interface AppConfig {
  captchaSiteKey: string;
  maintenance: boolean;
}

export interface Environment extends AppConfig {
  version: string;
  production: boolean;
  backendUrl: string;
  withCredentials: boolean;
}
