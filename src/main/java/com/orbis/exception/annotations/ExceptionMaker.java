package com.orbis.exception.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an interface as a source for automatic exception-handling
 * class generation.
 *
 * <p>This annotation must be placed <b>above an interface</b> that defines
 * one or more method signatures annotated with {@link ExceptionRunner}.
 * During code generation, a separate implementation class will be created
 * for each exception name specified in {@link #classesName()}.</p>
 *
 * <p>The {@code classesName} parameter defines the list of exception class
 * names that should be generated. Each element in this list corresponds
 * to one generated class responsible for handling the specified exception.</p>
 *
 * <p><b>Note:</b>
 * Retention policy: {@link java.lang.annotation.RetentionPolicy#SOURCE}
 * (discarded after compilation).<br>
 * Target: {@link java.lang.annotation.ElementType#TYPE}
 * (applicable to interfaces only).</p>
 *
 * @see java.lang.annotation.Retention
 * @see java.lang.annotation.Target
 * @see ExceptionRunner
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ExceptionMaker {

    /**
     * Defines the list of exception class names to generate.
     *
     * <p>Each element in this array corresponds to one generated class
     * responsible for handling the specified exception.</p>
     * <pre>
     *     {@code @ExceptionMaker(classesName = {"UserNotFoundException", "InvalidRequestException"})}
     * </pre>
     *
     * @return the list of exception class names
     */
    String[] classesName();
}

