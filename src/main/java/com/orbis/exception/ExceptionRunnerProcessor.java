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
    /**
     * Processes all elements annotated with {@link ExceptionRunner}.
     *
     * <p>This method is invoked by the annotation processing framework during
     * compilation. It scans the source code for methods annotated with
     * {@link ExceptionRunner}, validates them, and generates an implementation
     * class that wires together the exception-handling logic.</p>
     * @param annotations   the set of annotation types requested to be processed
     * @param roundEnvironment environment for information about the current and prior round
     * @return {@code true} if the annotations are claimed by this processor,
     *         {@code false} otherwise
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

        Set<? extends Element> annotatedElementWithRunException = roundEnvironment.getElementsAnnotatedWith(ExceptionRunner.class);

        List<Element> validAnnotations = getValidAnnotations(annotatedElementWithRunException);
        if (validAnnotations.isEmpty()) {
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


    /**
     * Filters and collects the elements annotated with {@link ExceptionRunner}.
     * @param annotatedElementWithRunException the set of elements annotated with {@link ExceptionRunner}
     * @return a list containing all valid annotated elements (currently identical to the input set)
     */
    private List<Element> getValidAnnotations(Set<? extends Element> annotatedElementWithRunException) {
        List<Element> elements = new ArrayList<>();
        for (Element element : annotatedElementWithRunException) {
            elements.add(element);
        }
        return elements;
    }

    /**
     * Extracts package and interface information from a method
     * annotated with {@link ExceptionRunner}.
     *
     * @param validAnnotation the annotated method element
     * @return a container with package and interface name
     */
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

    /**
     * Generates the implementation class source file for an interface
     * annotated with {@link ExceptionRunner}.
     *
     * <p>This method writes the package declaration, class header,
     * and all method implementations derived from the annotated
     * elements. It delegates the details of imports/class definition
     * to {@code writeImportsAndClassImpl(...)} and the method bodies
     * to {@code writerRunnerMethodsExceptionsImpl(...)}.</p>
     *
     * @param javaFileObject the target source file to write
     * @param packageName    the package where the implementation will be generated
     * @param interfaceName  the name of the annotated interface
     * @param validAnnotations the list of valid annotated elements to process
     * @throws RuntimeException if an error occurs while writing the file
     */
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


    /**
     * Writes the import statements and the class declaration for the generated
     * implementation of an interface annotated with {@link ExceptionRunner}.
     *
     * <p>For each annotated method, this method imports the corresponding
     * exception class. It also checks that all methods within the same
     * interface declare the same {@code componentModel}. If the models differ,
     * a compilation error is reported.</p>
     *
     * <p>If the component model is {@code spring}, the generated class is
     * annotated with  org.springframework.stereotype.Component.</p>
     *
     * @param writer          the writer used to output the generated source
     * @param packageName     the package of the generated class
     * @param validAnnotations the list of annotated methods to process
     * @param interfaceName   the name of the interface being implemented
     * @throws IOException if an error occurs while writing to the file
     */
    private void writeImportsAndClassImpl(Writer writer, String packageName, List<Element> validAnnotations, String interfaceName) throws IOException {

        String exceptionNameClass;
        String componetModel;

        ExecutableElement methodElement;
        String generatedNameClass = interfaceName + "Impl";

        List<Boolean> componentModelEqualsSpringList = new ArrayList<>();

        writer.write("import org.springframework.stereotype.Component;\n");
        for (Element element : validAnnotations) {
            methodElement = (ExecutableElement) element;
            exceptionNameClass = methodElement.getAnnotation(ExceptionRunner.class).exceptionClass();
            componetModel = methodElement.getAnnotation(ExceptionRunner.class).componentModel();
            if (componetModel.equalsIgnoreCase("spring")) {
                componentModelEqualsSpringList.add(true);
            }else{
                componentModelEqualsSpringList.add(false);
            }
            writer.write("import " + packageName + "." + exceptionNameClass + ";\n\n");
        }
        boolean hasSpring = componentModelEqualsSpringList.stream().allMatch(Boolean::booleanValue);
        if (!hasSpring) {
            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "All @ExceptionRunner in the same interface, must contains the same componentModel");
            return;
        }

        writer.write("import org.springframework.stereotype.Component;\n");
        writer.write("@Component\n");
        writer.write("public class " + generatedNameClass + " implements " + interfaceName + " {\n\n");
    }

    /**
     * Writes the method implementations for all {@link ExceptionRunner}-annotated
     * methods in the generated class.
     *
     * <p>For each annotated method, this method generates an overriding method
     * that immediately throws the corresponding exception defined in the
     * {@link ExceptionRunner} annotation.</p>
     *
     * @param writer           the writer used to output the generated source
     * @param validAnnotations the list of annotated method elements to process
     * @throws IOException if an error occurs while writing to the file
     */
    private void writerRunnerMethodsExceptionsImpl(Writer writer, List<Element> validAnnotations) throws IOException {

        List<RunnerMethodTypesException> runnerList = createListForMakeExceptionRunnerMethods(validAnnotations);

        for (RunnerMethodTypesException runnerMethod : runnerList) {
            writer.write("\t@Override\n");
            writer.write("    public " + runnerMethod.getReturnMethodType() + " " + runnerMethod.getMethodName() + "(" + runnerMethod.getMethodArguments() + ")"
                    + " {\n");
            writer.write("        throw new " + runnerMethod.getExceptionNameClass() + "(" + runnerMethod.getVariableList() + ");\n");
            writer.write("    }\n\n");
        }
    }

    /**
     * Builds a list of {@link RunnerMethodTypesException} objects from the
     * methods annotated with {@link ExceptionRunner}.
     *
     * <p>For each annotated method, this method extracts:</p>
     * <ul>
     *   <li>the return type</li>
     *   <li>the method name</li>
     *   <li>the full method arguments (types and names)</li>
     *   <li>the exception class name declared in {@link ExceptionRunner#exceptionClass()}</li>
     *   <li>the variable list (only parameter names, comma-separated)</li>
     * </ul>
     *
     * <p>Each set of extracted values is stored in a new
     * {@link RunnerMethodTypesException} instance, which is then added
     * to the result list.</p>
     *
     * @param validAnnotations the list of method elements annotated with {@link ExceptionRunner}
     * @return a list of {@link RunnerMethodTypesException} containing metadata for code generation
     */
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

    /**
     * Builds a list of method parameter signatures from the given elements.
     *
     * <p>Each parameter is represented as a string in the form
     * {@code "<type> <name>"}, preserving both its type and variable name.
     * This list is later used to reconstruct method signatures in the
     * generated implementation.</p>
     *
     * @param methodParameters the list of method parameters to process
     * @return a list of strings, each containing the type and name of a parameter
     */
    private List<String> getTypesAndMethodParametersAndConcatenatesThem(List<? extends VariableElement> methodParameters) {
        List<String> typeParameters = new ArrayList<>();
        for (VariableElement param : methodParameters) {
            String paramName = param.getSimpleName().toString();
            String paramType = param.asType().toString();
            typeParameters.add(paramType + " " + paramName);
        }
        return typeParameters;
    }

    /**
     * Prints diagnostic information at compile time about the annotated element.
     *
     * <p>This method logs the method name, the enclosing interface name,
     * and the package name to the compiler's messager with {@link Diagnostic.Kind#NOTE} level.
     * It is mainly used for debugging and tracking during annotation processing.</p>
     *
     * @param methodName    the name of the annotated method
     * @param interfaceName the name of the enclosing interface
     * @param packageName   the package where the interface is declared
     */
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
