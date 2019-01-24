> Java调用WebService的几种方式

## Constants 常量
```java
	// WebService俩个参数,帐号跟密码
    private static final String USERNAME = "void";
    private static final String PASSWORD = "password";

	// namespace工作空间
    private static final String NAME_SPACE = "http://xxx/kunlun/kws/1.1/";
	// 接口wsdl地址
    private static final String WSDL_URL = "http://xxx/kws/SecurityService.asmx?WSDL";
	// SOAPAction地址
    private static final String ACTION_URI = "http://xxx/kunlun/kws/1.1/AppLogin";
```

## 方式1.Axis2将wsdl转为java代码

### 转换源码

[点击下载](http://axis.apache.org/axis2/java/core/download.cgi "点击下载")

下载完毕后,解压 找到bin目录下的wsdl2java.bat  

[![](http://voidm.com/wp-content/uploads/2019/01/微信截图_20190123162208.png)](http://voidm.com/wp-content/uploads/2019/01/微信截图_20190123162208.png)

之后将WebService接口的wsdl文件保存到该目录下,命名为xxx.WSDL  

cmd下运行该命令生成java代码
```cmd
wsdl2java -uri SecurityService.wsdl -o C:\Users\voidm\Desktop\tmp
```
- url 对应WSDL文件名
- o 对应SRC对应输出文件夹

源码输出文件夹

[![](http://voidm.com/wp-content/uploads/2019/01/微信截图_20190123171827.png)](http://voidm.com/wp-content/uploads/2019/01/微信截图_20190123171827.png)


### Java实现
生成完毕后,将源码copy到ide,写一个Main方法运行  
直接new对象,运行方法即可,参数都生成好了

[![](http://voidm.com/wp-content/uploads/2019/01/微信截图_20190123162751-1024x291.png)](http://voidm.com/wp-content/uploads/2019/01/微信截图_20190123162751.png)
```java
    private static void forAxis2() {
        try {
            SecurityServiceStub securityServiceStub = new SecurityServiceStub();
            SecurityServiceStub.AppLogin params = new SecurityServiceStub.AppLogin();
            params.setUsername(USERNAME);
            params.setPassword(PASSWORD);
            SecurityServiceStub.AppLoginResponse response = securityServiceStub.appLogin(params);
            System.out.println(response.getAppLoginResult() ? "登录成功" : "登录失败");

        } catch (Exception e) {

        }
    }
```

## 方式2.maven引入Axis1包 远程调用

### Pom依赖

```xml
<dependency>
	<groupId>commons-discovery</groupId>
	<artifactId>commons-discovery</artifactId>
	<version>0.5</version>
</dependency>
        <dependency>
	<groupId>org.apache.axis</groupId>
	<artifactId>axis</artifactId>
	<version>1.4</version>
</dependency>
        <dependency>
	<groupId>javax.xml</groupId>
	<artifactId>jaxrpc</artifactId>
	<version>1.1</version>
</dependency>
```

### Java实现

注意下面这俩句一定要加,.Net的WebService似乎必须要在header里添加 SOAPAction
> call.setUseSOAPAction(true);
> call.setSOAPActionURI("http://xxxxx/kws/1.1/AppLogin");

```java
    private static void forAxis1RPC() {
        try {
            // 直接引用远程的wsdl文件
            // 以下都是套路
            Service service = new Service();
            Call call = (Call) service.createCall();
            // WSDL里面描述的接口名称
            call.setOperationName(new QName(NAME_SPACE, "AppLogin"));
            call.setTargetEndpointAddress(WSDL_URL);
            // 接口的参数
            call.addParameter("username", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("password", XMLType.XSD_STRING, ParameterMode.IN);
            // 设置返回类型
            call.setReturnType(XMLType.XSD_BOOLEAN);

            // 设置SOAPAction
            call.setUseSOAPAction(true);
            call.setSOAPActionURI(ACTION_URI);
            String result = call.invoke(new Object[]{USERNAME, PASSWORD}).toString();
            // 给方法传递参数，并且调用方法
            System.out.println(Boolean.valueOf(result) ? "登录成功" : "登录失败");
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
```
## 方式3.maven引入Axis2包 远程调用

### POM依赖
```xml
<dependency>
	<groupId>org.apache.axis2</groupId>
	<artifactId>axis2-spring</artifactId>
	<version>1.7.8</version>
</dependency>
<dependency>
	<groupId>org.apache.axis2</groupId>
	<artifactId>axis2-transport-http</artifactId>
	<version>1.7.8</version>
</dependency>
<dependency>
	<groupId>org.apache.axis2</groupId>
	<artifactId>axis2-transport-local</artifactId>
	<version>1.7.8</version>
</dependency>
<dependency>
	<groupId>org.apache.axis2</groupId>
	<artifactId>axis2-xmlbeans</artifactId>
	<version>1.7.8</version>
</dependency>
```

### Java实现

注意下面这俩句一定要加,.Net的WebService似乎必须要在header里添加 SOAPAction
> options.setAction(ACTION_URI);

```java
private static void forAxis2RPC() throws AxisFault {
	//  使用RPC方式调用WebService
	RPCServiceClient serviceClient = new RPCServiceClient();
	Options options = serviceClient.getOptions();
	//  指定调用WebService的URL
	EndpointReference targetEPR = new EndpointReference(WSDL_URL);
	options.setTo(targetEPR);
	options.setAction(ACTION_URI);
	// 指定AppLogin方法的参数值
	Object[] opAddEntryArgs = new Object[]{USERNAME, PASSWORD};
	Class[] classes = new Class[]{String.class};
	// 指定要调用的AppLogin方法及WSDL文件的命名空间
	QName opAddEntry = new QName(NAME_SPACE, "AppLogin");
	// 输出该方法的返回值
	System.out.println(serviceClient.invokeBlocking(opAddEntry, opAddEntryArgs, classes)[0].equals("false") ? "登录失败" : "登录成功");
}
```

## 方式4.HTTP方式调用


> 这里也要注意 添加header: SOAPAction,否则请求会失败
> postMethod.addRequestHeader("SOAPAction", ACTION_URI);


### Java实现
```java
    private static void forHttpClient() {
        //language=XML
        String xml = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <soap:Body>\n" +
                "        <AppLogin xmlns=\"" + NAME_SPACE + "\">\n" +
                "            <username>" + USERNAME + "</username>\n" +
                "            <password>" + PASSWORD + "</password>\n" +
                "        </AppLogin>\n" +
                "    </soap:Body>\n" +
                "</soap:Envelope>";

        //用来盛放返回值
        String result = "";
        PostMethod postMethod = new PostMethod(WSDL_URL);
        HttpClientParams httpClientParams = new HttpClientParams();

        //设置链接的访问时间
        httpClientParams.setConnectionManagerTimeout(999999);
        //设置超时时间
        httpClientParams.setSoTimeout(999999);

        //创建http线程
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpClient httpClient = new HttpClient(httpClientParams, connectionManager);
        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(11, true));
        try {

            //设置返回值的编码格式
            postMethod.setRequestEntity(new StringRequestEntity(xml, "text/xml", "UTF-8"));
            postMethod.addRequestHeader("SOAPAction", ACTION_URI);
			httpClient.executeMethod(postMethod);
            //下面还是老规矩进行流和字符串之间的转换
            InputStream out = postMethod.getResponseBodyAsStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(out));
            StringBuilder buffer = new StringBuilder();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            result = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            postMethod.releaseConnection();
        }
        System.out.println(result);
    }
```
### PostMan调试

[![](http://voidm.com/wp-content/uploads/2019/01/WX20190123-205653@2x-1024x898.png)](http://voidm.com/wp-content/uploads/2019/01/WX20190123-205653@2x.png)

## 总结
因为帐号密码是随便写的, 所以登录失败,权限不足 不知道是什么鬼...  
贴上接口Response响应

```xml
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope
	xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<soap:Header>
		<KwsSoapHeader
			xmlns="http://www.shijinet.com.cn/kunlun/kws/1.1/">
			<RetCode>0000</RetCode>
			<ErrReason>权限不够</ErrReason>
		</KwsSoapHeader>
	</soap:Header>
	<soap:Body>
		<AppLoginResponse
			xmlns="http://www.shijinet.com.cn/kunlun/kws/1.1/">
			<AppLoginResult>false</AppLoginResult>
		</AppLoginResponse>
	</soap:Body>
</soap:Envelope>
```

