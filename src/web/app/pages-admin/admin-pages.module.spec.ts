import { AdminPagesModule } from './admin-pages.module';

describe('AdminPagesModule', () => {
  let adminPagesModule: AdminPagesModule;

  beforeEach(() => {
    adminPagesModule = new AdminPagesModule();
  });

  it('should create an instance', () => {
    expect(adminPagesModule).toBeTruthy();
  });
});
