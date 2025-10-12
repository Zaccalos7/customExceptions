package com.orbis.exception.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.orbis.exception.annotations.ThrowRuntimeException")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class ThrowRuntimeExceptionProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ExecutableElement method;
        ThrowRuntimeException annotation;
        String className;
        String packageName;
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(ThrowRuntimeException.class);

        for (Element element : annotatedElements) {
            if (element.getKind() != ElementKind.METHOD)
                continue;

            method = (ExecutableElement) element;
            annotation = method.getAnnotation(ThrowRuntimeException.class);
            packageName = processingEnv.getElementUtils()
                    .getPackageOf(method.getEnclosingElement())
                    .getQualifiedName()
                    .toString();

            className = annotation.nameException()+"Impl";

            try {
                JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + "." + className);
                try (Writer writer = file.openWriter()) {
                    writer.write("package " + packageName + ";\n\n");
                    writer.write("public class "+className + " extends RuntimeException {\n");
                    writer.write("    private final Object[] params;\n\n");

                    writer.write("    public " + className+" (String message) {\n");
                    writer.write("        super(message);\n");
                    writer.write("        this.params = null;\n");
                    writer.write("    }\n\n");

                    writer.write("    public "+className+"(String message, Object[] params) {\n");
                    writer.write("        super(message);\n");
                    writer.write("        this.params = params;\n");
                    writer.write("    }\n");

                    writer.write("}\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
