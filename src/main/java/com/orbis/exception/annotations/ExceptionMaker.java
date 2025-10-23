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
 * one or more method signatures annotated with {@code @ExceptionRunner}.
 * During code generation, a separate implementation class will be created
 * for each exception name specified in {@link #classesName()}.</p>
 *
 * <p>The {@code classesName} parameter defines the list of exception class
 * names that should be generated. Each element in this list corresponds
 * to one generated class responsible for handling the specified exception.</p>
 *
 * <p><b>Note:</b> The annotation is retained only in the source code
 * (RetentionPolicy.SOURCE) and can be applied to types (interfaces) only.</p>
 *
 * @Retention {@link RetentionPolicy#SOURCE} — discarded after compilation
 * @Target {@link ElementType#TYPE} — applicable to interfaces only
 *
 * @see java.lang.annotation.Retention
 * @see java.lang.annotation.Target
 * @see ExceptionRunner
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ExceptionMaker {
    String[] classesName();

}
