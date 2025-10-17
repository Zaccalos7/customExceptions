package com.orbis.exception.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.orbis.exception.annotations.RunException")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class ExceptionRunnerProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

        Set<? extends Element> annotatedElementWithRunException = roundEnvironment.getElementsAnnotatedWith(ExceptionRunner.class);

        for(Element element : annotatedElementWithRunException){
            if(element.getKind() != ElementKind.METHOD)
                continue;

            ExecutableElement methodName = (ExecutableElement) element;
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "Method's name with @RunException: " + methodName
            );
            String exceptionNameClass = methodName.getAnnotation(ExceptionRunner.class).exceptionNameClass();

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, exceptionNameClass);

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, " "+methodName.getEnclosingElement());
        }
        return true;
    }
}
