package com.orbis.type;

public class RunnerMethodTypesException {
    private String returnMethodType;
    private String methodName;
    private String methodArguments;
    private String exceptionNameClass;
    private String variableList;

    public String getReturnMethodType() {
        return returnMethodType;
    }

    public void setReturnMethodType(String returnMethodType) {
        this.returnMethodType = returnMethodType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodArguments() {
        return methodArguments;
    }

    public void setMethodArguments(String methodArguments) {
        this.methodArguments = methodArguments;
    }

    public String getExceptionNameClass() {
        return exceptionNameClass;
    }

    public void setExceptionNameClass(String exceptionNameClass) {
        this.exceptionNameClass = exceptionNameClass;
    }

    public String getVariableList() {
        return variableList;
    }

    public void setVariableList(String variableList) {
        this.variableList = variableList;
    }
}
