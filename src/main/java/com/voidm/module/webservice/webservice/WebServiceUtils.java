package com.voidm.module.webservice.webservice;


import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import javax.xml.namespace.QName;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author voidm
 * @date 2019/1/23
 */
public class WebServiceUtils {

    private static final String USERNAME = "void";
    private static final String PASSWORD = "void";
    private static final String NAME_SPACE = "http://www.shijinet.com.cn/kunlun/kws/1.1/";
    private static final String WSDL_URL = "http://58.251.19.224:8081/kws/SecurityService.asmx?WSDL";
    private static final String ACTION_URI = "http://www.shijinet.com.cn/kunlun/kws/1.1/AppLogin";


    public static void main(String[] args) throws AxisFault {
        // forHttpClient();
        // forAxis2();
        // forAxis1RPC();
        // forAxis2RPC();

    }

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
            postMethod.setRequestEntity(new StringRequestEntity(xml, "text/xml", "GBK"));
            postMethod.addRequestHeader("Connection", "close");
            postMethod.addRequestHeader("SOAPAction", "http://www.shijinet.com.cn/kunlun/kws/1.1/AppLogin");

            //设置一个返回的状态值用以判断是否调用webservice成功
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

    private static void forAxis1RPC() {
        // try {
        //     // 直接引用远程的wsdl文件
        //     // 以下都是套路
        //     Service service = new Service();
        //     Call call = (Call) service.createCall();
        //     // WSDL里面描述的接口名称
        //     call.setOperationName(new QName(NAME_SPACE, "AppLogin"));
        //     call.setTargetEndpointAddress(WSDL_URL);
        //     // 接口的参数
        //     call.addParameter("username", XMLType.XSD_STRING, ParameterMode.IN);
        //     call.addParameter("password", XMLType.XSD_STRING, ParameterMode.IN);
        //     // 设置返回类型
        //     call.setReturnType(XMLType.XSD_BOOLEAN);
        //
        //     // 设置SOAPAction
        //     call.setUseSOAPAction(true);
        //     call.setSOAPActionURI(ACTION_URI);
        //     String result = call.invoke(new Object[]{USERNAME, PASSWORD}).toString();
        //     // 给方法传递参数，并且调用方法
        //     System.out.println(Boolean.valueOf(result) ? "登录成功" : "登录失败");
        // } catch (Exception e) {
        //     System.err.println(e.toString());
        // }
    }

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

}