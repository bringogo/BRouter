package com.lb.brouter.compiler;

import com.google.auto.service.AutoService;
import com.lb.brouter.anno.BRouter;
import com.lb.brouter.anno.BRouterMethod;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedOptions(value = {"BRouterIndex"})
public class BRouterAnnotationProcessor extends AbstractProcessor {
    private BRouterHandler bRouterHandler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        bRouterHandler = new BRouterHandler(processingEnv, processingEnv.getTypeUtils(),
                processingEnv.getElementUtils(), processingEnv.getMessager());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BRouter.class.getCanonicalName());
        types.add(BRouterMethod.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!annotations.isEmpty()) {
            bRouterHandler.handleBRouter(roundEnv);
        }
        return false;
    }
}
