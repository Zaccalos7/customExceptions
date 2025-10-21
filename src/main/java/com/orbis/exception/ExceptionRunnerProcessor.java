package com.orbis.exception;

import com.google.auto.service.AutoService;
import com.orbis.exception.annotations.ExceptionRunner;
import com.orbis.type.RunnerClassAndPackageException;
import com.orbis.type.RunnerMethodTypesException;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.orbis.exception.annotations.ExceptionRunner")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class ExceptionRunnerProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

        Set<? extends Element> annotatedElementWithRunException = roundEnvironment.getElementsAnnotatedWith(ExceptionRunner.class);

        List<Element> validAnnotations = getValidAnnotations(annotatedElementWithRunException);
        if(validAnnotations.isEmpty()){
            return false;
        }
        Element validAnnotatio = validAnnotations.get(0);
        RunnerClassAndPackageException runnerClassAndPackageException = getClassAndImport(validAnnotatio);

        try {
            JavaFileObject javaFileObject = processingEnv.getFiler()
                    .createSourceFile(
                            runnerClassAndPackageException.getPackageName()
                                    + "."
                                    + runnerClassAndPackageException.getInterfaceName()
                                    + "Impl");
            writePackageImpl(
                    javaFileObject,
                    runnerClassAndPackageException.getPackageName(),
                    runnerClassAndPackageException.getInterfaceName(),
                    validAnnotations);
        } catch (Exception e) {
            processingEnv
                    .getMessager()
                    .printMessage(
                            Diagnostic.Kind.ERROR, "error:" + e.getLocalizedMessage() + "\t" + Arrays.toString(e.getStackTrace()));
        }

        return true;
    }


    private List<Element> getValidAnnotations(Set<? extends Element> annotatedElementWithRunException) {
        List<Element> elements = new ArrayList<>();
        for (Element element : annotatedElementWithRunException) {
            elements.add(element);
        }
        return elements;
    }

    private RunnerClassAndPackageException getClassAndImport(Element validAnnotation) {
        ExecutableElement methodElement = (ExecutableElement) validAnnotation;
        String methodName = methodElement.getSimpleName().toString();
        //String exceptionNameClass = methodElement.getAnnotation(ExceptionRunner.class).exceptionMethod();

        Element enclosingElement = methodElement.getEnclosingElement();
        String interfaceName = enclosingElement.getSimpleName().toString();

        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(enclosingElement);
        String packageName = packageElement.getQualifiedName().toString();

        printerAtCompileTime(methodName, interfaceName, packageName);

        return new RunnerClassAndPackageException(packageName, interfaceName);
    }

    private void writePackageImpl(JavaFileObject javaFileObject, String packageName, String interfaceName, List<Element> validAnnotations) {
        try (Writer writer = javaFileObject.openWriter()) {
            writer.write("/*\n");
            writer.write("*\n");
            writer.write("* Code generated:\n");
            writer.write("*" + " " + LocalDateTime.now() + "\n");
            writer.write("*/\n");
            writer.write("package " + packageName + ";\n\n");

            writeImportsAndClassImpl(writer, packageName, validAnnotations, interfaceName);

            writerRunnerMethodsExceptionsImpl(writer, validAnnotations);

            writer.write("}\n");
        } catch (Exception e) {
            throw new RuntimeException("error during the writting " + javaFileObject);
        }
    }


    private void writeImportsAndClassImpl(Writer writer, String packageName, List<Element> validAnnotations, String interfaceName) throws IOException {

        String exceptionNameClass;
        String componetModel;

        ExecutableElement methodElement;
        String generatedNameClass = interfaceName + "Impl";

        writer.write("import org.springframework.stereotype.Component;\n");
        for (Element element : validAnnotations) {
            methodElement = (ExecutableElement) element;
            exceptionNameClass = methodElement.getAnnotation(ExceptionRunner.class).exceptionClass();
            componetModel = methodElement.getAnnotation(ExceptionRunner.class).componentModel(); //for jakarta in the future
            writer.write("import " + packageName + "." + exceptionNameClass + ";\n\n");
        }
        writer.write("@Component\n");
        writer.write("public class " + generatedNameClass + " implements " + interfaceName + " {\n\n");
    }

    private void writerRunnerMethodsExceptionsImpl(Writer writer, List<Element> validAnnotations) throws IOException {

        List<RunnerMethodTypesException> runnerList = createListForMakeExceptionRunnerMethods(validAnnotations);

        for(RunnerMethodTypesException runnerMethod: runnerList){
            writer.write("\t@Override\n");
            writer.write("    public " + runnerMethod.getReturnMethodType() + " " + runnerMethod.getMethodName() + "(" + runnerMethod.getMethodArguments() + ")"
                    + " {\n");
            writer.write("        throw new " + runnerMethod.getExceptionNameClass() + "(" + runnerMethod.getVariableList() + ");\n");
            writer.write("    }\n\n");
        }
    }

    private List<RunnerMethodTypesException> createListForMakeExceptionRunnerMethods(List<Element> validAnnotations) {
        List<RunnerMethodTypesException> runnerMethodTypesExceptionList = new ArrayList<>();
        RunnerMethodTypesException runnerMethodTypesException = new RunnerMethodTypesException();

        ExecutableElement methodElement;
        List<? extends VariableElement> methodParameters;
        List<String> typeParameters;

        String exceptionNameClass;
        String variableList;

        for (Element element : validAnnotations) {
            methodElement = (ExecutableElement) element;

            runnerMethodTypesException.setReturnMethodType(methodElement.getReturnType().toString());
            runnerMethodTypesException.setMethodName(methodElement.getSimpleName().toString());

            methodParameters = methodElement.getParameters();
            typeParameters = getTypesAndMethodParametersAndConcatenatesThem(methodParameters);

            runnerMethodTypesException.setMethodArguments(String.join(",", typeParameters));

            exceptionNameClass = methodElement.getAnnotation(ExceptionRunner.class).exceptionClass();
            runnerMethodTypesException.setExceptionNameClass(exceptionNameClass);

            variableList = typeParameters.stream()
                    .map(value -> value.split(" ")[1])
                    .collect(Collectors.joining(","));

            runnerMethodTypesException.setVariableList(variableList);

            runnerMethodTypesExceptionList.add(runnerMethodTypesException);
            runnerMethodTypesException = new RunnerMethodTypesException();
        }
        return runnerMethodTypesExceptionList;
    }

    private List<String> getTypesAndMethodParametersAndConcatenatesThem(List<? extends VariableElement> methodParameters) {
        List<String> typeParameters = new ArrayList<>();
        for (VariableElement param : methodParameters) {
            String paramName = param.getSimpleName().toString();
            String paramType = param.asType().toString();
            typeParameters.add(paramType + " " + paramName);
        }
        return typeParameters;
    }

    public void printerAtCompileTime(
            String methodName,
            String interfaceName,
            String packageName
    ) {
        processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.NOTE, "Annotated method: " + methodName);
        processingEnv
                .getMessager().printMessage(Diagnostic.Kind.NOTE, "Interface's name: " + interfaceName);
        processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.NOTE, "Package: " + packageName);

    }
}
