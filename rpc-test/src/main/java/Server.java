import com.github.zou.rpc.server.core.ServiceBs;

/**
 * @author zou
 * @since 1.0.0
 */
public class Server {
    public static void main(String[] args) {
        ServiceBs.getInstance()
                .register("testServiceImpl", new TestServiceImpl())
                .registerCenter("localhost:8527")
                .expose();
    }
}
