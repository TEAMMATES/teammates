import { VisibilityCapabilityPipe } from './visibility-capability.pipe';
import { VisibilityControl } from '../../../types/visibility-control';

describe('VisibilityCapabilityPipe', () => {
    let pipe: VisibilityCapabilityPipe;

    beforeEach(() => {
        pipe = new VisibilityCapabilityPipe();
    });

    it('should create', () => {
        expect(pipe).toBeTruthy();
    });

    it('transform: should return correct message when both recipient and giver name are shown', () => {
        const controls = {
            [VisibilityControl.SHOW_RECIPIENT_NAME]: true,
            [VisibilityControl.SHOW_GIVER_NAME]: true,
        } as any;
        expect(pipe.transform(controls)).toBe('can see your response, the name of the recipient, and your name');
    });

    it('transform: should return correct message when only recipient name is shown', () => {
        const controls = {
            [VisibilityControl.SHOW_RECIPIENT_NAME]: true,
            [VisibilityControl.SHOW_GIVER_NAME]: false,
        } as any;
        expect(pipe.transform(controls)).toBe('can see your response, the name of the recipient, but not your name');
    });

    it('transform: should return correct message when only giver name is shown', () => {
        const controls = {
            [VisibilityControl.SHOW_RECIPIENT_NAME]: false,
            [VisibilityControl.SHOW_GIVER_NAME]: true,
        } as any;
        expect(pipe.transform(controls)).toBe('can see your response, and your name, but not the name of the recipient');
    });

    it('transform: should return correct message when neither names are shown', () => {
        const controls = {
            [VisibilityControl.SHOW_RECIPIENT_NAME]: false,
            [VisibilityControl.SHOW_GIVER_NAME]: false,
        } as any;
        expect(pipe.transform(controls)).toBe('can see your response, but not the name of the recipient, or your name');
    });

    it('transform: should handle empty visibility controls gracefully', () => {
        const controls = {} as any;
        expect(pipe.transform(controls)).toBe('can see your response, but not the name of the recipient, or your name');
    });
});