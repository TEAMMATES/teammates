# TEAMMATES Frontend (Angular/TypeScript) - AI Agent Guide

## Overview

This directory contains the Angular 16 frontend application for TEAMMATES.

## Structure

```
web/
├── app/                    # Main application code
│   ├── components/         # Reusable components
│   ├── pages-*/            # Page components (instructor, student, admin, etc.)
│   ├── pipes/              # Angular pipes
│   └── *.component.ts      # Root components
├── services/               # Angular services
├── types/                  # TypeScript type definitions
│   ├── api-output.ts       # Generated from backend
│   ├── api-request.ts      # Generated from backend
│   └── api-const.ts        # Generated constants
├── assets/                 # Static assets
└── environments/           # Environment configuration
```

## Key Technologies

- **Angular 16** - Framework
- **TypeScript** - Language
- **Bootstrap 5** - CSS framework
- **RxJS** - Reactive programming
- **Jest** - Unit testing framework

## Component Patterns

### Component Structure

```typescript
@Component({
  selector: 'tm-component-name',
  templateUrl: './component-name.component.html',
  styleUrls: ['./component-name.component.scss'],
})
export class ComponentNameComponent implements OnInit {
  // Component code
}
```

### Page Components

- Extend from base page components (`PageComponent`, `StudentPageComponent`, etc.)
- Use services for data fetching
- Handle loading states and errors

### Service Pattern

```typescript
@Injectable({
  providedIn: 'root',
})
export class ServiceName {
  constructor(private httpRequestService: HttpRequestService) {}
  
  // API calls return Observables
  getData(): Observable<Data> {
    return this.httpRequestService.get('/endpoint');
  }
}
```

## API Integration

### Making API Calls

```typescript
// Use HttpRequestService
this.httpRequestService.get('/students', {
  courseId: this.courseId,
}).subscribe({
  next: (response: StudentsData) => {
    this.students = response.students;
  },
  error: (resp: ErrorMessageOutput) => {
    this.statusMessageService.showErrorToast(resp.error.message);
  },
});
```

### Type Safety

- Use generated types from `types/api-output.ts` and `types/api-request.ts`
- These are synced from backend - don't modify manually
- Regenerate with `./gradlew generateTypes`

## UI/UX Guidelines

### User Feedback

- Use `StatusMessageService` for toast notifications
- Show loading states during API calls
- Display clear error messages

### Forms

- Use Angular Reactive Forms (`FormBuilder`, `FormGroup`)
- Validate inputs on both client and server side
- Show validation errors clearly

### Accessibility

- Use semantic HTML
- Include ARIA labels where needed
- Test with screen readers
- Follow Bootstrap accessibility guidelines

### Mobile-Friendliness

- Use Bootstrap responsive classes
- Test on mobile devices
- Ensure touch targets are adequate size

## Styling

- **Prefer Bootstrap classes** over custom CSS
- Use Bootstrap utility classes (`text-danger`, `btn-primary`, etc.)
- Custom styles go in `*.component.scss`
- Follow the neutral color scheme

## Testing

- Unit tests in `*.component.spec.ts` files (same directory)
- Use Jest (not Karma)
- Use snapshot testing for UI components
- Mock services and HTTP calls

```typescript
describe('ComponentNameComponent', () => {
  let component: ComponentNameComponent;
  let fixture: ComponentFixture<ComponentNameComponent>;
  
  beforeEach(() => {
    // Setup
  });
  
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
```

### Test Naming

Follow format: `"<function-name>: should ... when/if ..."`

Example:
```typescript
it("hasSection: should return false when there are no sections in the course");
```

### Creating Test Data

Use the builder in `src/web/test-helpers/generic-builder.ts`:

```typescript
const instructorModelBuilder = createBuilder<InstructorListInfoTableRowModel>({
  email: "instructor@gmail.com",
  name: "Instructor",
  hasSubmittedSession: false,
  isSelected: false,
});
```

## Common Tasks

### Creating a New Page

1. Create component files (`*.component.ts`, `*.component.html`, `*.component.scss`)
2. Create `*.component.spec.ts` for tests
3. Add route in appropriate module
4. Add navigation link if needed

### Creating a Reusable Component

1. Create in `app/components/`
2. Use `tm-` prefix for selectors
3. Make it configurable via `@Input()` properties
4. Emit events via `@Output()` if needed

## Important Files

- `app/page.component.ts` - Base page component
- `services/http-request.service.ts` - HTTP service
- `services/status-message.service.ts` - Toast notifications
- `types/api-output.ts` - Response types (generated)
- `types/api-request.ts` - Request types (generated)

## Things to Avoid

- Don't modify generated types (`api-output.ts`, `api-request.ts`)
- Don't use inline styles - use SCSS files
- Don't skip error handling in API calls
- Don't forget to sanitize user input before displaying
- Don't create components without tests
- Don't test implementation details - test behavior
