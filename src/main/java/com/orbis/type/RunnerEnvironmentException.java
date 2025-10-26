package com.orbis.type;

import javax.lang.model.element.Element;

public class RunnerEnvironmentException {
    private String packageName;
    private String interfaceName;
    private Element methodWithValidAnnotation;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Element getMethodWithValidAnnotation() {
        return methodWithValidAnnotation;
    }

    public void setMethodWithValidAnnotation(Element methodWithValidAnnotation) {
        this.methodWithValidAnnotation = methodWithValidAnnotation;
    }
}
