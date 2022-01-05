`BRouter`

一个简单的路由库，路由到目标类执行目标方法。
利用`APT`方式生成路由表文件，`反射`路由到目标方法并执行。

### 支持
- 支持各模块间页面跳转，解依赖路由类名、路由方法；
- 路由方法的参数支持各种类型，如无参数、已有类型、自定义类、内部类类型，基本类型及其包装类、集合类等；

### 使用
- 配置：在支持路由跳转的Module的build.gradle里可以申明注解处理类名'com.lb.brouter.compiler.BRouterAnnotationProcessor'，
并定义BRouterIndex路由表的全路径名称，不同Module必须设置不同的全路径的路由表名称，如 'com.lb.brouter.MyBRouterIndex'，
并在dependencies里添加 annotationProcessor project(':brouter_compiler')；
- 注册：在业务线初始化时，通过{@link #registerRouterIndex(String...)}注册所有Module的路由表；
- 注解：用{@link com.lb.brouter.anno.BRouter}注解路由页面，用{@link com.lb.brouter.anno.BRouterMethod}注解路由方法；
- 路由：通过{@link #route(String, Object...)}路由到页面的路由方法；
- 异常：如果路由异常（{@link #route(String, Object...)}会抛出），可做降级处理；
- 路由参数：自定义路由参数必须实现Serializable或者用Keep注解，防止被混淆导致路由失败；
- proguard：
```
    -keep interface com.lb.brouter.api.internal.BRouterIndex {
  		<fields>;
		<methods>;
	}
	-keep class * implements com.lb.brouter.api.internal.BRouterIndex {*;}
	-keep,allowobfuscation @interface com.lb.brouter.anno.BRouter
	-keep,allowobfuscation @interface com.lb.brouter.anno.BRouterMethod
	-keep @com.lb.brouter.anno.BRouter class *
	-keepclassmembers class * {
		@com.lb.brouter.anno.BRouterMethod <methods>;
	}
```

### 约定
- {@link com.lb.brouter.anno.BRouter}的path大小写不敏感，不可重复。如 '/fragment/PageA' ；
- 路由方法必须是 public static修饰的；
- 路由时传递的实参，要求个数、类型、顺序与目标方法的一一对应，否则路由失败；
- 路由时，假定目标方法fun1的参数是超类A，路由方法fun2的参数是A的子类B，如果用B的对象做实参执行路由，可能会调起fun1而不是fun2，所以请避免这种父子类参数情况；
- 路由时，参数个数不能超过20个，否则路由失败；
- 重要：使用时请做好充分测试 ！！
