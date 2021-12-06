import com.github.zou.rpc.register.constant.enums.RegisterTypeEnum;
import com.github.zou.rpc.register.core.RegisterBs;
import com.github.zou.rpc.register.support.register.URL;

/**
 * @author zou
 * @since 1.0.0
 */
public class Register {
    public static void main(String[] args) {
        URL url = new URL("localhost:2181");
        RegisterBs.newInstance().setTypeEnum(RegisterTypeEnum.ZOOKEEPER).setUrl(url).start();
    }
}
