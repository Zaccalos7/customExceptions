package com.orbis.exception;

import com.google.auto.service.AutoService;
import com.orbis.exception.annotations.ExceptionMaker;

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
            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.NOTE, "Exceptions generated from: " + packageName + "." + exceptionClassesName);

            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.NOTE, "Annotations taken: " + packagePlusInterfaceName + " the row= ExceptionMaker:" + exceptionMaker);
            try {
                JavaFileObject javaFileObject;
                for (String exceptionClassName : exceptionClassesName) {
                    javaFileObject = processingEnv.getFiler().createSourceFile(packageName + "." + exceptionClassName);
                    writeExceptionsFile(javaFileObject, packageName, exceptionClassName);
                }

            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "error:" + e.getLocalizedMessage() + "/t" + Arrays.toString(e.getStackTrace()));
            }
        }
        return true;
    }

    /**
     * Write the exceptions class
     *
     * @param javaFileObject java file to generate
     * @param packageName the package where user used the annotations
     * @param exceptionClassName exception custom name
     */
    public void writeExceptionsFile(JavaFileObject javaFileObject, String packageName, String exceptionClassName) {

        try (Writer writer = javaFileObject.openWriter()) {
            writer.write("/*\n");
            writer.write("*\n");
            writer.write("* Code generated:\n");
            writer.write("*" + " " + LocalDateTime.now() + "\n");
            writer.write("*/\n");
            writer.write("package " + packageName + ";\n\n");
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

            writer.write("}\n");
        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error during the writting of the exception " + exceptionClassName);
        }

    }
}
