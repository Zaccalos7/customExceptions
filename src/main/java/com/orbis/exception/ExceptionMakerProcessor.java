package com.orbis.exception;

import com.google.auto.service.AutoService;
import com.orbis.exception.annotations.ExceptionMaker;
import com.orbis.exception.info.PROJECT;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.orbis.exception.annotations.ExceptionMaker")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class ExceptionMakerProcessor extends AbstractProcessor {


    /**
     * Processes all elements annotated with {@link ExceptionMaker}.
     *
     * @param annotations the set of annotation types requested to be processed
     * @param roundEnv    environment for information about the current and prior round
     * @return {@code true} if the annotations are claimed by this processor
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement packagePlusInterfaceName;
        ExceptionMaker exceptionMaker;
        String[] exceptionClassesName;
        boolean isEnableCustomParameterOrder;
        String packageName;
        Set<? extends Element> annotatedElementsWithThrowRuntimeException = roundEnv.getElementsAnnotatedWith(ExceptionMaker.class);

        for (Element element : annotatedElementsWithThrowRuntimeException) {

            if (element.getKind() != ElementKind.INTERFACE)
                continue;
            // Package where user created the interface with the annotation
            packagePlusInterfaceName = (TypeElement) element;
            exceptionMaker = packagePlusInterfaceName.getAnnotation(ExceptionMaker.class);
            packageName = processingEnv.getElementUtils()
                    .getPackageOf(packagePlusInterfaceName)
                    .getQualifiedName()
                    .toString();

            exceptionClassesName = exceptionMaker.classesName();
            isEnableCustomParameterOrder = exceptionMaker.enableCustomParameterOrder();
            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.NOTE, "Making Exceptions from: " + packageName + "." + exceptionClassesName);

            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.NOTE, "Annotations taken: " + packagePlusInterfaceName + " the row= ExceptionMaker:" + exceptionMaker);

            if (isEnableCustomParameterOrder) {
                writeWithCustomParameterOrder(exceptionClassesName, packageName);
            } else {
                writeWithoutCustomParameterOrder(exceptionClassesName, packageName);
            }
        }
        return true;
    }

    /**
     * Generates Java source files for the specified exception classes with applying custom parameter ordering.
     * The number of parameters depends on those defined in the constructor.
     *
     * @param exceptionClassesName array of exception class names to generate
     * @param packageName          name of the package where the classes will be created
     */
    private void writeWithCustomParameterOrder(String[] exceptionClassesName, String packageName) {
        try {
            JavaFileObject javaFileObject;

            for (String exceptionClassName : exceptionClassesName) {
                javaFileObject = processingEnv.getFiler().createSourceFile(packageName + "." + exceptionClassName);
                writeExceptionsFileWithCustomParameterOrder(javaFileObject, packageName, exceptionClassName);
            }

        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "error:" + e.getLocalizedMessage() + "/t" + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Writes a Java source file for a custom exception class, applying a specific parameter order.
     * The generated class includes a varargs constructor and stores parameters in a single field.
     *
     * @param javaFileObject     the file object representing the source file to be written
     * @param packageName        the package where the exception class will be placed
     * @param exceptionClassName the name of the exception class to generate
     */
    private void writeExceptionsFileWithCustomParameterOrder(JavaFileObject javaFileObject, String packageName, String exceptionClassName) {
        try (Writer writer = javaFileObject.openWriter()) {
            writer.write("package " + packageName + ";\n\n");
            writer.write("import com.orbis.exception.annotations.Generated;\n");
            writeGeneratedAnnotation(writer);
            writer.write("public class " + exceptionClassName + " extends RuntimeException {\n");

            writer.write("    private final Object param;\n\n");

            writer.write("    public " + exceptionClassName + "(Object... param) {\n");
            writer.write("        this.param = param;\n");
            writer.write("    }\n");

            writer.write("  public Object getParam(){\n");
            writer.write("      return param;\n");
            writer.write("  }\n");

            writer.write("}\n");
        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error during the writting of the exception " + exceptionClassName);
        }
    }

    /**
     * Generates Java source files for the specified exception classes without applying custom parameter ordering.
     *
     * @param exceptionClassesName array of exception class names to generate
     * @param packageName          name of the package where the classes will be created
     */
    private void writeWithoutCustomParameterOrder(String[] exceptionClassesName, String packageName) {
        try {
            JavaFileObject javaFileObject;

            for (String exceptionClassName : exceptionClassesName) {
                javaFileObject = processingEnv.getFiler().createSourceFile(packageName + "." + exceptionClassName);
                writeExceptionsFileWithoutCustomParameterOrder(javaFileObject, packageName, exceptionClassName);
            }

        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "error:" + e.getLocalizedMessage() + "/t" + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Write the exceptions class, without custom parameter order
     *
     * @param javaFileObject     java file to generate
     * @param packageName        the package where user used the annotations
     * @param exceptionClassName exception custom name
     */
    private void writeExceptionsFileWithoutCustomParameterOrder(JavaFileObject javaFileObject, String packageName, String exceptionClassName) {

        try (Writer writer = javaFileObject.openWriter()) {
            writer.write("package " + packageName + ";\n\n");
            writer.write("import com.orbis.exception.annotations.Generated;\n");
            writeGeneratedAnnotation(writer);
            writer.write("public class " + exceptionClassName + " extends RuntimeException {\n");
            writer.write("    private final Object[] params;\n\n");

            writer.write("    public " + exceptionClassName + " (String message) {\n");
            writer.write("        super(message);\n");
            writer.write("        this.params = null;\n");
            writer.write("    }\n\n");

            writer.write("    public " + exceptionClassName + "(String message, Object[] params) {\n");
            writer.write("        super(message);\n");
            writer.write("        this.params = params;\n");
            writer.write("    }\n");

            writer.write("  public Object[] getParams(){\n");
            writer.write("      return params;\n");
            writer.write("  }\n");

            writer.write("}\n");
        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error during the writting of the exception " + exceptionClassName);
        }

    }

    /**
     * Writes a custom {@code @Generated} annotation to the provided {@link Writer}.
     * <p>
     * The annotation includes:
     * <ul>
     *   <li><b>version</b> – the version of the JAR, retrieved from {@code PROJECT.VERSION}</li>
     *   <li><b>date</b> – the current generation timestamp, formatted as {@code dd/MM/yyyy HH:mm:ss}</li>
     *   <li><b>packageInfo</b> – the package name, retrieved from {@code PROJECT.PACKAGE}</li>
     * </ul>
     * <p>
     * This method is typically used during code generation to embed metadata
     * about the build and environment directly into the generated source code.
     * </p>
     *
     * @param writer the {@link Writer} where the annotation will be written
     * @throws RuntimeException if an {@link IOException} occurs while writing,
     *         an error message is also reported via the annotation processing environment
     */
    private void writeGeneratedAnnotation(Writer writer) {
        String version = PROJECT.VERSION.getValue();
        String infoPackage = PROJECT.PACKAGE.getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String date = LocalDateTime.now().format(formatter);
        try {
            writer.write("@Generated(\n");
            writer.write("\tversion=  \"" + version + "\"" + ",\n");
            writer.write("\tdate=  \"" + date + "\"" + ",\n");
            writer.write("\tpackageInfo=  \"" + infoPackage + "\"" + "\n");
            writer.write("\t)\n");
        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error during the writting of the @Generated annotation " + exception.getCause());
        }
    }

}
