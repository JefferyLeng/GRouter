## GRouter组件化路由使用

### GRouter简介

GRouter是基于组件化的路由架构,使用APT + JavaPoet来构建生成 "Group" 和 "Path" 来实现路由通信，支持

参数传递。 优化改进了ARouter的类加载策略和兼容性问题。

注：每个业务模块称为group  每个界面访问的路径称为path

### 组件化配置

1. 修改config.gradle中isRelease变量

```
// false 每个moudle都是apk单独打包  true 集成化打包  业务模块变成moudle打包
isRelease = true
```

2.业务moudle build.gradle配置

```
sourceSets {
    main {
        if (!isRelease) {
            //当前是组件化模式
            manifest.srcFile "src/main/debug/AndroidManifest.xml"
        } else {
            manifest.srcFile "src/main/AndroidManifest.xml"
            java {
                //集成化 debug保重的class不合并到dex
                exclude '**/debug/**'
            }
        }
    }
}
```

3.模块化模式下，清单文件和测试文件 放到 debug文件夹下

### 路由gradle配置说明

1. config.gradle中定义 packageNameForAPT 来存放apt生成的文件路径（建议和app包名一致）

```
// 包名，用于存放APT生成的类文件
packageNameForAPT = "cc.guider.architeature.router.apt"
```

2. 每个moudle下的build.gradle需要增加传参配置

```
javaCompileOptions {
    annotationProcessorOptions {
        arguments = [GROUTER_MODULE_NAME: project.getName(), APT_LOCATION_NAME: packageNameForAPT]
    }
}
```

### 使用说明

1. 需要加入GRouter的类 加入@GRouter注解，path 中传入 /组名（moudle名）/Activity名，组名可以不传，GRouter自动获取

```
@GRouter(path = "/app/MainActivity")
```

2. 如果需要接收传递的参数，则在成员变量声明@Parameter注解,name如果为空，GRouter默认将通过成员变量名获取参数，需要在接收参数的Activity中调用ParameterManager注册

```
@Parameter
String name;

@Parameter(name = "agep")
int age;

//注册ParameterManager接收参数
ParameterManager.getInstance().loadParameter(this);
```

3. 调用跳转API

```
RouterManager.getInstance()
        .build("/orderbiz/OrderBiz_MainActivity")  //其他模块的跳转路径
        .withString("name", "jeffery")     		   //传递String参数 name
        .withInt("agep",23)                        //传递Int参数 agep
        .navigation(this);                         //跳转

//封装Bundle跳转       
Bundle bundle = new Bundle();
bundle.putString("userName", "jeffery");
bundle.putInt("age", 23);
bundle.putBoolean("isSuccess", true);
bundle.putString("email", "JefferyLeng@guider.cc");

RouterManager.getInstance()
	.build("/minecenter/MineCenter_MainActivity")
    .withBundle(bundle)
    //未调用withResultXXX() 第二个参数的code为requestCode
    .navigation(this, 110);  
    
// startActivityForResult 打开Activity 需要setResult和finish
RouterManager.getInstance()
    .build("/app/MainActivity")
    .withResultInt("orderBackResult",156)
    //调用withResultXXX  GRouter自动区分code为resultCode          
    .navigation(this,001);

```

4. 调用其他moudle的业务（面向接口设计）如：App模块调用Order模块的一个网络请求

```
//在业务通用library或commonLibrary中定义接口 必须继承BizCall
public interface OrderAddress extends BizCall {
    OrderAddressResponseBean getOrderAddress(String phone);
}
//在order moudle中实现接口 并声明GRouter注解
@GRouter(path = "/orderbiz/getOrderAddress")
public class OrderAddressImpl implements OrderAddress {

    private final static String BASE_URL = "http://apis.juhe.cn/";

    @Override
    public OrderAddressResponseBean getOrderAddress(String phone) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        OrderRequestService host = retrofit.create(OrderRequestService.class);

        // Retrofit GET同步请求
        Call<ResponseBody> call = host.get(phone, "5c7340e52bd7a64f4bc4caa440efe250");
        try {
            Response<ResponseBody> response  = call.execute();
            if (response != null && response.body() != null) {
                JSONObject jsonObject = JSON.parseObject(response.body().string());
                OrderAddressResponseBean orderBean = jsonObject.toJavaObject(OrderAddressResponseBean.class);
                Log.d(CommonUtil.LOG_TAG, "getOrderAddress json : " + orderBean.toString());
                return orderBean;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

//调用的Activity中声明 GRouter会自动为属性赋值
 @Parameter(name = "/orderbiz/getOrderAddress")
 OrderAddress orderAddress;
 //直接调用order业务 OK
 responseBean = orderAddress.getOrderAddress("1562103");
```
