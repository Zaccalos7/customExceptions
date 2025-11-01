package com.orbis.exception.annotations;

/**
 * Annotation to mark generated code with metadata.
 *
 * - {@code version}: Specifies the current version of the project at the time of generation.
 * - {@code date}: Indicates the date when the annotated code was generated.
 * - {@code packageInfo}: Describes the package location where the annotations are placed.
 */
public @interface Generated {
    String version();
    String date();
    String packageInfo();
}
