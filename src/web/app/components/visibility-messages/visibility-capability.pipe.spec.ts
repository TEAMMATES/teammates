import { VisibilityCapabilityPipe } from './visibility-capability.pipe';

describe('VisibilityCapabilityPipe', () => {
  it('create an instance', () => {
    const pipe: VisibilityCapabilityPipe = new VisibilityCapabilityPipe();
    expect(pipe).toBeTruthy();
    pipe.transform({SHOW_RESPONSE: true, SHOW_GIVER_NAME: true, SHOW_RECIPIENT_NAME: true});

    console.log(pipe.b);
  });
});
