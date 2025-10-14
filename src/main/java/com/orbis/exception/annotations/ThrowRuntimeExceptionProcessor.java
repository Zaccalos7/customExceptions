package com.orbis.exception.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.orbis.exception.annotations.ThrowRuntimeException")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class ThrowRuntimeExceptionProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement packagePlusInterfaceName;
        ThrowRuntimeException throwRuntimeException;
        String exceptionName;
        String packageName;
        Set<? extends Element> annotatedElementsWithThrowRuntimeException = roundEnv.getElementsAnnotatedWith(ThrowRuntimeException.class);

//        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
//                "annotatedElementsWithThrowRuntimeException " + annotatedElementsWithThrowRuntimeException );

        for (Element element : annotatedElementsWithThrowRuntimeException) {


            if (element.getKind() != ElementKind.INTERFACE)
                continue;

            packagePlusInterfaceName = (TypeElement) element; // Package where user created the interface with the annotation
            throwRuntimeException = packagePlusInterfaceName.getAnnotation(ThrowRuntimeException.class);
            packageName = processingEnv.getElementUtils()
                    .getPackageOf(packagePlusInterfaceName)
                    .getQualifiedName()
                    .toString();

            exceptionName = throwRuntimeException.nameException();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "Genero eccezione: " + packageName + "." + exceptionName);

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "Prendo l'annotazione da: " + packagePlusInterfaceName + " la riga= throwRuntimeException:" + throwRuntimeException);
            try {
                JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + "." + exceptionName);
                try (Writer writer = file.openWriter()) {
                    writer.write("package " + packageName + ";\n\n");
                    writer.write("public class "+exceptionName + " extends RuntimeException {\n");
                    writer.write("    private final Object[] params;\n\n");

                    writer.write("    public " + exceptionName+" (String message) {\n");
                    writer.write("        super(message);\n");
                    writer.write("        this.params = null;\n");
                    writer.write("    }\n\n");

                    writer.write("    public "+exceptionName+"(String message, Object[] params) {\n");
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
