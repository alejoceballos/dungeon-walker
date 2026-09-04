# Coding Guidelines for AI Agents

Apply these guidelines when writing, reviewing, testing, documenting, and
committing Java code. The primary objective is maintainable code that future
developers can read, understand, extend, and safely modify.

## Core design principles

- Treat the codebase as object-oriented. Evaluate all production changes
  against SOLID principles.
- Give each class and method one cohesive responsibility. Do not allow message
  consumers, processors, and publishers (or similarly unrelated concerns) to
  accumulate in a single class.
- Prefer extension over modification. When related behavior varies or will
  grow, use an appropriate design pattern (for example, Strategy) rather than
  adding chains of `if` statements or `switch` cases.
- Depend on abstractions, not concrete implementations. Preserve layer
  isolation and inject interfaces that expose only what the consumer needs.
- Honor Liskov substitution: replacements for a dependency must keep client
  code working without modification.
- Keep interfaces focused. A consumer must not depend on methods it does not
  use; split broad interfaces into purpose-specific ones.
- Use inheritance only for a genuine "is-a" relationship. Do not extend a type
  merely to reuse state or behavior; prefer composition.
- Keep designs simple, but do not sacrifice extensibility or maintainability
  for a quick implementation. Use SOLID and design patterns by default; depart
  from them only deliberately and with a clear reason.

## Reuse and class structure

- Do not repeat code *or ideas*. Search for an existing suitable abstraction
  before adding new logic.
- Extract repetition within one class into a private method.
- Extract repeated behavior across classes through composition or, where
  appropriate, inheritance; prefer composition.
- Look for established design patterns before duplicating related conditional
  logic.
- Treat test code as production-quality code: remove duplication with
  composition, inheritance, and parameterized tests where appropriate.
- Avoid general-purpose utility classes. Establish the scope and ownership of
  each operation instead of adding static helpers that become shared,
  untestable, bloated dependencies.
- Avoid public static methods. Use them only when justified, such as a
  dependency-injection-free library factory. Prefer an injectable class with
  lifecycle owned by the consuming system.

## Code style

- First identify and follow the existing codebase's clean conventions. Change a
  convention only through team agreement and apply the new convention
  consistently across its relevant scope.
- Use access and implementation modifiers to communicate intent:
  - Use `private` unless wider visibility is required.
  - Use package visibility only for intentional package collaboration.
  - Use `public` only for the external contract that must be visible.
  - Use `final` for state that must not change.
  - Use `static` only when state or behavior is intentionally shared rather
    than instance-specific.
- Mark every method parameter and local variable `final` unless it is
  intentionally reassigned.
- Use `var` unless an explicit type materially improves readability, especially
  for complex chained expressions. Prefer `var` for obvious assignments and
  overly verbose generic types.
- End every file with a newline. Use one blank line after a class opening and
  before its closing brace. Do not add blank lines immediately inside method
  braces or extra blank lines between methods.
- Use Java text blocks for large strings containing line breaks. For readable
  long single-line strings, use text-block line continuation (`\`) rather than
  concatenation or `StringBuilder`/`StringBuffer`.
- Replace unexplained numeric literals with well-named constants.
- Declare variables as close as possible to their first use and keep setup
  next to the code that uses it.

## Documentation

- Add and maintain Javadoc for every class, interface, record, and enum, and
  for every public or protected method.
- Do not require Javadoc for test classes or test methods; tests should serve
  as executable documentation.
- Review generated or copied Javadoc for accuracy. Update it whenever public
  signatures, parameters, or generic declarations change.
- Keep the README current when behavior, setup, architecture, or contribution
  guidance changes. Include practical run/change guidance and architecture
  references or links when useful.
- Prefer working software and direct communication over unnecessary
  documentation, but document reusable services, libraries, and complex flows
  adequately.

## Java implementation guidance

- Do not use `double` or `Double` for mathematical calculations requiring
  reliable decimal precision; use `BigDecimal` with an appropriate precision
  and rounding policy.
- Favor Streams when they improve readability and maintainability. Use regular
  loops when the performance characteristics of a small or hot iteration make
  that choice genuinely advantageous.
- Use `Optional` to clarify null handling:
  - Prefer `Optional.ofNullable(value).orElse(defaultValue)` to simple
    null-check fallback logic.
  - Use `map` chains to safely traverse nullable object graphs.
  - Do not deeply nest `Optional` expressions; refactor when nesting harms
    readability.

## Testing

- Aim for 100% coverage of new and changed code. If code cannot reasonably be
  covered, document the specific reason in the pull request or source where
  appropriate.
- Do not use the same business logic under test to create expected assertions;
  otherwise identical defects can create false positives.
- Mock collaborators explicitly with Mockito `mock`/`when`. For complete
  object comparisons, use AssertJ `usingRecursiveComparison` against an
  independently controlled expected object.
- Never widen a method's visibility solely to test it. If a private behavior
  needs independent testing, extract it into a separate collaborator and use
  composition and polymorphism (for example, Strategy).
- Prefer explicit constructor calls in tests over `@InjectMocks` so dependency
  changes are caught by the compiler and remain visible in test code.
- Use Mockito matchers precisely:
  - In stubbing and positive verification, prefer typed matchers such as
    `any(SomeClass.class)` or `anyString()` rather than bare `any()`.
  - Use bare `any()` only when verifying a method was never called, so every
    possible argument is covered.
  - Use `never()` rather than `times(0)`.
  - Use `verifyNoInteractions` when asserting that a collaborator was not
    involved at all.
- Do not put conditional logic in tests. Make outcomes deterministic; use
  `@ParameterizedTest` for multiple input/output cases. Conditional assertions
  indicate an uncontrolled outcome or a design that needs refactoring.

## Quality tools and commits

- Resolve IDE warnings before committing. If a warning is knowingly valid to
  suppress, use the appropriate `@SuppressWarnings` annotation instead of
  allowing an unreviewed warning backlog.
- Use the project's SonarQube rules and address reported issues before
  committing.
- Use IDE assistance to organize imports and format files. Avoid wildcard
  imports.
- Keep commits small and organized by scope.
- Write concise, meaningful commit messages that make the change easy to
  identify and trace.
- Update the related Jira ticket so future maintainers can understand the
  broader change context.

## AI-assisted work

- AI may assist with implementation, tests, documentation, and specification
  work, but never accept its output without a thorough review.
- Ensure AI-generated changes conform to local conventions, reuse existing
  abstractions, maintain cohesive scopes, and do not introduce redundant or
  inconsistent code.
- Treat this document as a living standard. Apply exceptions judiciously and
  never let an exception become an unexamined default.
