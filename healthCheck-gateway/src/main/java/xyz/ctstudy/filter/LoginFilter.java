package xyz.ctstudy.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import xyz.ctstudy.common.Result;
import xyz.ctstudy.utils.JWTUtils;
import xyz.ctstudy.utils.PathMatchUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

/**
 * 登录成功后创建token，返回给前端
 */
@Component
@Slf4j
public class LoginFilter extends ZuulFilter {

    @Value("${common.login.url}")
    private String loginUrl;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        //只有路径与配置文件中配置的登录路径相匹配，才会放行该过滤器,执行过滤操作
        RequestContext ctx = RequestContext.getCurrentContext();
        String requestURI = ctx.getRequest().getRequestURI();
        for (String url : loginUrl.split(",")) {
            if (PathMatchUtil.isPathMatch(url, requestURI)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        try {
            log.info("执行了zuul的LoginFilter");
            InputStream stream = ctx.getResponseDataStream();
            String body = StreamUtils.copyToString(stream, StandardCharsets.UTF_8);
            Result<HashMap<String, Object>> result = objectMapper.readValue(body, new TypeReference<Result<HashMap<String, Object>>>() {
            });
            //result.getCode() == 0 表示登录成功
            if (result.getCode() == 0) {
                log.info("登录成功");
                HashMap<String, Object> jwtClaims = new HashMap<String, Object>() {{
                    //原本token中放的是openid，现在将openid作为Payload（有效载荷）
                    put("openid", result.getData().get("token"));
                }};
                Date expDate = DateTime.now().plusDays(7).toDate(); //过期时间 7 天

                String token = JWTUtils.createJWT(expDate, jwtClaims);
                //body json更新token
                result.getData().put("token", token);
                //序列化body json,设置到响应body中
                body = objectMapper.writeValueAsString(result);
                ctx.setResponseBody(body);
                //防止乱码
                ctx.getResponse().setContentType("application/json;charset=utf-8");

            }else{
                log.info("登录失败");
                //序列化body json,设置到响应body中
                body = objectMapper.writeValueAsString(result);
                ctx.setResponseBody(body);
                //防止乱码
                ctx.getResponse().setContentType("application/json;charset=utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
