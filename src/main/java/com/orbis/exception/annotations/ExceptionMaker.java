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

    /**
     * Allows the user to define a custom order and number of parameters
     * for the generated exception constructor.
     * <p>
     * Use this when you want to manually specify the order of constructor parameters
     * or pass a variable number of parameters directly, instead of providing them
     * inside an {@code Object[]} array (e.g., {@code Object[] params}).
     * </p>
     *
     * <pre>
     * {@code
     * @ExceptionMaker(
     *     classNames = {"UserNotFoundException"},
     *     enableCustomParameterOrder = true
     * )
     * }
     *
     * {@code
     *  Example: new UserNotFoundException("UserID_123", genericList , message, 404);
     * }
     * </pre>
     *
     * <pre>
     * {@code
     * @ExceptionMaker(
     *     classNames = {"UserNotFoundException"},
     *     enableCustomParameterOrder = false
     * )
     * }
     *
     * {@code
     *  Example: new UserNotFoundException(message, new Object[]{"Not found", 404, genericList});
     * }
     * </pre>
     *
     * @return true if custom constructor parameter order and count should be enabled
     */
    boolean enableCustomParameterOrder() default false;
}

