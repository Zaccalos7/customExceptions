package com.orbis.type;

public class RunnerClassAndPackageException {
    String packageName;
    String interfaceName;

    public RunnerClassAndPackageException(String packageName, String interfaceName) {
        this.packageName = packageName;
        this.interfaceName = interfaceName;
    }

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
}
