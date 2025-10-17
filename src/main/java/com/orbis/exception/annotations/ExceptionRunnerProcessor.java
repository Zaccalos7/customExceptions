package com.orbis.exception.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Arrays;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.orbis.exception.annotations.ExceptionRunner")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class ExceptionRunnerProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

        Set<? extends Element> annotatedElementWithRunException = roundEnvironment.getElementsAnnotatedWith(ExceptionRunner.class);
        String methodName;
        String interfaceName;
        String packageName;
        String exceptionNameClass;

        for(Element element : annotatedElementWithRunException){
            if(element.getKind() != ElementKind.METHOD)
                continue;

            ExecutableElement methodElement = (ExecutableElement) element;
            methodName = methodElement.getSimpleName().toString();

            exceptionNameClass = methodElement.getAnnotation(ExceptionRunner.class).exceptionMethod();

            Element enclosingElement = methodElement.getEnclosingElement();
            interfaceName = enclosingElement.getSimpleName().toString();

            PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(enclosingElement);
            packageName = packageElement.getQualifiedName().toString();

            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "Annotated method: " + methodName
            );
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "Interface's name: " + interfaceName
            );
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "Package: " + packageName
            );

            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "Exception class name: " + exceptionNameClass
            );

            try {
                JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(packageName + "." + interfaceName+"Impl");
                writeThrowCustomException(javaFileObject, packageName, interfaceName, exceptionNameClass);
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "error:"+ e.getLocalizedMessage()+"/t"+ Arrays.toString(e.getStackTrace()));
            }

        }
        return true;
    }

    public void writeThrowCustomException(JavaFileObject javaFileObject, String packageName, String interfaceName, String exceptionNameClass){
        try (Writer writer = javaFileObject.openWriter()) {
            writer.write("package " + packageName + ";\n\n");
            writer.write("import " + packageName + "."+exceptionNameClass+";\n\n");
            writer.write("public abstract class " + interfaceName+"Impl" + " {\n\n");

            writer.write("    void createException(String message) {\n");
            writer.write("        throw new CustomException(message);\n");
            writer.write("    }\n\n");

            writer.write("    void createException(String message, Object[] params) {\n");
            writer.write("        throw new CustomException(message, params);\n");
            writer.write("    }\n");

            writer.write("}\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
