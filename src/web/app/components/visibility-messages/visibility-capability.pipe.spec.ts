import {VisibilityCapabilityPipe} from './visibility-capability.pipe';

describe('VisibilityCapabilityPipe', () => {
  it('create an instance', () => {
    const pipe: VisibilityCapabilityPipe = new VisibilityCapabilityPipe();
    expect(pipe).toBeTruthy();
  });

  let visibilityCapabilityPipe: VisibilityCapabilityPipe;

  beforeEach(() => {
    visibilityCapabilityPipe = new VisibilityCapabilityPipe();
  })

  it('should return correct message', () => {
    let example1: { SHOW_RECIPIENT_NAME: boolean; SHOW_GIVER_NAME: boolean; SHOW_RESPONSE: boolean } = {
      SHOW_RESPONSE: true,
      SHOW_GIVER_NAME: true,
      SHOW_RECIPIENT_NAME: true
    };
    expect(visibilityCapabilityPipe.transform(example1)).toBe("can see your response, the name of the recipient, and your name");

    let example2: { SHOW_RECIPIENT_NAME: boolean; SHOW_GIVER_NAME: boolean; SHOW_RESPONSE: boolean } = {
      SHOW_RESPONSE: true,
      SHOW_GIVER_NAME: true,
      SHOW_RECIPIENT_NAME: false
    };
    expect(visibilityCapabilityPipe.transform(example2)).toBe("can see your response, and your name, but not the name of the recipient");

    let example3: { SHOW_RECIPIENT_NAME: boolean; SHOW_GIVER_NAME: boolean; SHOW_RESPONSE: boolean } = {
      SHOW_RESPONSE: true,
      SHOW_GIVER_NAME: false,
      SHOW_RECIPIENT_NAME: true
    };
    expect(visibilityCapabilityPipe.transform(example3)).toBe("can see your response, the name of the recipient, but not your name");

    let example4: { SHOW_RECIPIENT_NAME: boolean; SHOW_GIVER_NAME: boolean; SHOW_RESPONSE: boolean } = {
      SHOW_RESPONSE: true,
      SHOW_GIVER_NAME: false,
      SHOW_RECIPIENT_NAME: false
    };

    expect(visibilityCapabilityPipe.transform(example4)).toBe("can see your response, but not the name of the recipient, or your name");
  });
});
