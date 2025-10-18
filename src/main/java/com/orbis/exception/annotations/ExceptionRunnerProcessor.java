package com.orbis.exception.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.orbis.exception.annotations.ExceptionRunner")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class ExceptionRunnerProcessor extends AbstractProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

		Set<? extends Element> annotatedElementWithRunException =
				roundEnvironment.getElementsAnnotatedWith(ExceptionRunner.class);
		ExecutableElement methodElement;
		Element enclosingElement;
		PackageElement packageElement;
		List<? extends VariableElement> methodParameters;
		List<String> typeParameters = new ArrayList<>();

		String methodName;
		String interfaceName;
		String packageName;
		String exceptionNameClass;
		String returnMethodType;

		for (Element element : annotatedElementWithRunException) {
			if (element.getKind() != ElementKind.METHOD) continue;

			methodElement = (ExecutableElement) element;
			methodName = methodElement.getSimpleName().toString();
			returnMethodType = methodElement.getReturnType().toString();
			methodParameters = methodElement.getParameters();

			String componetModel = methodElement.getAnnotation(ExceptionRunner.class).componentModel();

			for (VariableElement param : methodParameters) {
				String paramName = param.getSimpleName().toString();
				String paramType = param.asType().toString();
				typeParameters.add(paramType + " " + paramName);
			}

			exceptionNameClass = methodElement.getAnnotation(ExceptionRunner.class).exceptionMethod();

			enclosingElement = methodElement.getEnclosingElement();
			interfaceName = enclosingElement.getSimpleName().toString();

			packageElement = processingEnv.getElementUtils().getPackageOf(enclosingElement);
			packageName = packageElement.getQualifiedName().toString();

			printerAtCompileTime(methodName, interfaceName, packageName, exceptionNameClass);

			try {
				JavaFileObject javaFileObject =
						processingEnv.getFiler().createSourceFile(packageName + "." + interfaceName + "Impl");
				writeClassImpl(
						javaFileObject,
						methodName,
						packageName,
						interfaceName,
						exceptionNameClass,
						returnMethodType,
						componetModel,
						typeParameters);
			} catch (Exception e) {
				processingEnv
						.getMessager()
						.printMessage(
								Diagnostic.Kind.ERROR, "error:" + e.getLocalizedMessage() + "/t" + Arrays.toString(e.getStackTrace()));
			}
		}
		return true;
	}

	public void printerAtCompileTime(
			String methodName,
			String interfaceName,
			String packageName,
			String exceptionNameClass
			){
		processingEnv.getMessager()
				.printMessage(Diagnostic.Kind.NOTE, "Annotated method: " + methodName);
		processingEnv
				.getMessager().printMessage(Diagnostic.Kind.NOTE, "Interface's name: " + interfaceName);
		processingEnv.getMessager()
				.printMessage(Diagnostic.Kind.NOTE, "Package: " + packageName);

		processingEnv.getMessager()
				.printMessage(Diagnostic.Kind.NOTE, "Exception class name: " + exceptionNameClass);
	}

	public void writeClassImpl(
			JavaFileObject javaFileObject,
			String methodName,
			String packageName,
			String interfaceName,
			String exceptionNameClass,
			String returnMethodType,
			String componentModel,
			List<String> typeParameters) {

		String generatedNameClass = interfaceName + "Impl";
		String methodArguments = String.join(",", typeParameters);


		try (Writer writer = javaFileObject.openWriter()) {
			writer.write("package " + packageName + ";\n\n");
			writer.write("import " + packageName + "." + exceptionNameClass + ";\n\n");
			writer.write("import org.springframework.stereotype.Component;");


			writer.write(componentModel.equalsIgnoreCase("no spring") ? "": "@Component\n");
			writer.write("public class " + generatedNameClass + " implements " + interfaceName + " {\n\n");

			writer.write("\t@Override\n");
			writer.write("    public " + returnMethodType + " " + methodName + "(" + methodArguments + ")" + " {\n");
			writer.write("        throw new CustomException(message);\n");
			writer.write("    }\n\n");

			writer.write("}\n");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
