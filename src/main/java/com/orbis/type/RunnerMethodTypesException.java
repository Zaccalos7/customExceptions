package com.orbis.type;

import com.orbis.exception.annotations.ExceptionRunner;

/**
 * Class representing metadata of a method annotated with {@link ExceptionRunner}.
 *
 * <p>It stores return type, method name, arguments, the related exception class,
 * and the variable list used for code generation.</p>
 */
public class RunnerMethodTypesException {
    private String returnMethodType;
    private String methodName;
    private String methodArguments;
    private String exceptionNameClass;
    private String variableList;

    /** @return the return type of the method */
    public String getReturnMethodType() {
        return returnMethodType;
    }

    /** @param returnMethodType the return type to set */
    public void setReturnMethodType(String returnMethodType) {
        this.returnMethodType = returnMethodType;
    }

    /** @return the method name */
    public String getMethodName() {
        return methodName;
    }

    /** @param methodName the method name to set */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /** @return the method arguments as a string */
    public String getMethodArguments() {
        return methodArguments;
    }

    /** @param methodArguments the method arguments to set */
    public void setMethodArguments(String methodArguments) {
        this.methodArguments = methodArguments;
    }

    /** @return the exception class name */
    public String getExceptionNameClass() {
        return exceptionNameClass;
    }

    /** @param exceptionNameClass the exception class name to set */
    public void setExceptionNameClass(String exceptionNameClass) {
        this.exceptionNameClass = exceptionNameClass;
    }

    /** @return the variable list (parameter names) */
    public String getVariableList() {
        return variableList;
    }

    /** @param variableList the variable list to set */
    public void setVariableList(String variableList) {
        this.variableList = variableList;
    }
}
