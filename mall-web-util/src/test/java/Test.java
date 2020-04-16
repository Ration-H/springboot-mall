import com.gdou.mall.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

public class Test {


    @org.junit.Test
    public void JwtTest(){
        HashMap<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("username", "ashen");
        userInfoMap.put("userId", "1");
        String token = JwtUtil.encode("com.gdou.mall", userInfoMap, "127.0.0.1");
        System.out.println(token);

        Map<String, Object> decode = JwtUtil.decode(token+"", "com.gdou.mall", "127.0.0.1");
    }
}
