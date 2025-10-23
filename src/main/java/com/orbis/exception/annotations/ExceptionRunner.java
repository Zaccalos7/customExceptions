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
 * within an interface that is itself annotated with {@code @ExceptionMaker}.
 * During code generation, implementation methods will be created based
 * on these annotated method signatures.</p>
 *
 * <p>The {@code exceptionClass} parameter defines which exception type 
 * should be handled, while the {@code componentModel} parameter specifies 
 * the target component model used for code generation.</p>
 *
 * <p><b>Note:</b> The annotation is retained only in the source code 
 * (RetentionPolicy.SOURCE) and can be applied to methods only.</p>
 *
 * @Retention {@link RetentionPolicy#SOURCE} — discarded after compilation
 * @Target {@link ElementType#METHOD} — applicable to methods only
 *
 * @see java.lang.annotation.Retention
 * @see java.lang.annotation.Target
 * @see ExceptionMaker
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ExceptionRunner {
    /**
    * @return the exception class name to handle
    * 
    * example
    * exceptionClass = "ExampleException"
    */
    String exceptionClass();
    /**
    *@return the component model type
    *
    */
    String componentModel() default "default";
}
