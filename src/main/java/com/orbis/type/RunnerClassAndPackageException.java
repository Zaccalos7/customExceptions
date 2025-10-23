package com.orbis.type;

/**
 * Simple class holding package and interface names
 * used during exception class generation.
 */
public class RunnerClassAndPackageException {
    private String packageName;
    private String interfaceName;

    /**
     * Creates a new container with package and interface name.
     *
     * @param packageName   the package name
     * @param interfaceName the interface name
     */
    public RunnerClassAndPackageException(String packageName, String interfaceName) {
        this.packageName = packageName;
        this.interfaceName = interfaceName;
    }

    /** @return the package name */
    public String getPackageName() {
        return packageName;
    }

    /** @param packageName the package name to set */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /** @return the interface name */
    public String getInterfaceName() {
        return interfaceName;
    }

    /** @param interfaceName the interface name to set */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
}
