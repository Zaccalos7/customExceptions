package com.orbis.exception.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method signature inside an interface as an exception-handling
 * entry point to be implemented automatically.
 *
 * <p>This annotation must be placed <b>above the method signature</b> 
 * within an interface that is itself annotated with {@link ExceptionMaker}.
 * During code generation, implementation methods will be created based
 * on these annotated method signatures.</p>
 *
 * <p>The {@code exceptionClass} parameter defines which exception type
 * should be handled, while the {@code componentModel} parameter specifies
 * the target component model used for code generation.</p>
 *
 * <p><b>Note:</b>
 * Retention policy: {@link java.lang.annotation.RetentionPolicy#SOURCE}
 * (discarded after compilation).<br>
 * Target: {@link java.lang.annotation.ElementType#METHOD}
 * (applicable to methods only).</p>
 *
 * @see java.lang.annotation.Retention
 * @see java.lang.annotation.Target
 * @see ExceptionMaker
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ExceptionRunner {

    /**
     * Defines the exception class name to handle.
     *
     * <p>Example:</p>
     * <pre>
     * {@code exceptionClass = "ExampleException"}
     * </pre>
     *
     * @return the exception class name to handle
     */
    String exceptionClass();

    /**
     * Specifies the component model type used for code generation.
     *
     * @return the component model type
     */
    String componentModel() default "default";
}

