import com.github.zou.rpc.client.config.ReferenceConfig;
import com.github.zou.rpc.client.core.ClientBs;

/**
 * @author zou
 * @since 1.0.0
 */
public class Client {
    public static void main(String[] args) {
        // 服务配置信息
        ReferenceConfig<TestService> config = ClientBs.newInstance();
        config.serviceId("testServiceImpl");
        config.serviceInterface(TestService.class);
        // 自动发现服务
        config.subscribe(true);
        config.registerCenter("localhost:8527");
        // 拦截器测试
        config.rpcInterceptor(new CostTimeInterceptor());

        TestService testService = config.reference();

        System.out.println(testService.sayHello());

    }
}
