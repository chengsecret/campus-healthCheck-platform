package xyz.ctstudy.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.ctstudy.common.Result;
import xyz.ctstudy.utils.JWTUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * ，拦截具体的业务接口，验证token
 */
@Component
@Slf4j
public class AuthFilter extends ZuulFilter {
    @Value("${exclude.auth.url}")
    private String excludeAuthUrl;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public String filterType() {
        //由于授权需要在请求之前调用，所以这里使用前置过滤器
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        //路径与配置文件中的不匹配，则执行过滤
        RequestContext ctx = RequestContext.getCurrentContext();
        String requestURI = ctx.getRequest().getRequestURI();
        List<String> excludesUrlList = Arrays.asList(excludeAuthUrl.split(","));
        return !excludesUrlList.contains(requestURI);
    }

    @Override
    public Object run() {
        log.info("执行了zuul的AuthFilter");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = request.getHeader("token");
        Claims claims;
        try {
            //解析没有异常则表示token验证通过，如有必要可根据自身需求增加验证逻辑
            claims = JWTUtils.parseJWT(token);
            log.info("token : {} 验证通过", token);
            //对请求进行路由
            ctx.setSendZuulResponse(true);
            //请求头加入userId，传给业务服务
            ctx.addZuulRequestHeader("openid", claims.get("openid").toString());
        } catch (ExpiredJwtException expiredJwtEx) {
            log.error("token : {} 过期", token );
            //不对请求进行路由
            ctx.setSendZuulResponse(false);
            responseError(ctx, -402, "token expired");
        } catch (Exception ex) {
            log.error("token : {} 验证失败" , token );
            //不对请求进行路由
            ctx.setSendZuulResponse(false);
            responseError(ctx, -401, "invalid token");
        }
        return null;
    }

    /**
     * 将异常信息响应给前端
     */
    private void responseError(RequestContext ctx, int code, String message) {
        HttpServletResponse response = ctx.getResponse();
        Result errResult = new Result();
        errResult.setCode(code);
        errResult.setMessage(message);
        ctx.setResponseBody(toJsonString(errResult));
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=utf-8");
    }

    private String toJsonString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("json序列化失败", e);
            return null;
        }
    }
}
