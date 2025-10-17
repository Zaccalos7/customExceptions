package com.orbis.exception.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.orbis.exception.annotations.ExceptionMaker")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class ExceptionMakerProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement packagePlusInterfaceName;
        ExceptionMaker exceptionMaker;
        String exceptionClassName;
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

            exceptionClassName = exceptionMaker.className();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "Exceptions generated from: " + packageName + "." + exceptionClassName);

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "Annotations taken: " + packagePlusInterfaceName + " the row= throwRuntimeException:" + exceptionMaker);
            try {
                JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + "." + exceptionClassName);
               writeExceptionsFile(file, packageName, exceptionClassName);
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "error:"+ e.getLocalizedMessage()+"/t"+ Arrays.toString(e.getStackTrace()));
            }
        }
        return true;
    }

    public void writeExceptionsFile(JavaFileObject javaFileObject, String packageName, String exceptionClassName){

        try (Writer writer = javaFileObject.openWriter()) {
            writer.write("package " + packageName + ";\n\n");
            writer.write("public class "+exceptionClassName + " extends RuntimeException {\n");
            writer.write("    private final Object[] params;\n\n");

            writer.write("    public " + exceptionClassName+" (String message) {\n");
            writer.write("        super(message);\n");
            writer.write("        this.params = null;\n");
            writer.write("    }\n\n");

            writer.write("    public "+exceptionClassName+"(String message, Object[] params) {\n");
            writer.write("        super(message);\n");
            writer.write("        this.params = params;\n");
            writer.write("    }\n");

            writer.write("}\n");
        }catch (IOException exception){
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"Error during the writting of the exception "+exceptionClassName);
        }

    }
}
