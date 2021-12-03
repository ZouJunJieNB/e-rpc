/**
 * @author zou
 * @since 1.0.0
 */
public class TestServiceImpl implements TestService{
    @Override
    public String sayHello()  {
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return "hello";
    }
}
