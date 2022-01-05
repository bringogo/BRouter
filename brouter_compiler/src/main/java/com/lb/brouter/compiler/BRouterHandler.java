package com.lb.brouter.compiler;

import com.lb.brouter.anno.BRouter;
import com.lb.brouter.anno.BRouterMethod;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

/**
 * 路由能力处理。
 *
 * <p>主要功能：
 * <ul>
 *  <li> 收集被注解的路由页面、路由方法信息，根据配置的路由表名称生成路由表类文件，把路由信息加入文件；
 *  <li> 检查错误的配置并给提示
 * </ul>
 * <p>
 * Created by lb on 2022/01/01
 */
public class BRouterHandler {
    private static final String OPTION_ROUTER_INDEX = "BRouterIndex";
    private static final String TAG = "[BRouter]";

    private final String routerIndex;
    private final Map<String, RouterElement> routerElementMap = new HashMap<>();

    private final ProcessingEnvironment processingEnvironment;
    private final Types typeUtils;
    private final Elements elementUtils;
    private final Messager messager;

    public BRouterHandler(ProcessingEnvironment processingEnvironment, Types typeUtils,
                          Elements elementUtils, Messager messager) {
        this.processingEnvironment = processingEnvironment;
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.messager = messager;
        this.routerIndex = processingEnvironment.getOptions().get(OPTION_ROUTER_INDEX);
        if (this.routerIndex == null) {
            String err =
                    TAG + "  No option '" + OPTION_ROUTER_INDEX + "' defined in build.gradle";
            messager.printMessage(Kind.ERROR, err);
            throw new RuntimeException(err);
        }
        messager.printMessage(Kind.NOTE, TAG + " Create BRouterHandler: " + this);
    }

    public void handleBRouter(RoundEnvironment roundEnvironment) {
        messager.printMessage(Kind.NOTE, TAG + " BRouterHandler handleBRouter");
        collectRouterElements(roundEnvironment);
        if (routerElementMap.isEmpty()) {
            messager
                    .printMessage(Kind.WARNING, TAG + "  No '@BRouter' annotated class found");
        } else {
            createRouterIndexFile();
        }
    }

    private void collectRouterElements(RoundEnvironment environment) {
        collectBRouter(environment);
        collectBRouterMethods(environment);
    }

    private void collectBRouter(RoundEnvironment environment) {
        Set<? extends Element> routerElements = environment.getElementsAnnotatedWith(BRouter.class);
        if (routerElements == null || routerElements.isEmpty()) {
            return;
        }
        for (Element element : routerElements) {
            TypeElement typeElement = (TypeElement) element;
            BRouter route = element.getAnnotation(BRouter.class);
            String path = route.path().toLowerCase();
            String desc = route.desc();
            // 检查注解是否合法并收集
//            if (checkRouterValidation(typeElement, path)) {
            if (routerElementMap.containsKey(path)) {
                String err = TAG + "  Duplicate path: " + path;
                messager.printMessage(Kind.ERROR, err);
                throw new RuntimeException(err);
            }
            routerElementMap
                    .put(path, new RouterElement(route.toString(), path, desc, typeElement));
//            }
        }
    }

    private void collectBRouterMethods(RoundEnvironment environment) {
        if (routerElementMap.isEmpty()) {
            messager.printMessage(Kind.WARNING, TAG
                    + "  No '@BRouter' annotated class, thus ignore parsing '@BRouterMethod' annotated methods");
            return;
        }

        Set<? extends Element> methodElements = environment.getElementsAnnotatedWith(
                BRouterMethod.class);
        if (methodElements == null || methodElements.isEmpty()) {
            messager
                    .printMessage(Kind.WARNING, TAG + "  No '@BRouterMethod' annotated method found");
            return;
        }

        for (Element element : methodElements) {
            if (!element.getModifiers().contains(Modifier.PUBLIC)) {
                String err = TAG + "  Method annotated with '@BRouterMethod' must be public !";
                messager.printMessage(Kind.ERROR, err);
                throw new RuntimeException(err);
            }

            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            String classFullName = enclosingElement.getQualifiedName().toString();
            for (String path : routerElementMap.keySet()) {
                if (classFullName.equals(routerElementMap.get(path).classFullName)) {
                    routerElementMap.get(path).addMethod(element);
                    break;
                }
            }
        }
    }

