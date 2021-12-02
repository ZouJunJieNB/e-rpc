import com.github.zou.rpc.common.support.inteceptor.RpcInterceptor;
import com.github.zou.rpc.common.support.inteceptor.RpcInterceptorContext;

import java.util.Arrays;

/**
 * @author zou
 * @since 1.0.0
 */
public class CostTimeInterceptor implements RpcInterceptor {
    @Override
    public void before(RpcInterceptorContext context) {
        System.out.println(Arrays.toString(context.params()));
    }

    @Override
    public void after(RpcInterceptorContext context) {
        System.out.println("after");
    }

    @Override
    public void exception(RpcInterceptorContext context) {

    }
}
