package com.edu.gateway.interceptor;

import com.alibaba.fastjson.JSON;
import com.edu.lw.model.bean.CodeMsg;
import com.edu.lw.model.response.dto.ResponseDTO;
import com.edu.lw.model.user.dto.UserDTO;
import com.lw.api.system_services.IUserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 登录拦截器
 * @author 17259
 *
 */


/**
 * 登录拦截器，用于验证用户登录状态。
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

	private Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

	@Resource
	private IUserClient userClient;

	/**
	 * 在请求处理之前执行预处理操作，包括验证用户登录状态。
	 *
	 * @param request  HTTP请求对象
	 * @param response HTTP响应对象
	 * @param handler  处理程序对象
	 * @return 如果预处理成功，则返回true；否则返回false，请求不会继续进行
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
		// 设置跨域请求头
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "*");
		response.setContentType("application/json; charset=utf-8");
		// 获取请求方法
		String method = request.getMethod();
		if("OPTIONS".equalsIgnoreCase(method)) {
			// 如果是OPTIONS测试请求，则直接返回测试成功
			try {
				//JSON.parseObject，是将Json字符串转化为相应的对象；JSON.toJSONString则是将对象转化为Json字符串。
				response.getWriter().print(JSON.toJSONString(ResponseDTO.success(true)));
				return false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 获取请求头中的token信息
		String token = request.getHeader("token");
		log.info("接受到的token={}", token);
		// 调用用户服务验证登录状态
		UserDTO userDTO = new UserDTO();
		userDTO.setToken(token);
		ResponseDTO<UserDTO> responseDTO = userClient.checkLogin(userDTO);
		if(responseDTO.getCode() != 0) {
			try {
				//JSON.parseObject，是将Json字符串转化为相应的对象；JSON.toJSONString则是将对象转化为Json字符串。
				response.getWriter().print(JSON.toJSONString(ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED)));
				return false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
