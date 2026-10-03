# Core module instructions

Apply the [repository instructions](../AGENTS.md) first. This guide covers `jenie-spring-core`.

## Responsibility

This is the small shared foundation for other Jenie modules. Its current entry points are
`src/main/java/org/jenie/spring/util/` and the shared logging resource
`src/main/resources/org/jenie/spring/base.xml`.

## Changes

- Keep utilities reusable across applications. Put MongoDB behavior in `jenie-spring-data-mongodb` and Helloworld
  business behavior in the Helloworld modules rather than adding those dependencies here.
- Preserve the retention, targets, and `Generated` naming requirement of `ExcludeCodeCoverageGenerated`; its name is
  significant to coverage filtering.
- Changes to `base.xml` can affect logging in multiple applications. Inspect its consumers before changing resource
  names, appenders, or logging behavior.

## Verification

```sh
./gradlew :jenie-spring-core:classes :jenie-spring-core:checkFormat :jenie-spring-core:checkstyleMain
```

For public API or resource changes, also compile or exercise the affected consuming modules. Add focused tests when
introducing executable utility behavior.