    private boolean checkRouterValidation(TypeElement typeElement, String path) {
        return true;
    }

    /**
     * 生成路由表类文件。 在目录 build/generated/ap_generated_sources/debug/out/ 下
     */
    private void createRouterIndexFile() {
        BufferedWriter writer = null;
        try {
            JavaFileObject sourceFile = processingEnvironment.getFiler()
                    .createSourceFile(routerIndex);
            int lastDot = routerIndex.lastIndexOf('.');
            String pkg = lastDot > 0 ? routerIndex.substring(0, lastDot) : null;
            String clazz = routerIndex.substring(lastDot + 1);
            writer = new BufferedWriter(sourceFile.openWriter());

            writer.write("package " + pkg + ";\n\n");
            writer.write("import com.lb.brouter.api.internal.BRouterIndex;\n");
            writer.write("import com.lb.brouter.api.internal.BRouterInfo;\n\n");
            writer.write("/** Automatically generated file. DO NOT EDIT.  Generated Date: "
                    + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date())
                    + " */\n");
            writer.write("public class " + clazz + " implements BRouterIndex {\n\n");
            writer.write("    private BRouterInfo routerInfo = new BRouterInfo();\n\n");
            writer.write("    public " + clazz + "() {\n");
            writeRoutersSource(writer);
            writer.write("    }\n\n");
            writer.write("    @Override\n");
            writer.write("    public BRouterInfo getRouterInfo() {\n");
            writer.write("        return routerInfo;\n");
            writer.write("    }\n");
            writer.write("}\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            String err = TAG + "  Err in writing java source for index: " + routerIndex;
            messager.printMessage(Kind.ERROR, err);
            throw new RuntimeException(err, e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // Silent
                }
            }
        }
    }

    private void writeRoutersSource(BufferedWriter writer) throws IOException {
        for (String path : routerElementMap.keySet()) {
            RouterElement element = routerElementMap.get(path);
            if (element.methodElements != null) {
                for (Element methodElement : element.methodElements) {
                    if (methodElement instanceof ExecutableElement) {
                        ExecutableElement executableElement = (ExecutableElement) methodElement;
                        // 注释
                        writer.write(
                                "        // " + element.routerString + " | return type:"
                                        + executableElement.getReturnType() + "\n");
                        // add 路由方法
                        writer.write("        routerInfo.addRouter(\"" + element.path + "\", "
                                + "\"" + element.classFullName + "\", "
                                + "\"" + executableElement.getSimpleName() + "\"");
                        List<? extends VariableElement> variableElements = executableElement
                                .getParameters();
                        if (variableElements != null && !variableElements.isEmpty()) {
                            writer.write(", ");
                            for (int i = 0; i < variableElements.size(); i++) {
                                String className = typeStrExchange(variableElements.get(i));
                                writer.write("\"" + className + "\"");
                                if (i < variableElements.size() - 1) {
                                    writer.write(", ");
                                }
                            }
                        }
                        writer.write(");" + "\n");
                    } else {
                        writer.write("  // other than ExecutableElement ???\n");
                    }
                }
            }
        }
    }

    private String typeStrExchange(@Nonnull Element element) {
        return element.asType().toString();
    }

    private static class RouterElement {
        private String routerString;
        private String path;
        private String desc;
        private String classFullName;
        private TypeElement routerElement;
        private List<Element> methodElements;

        private RouterElement(@Nonnull String routerString, @Nonnull String path, String desc,
                              @Nonnull TypeElement routerElement) {
            this.routerString = routerString;
            this.path = path;
            this.desc = desc;
            this.routerElement = routerElement;
            this.classFullName = routerElement.getQualifiedName().toString();
        }

        private void addMethod(@Nonnull Element methodElement) {
            if (this.methodElements == null) {
                this.methodElements = new ArrayList<>();
            }
            this.methodElements.add(methodElement);
        }
    }
}
