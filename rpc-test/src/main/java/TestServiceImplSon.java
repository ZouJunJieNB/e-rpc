/**
 * @author zou
 * @since 1.0.0
 */
public class TestServiceImplSon extends TestServiceImpl {
    @Override
    public String sayHello()  {
        if(true){
           return super.sayHello();
        }
        return "s";
    }
}
