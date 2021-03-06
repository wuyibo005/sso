package win.scolia.cloud.sso.exception.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 参数错误
     *
     * @return 400
     */
    @ExceptionHandler(value = {MissingServletRequestParameterException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<String> bedRequestHandler(HttpServletRequest request) {
        if (LOGGER.isWarnEnabled()) {
            String param;
            try {
                param = MAPPER.writeValueAsString(request.getParameterMap());
            } catch (JsonProcessingException e) {
                param = null;
            }
            LOGGER.warn("{} try to access {}, but parameter error in: {}", request.getRemoteHost(),
                    request.getRequestURI(), param);
        }
        return ResponseEntity.badRequest().body("Parameter error");
    }

    /**
     * 不支持的媒体类型
     * @return 415
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Void> mediaType() {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
    }

    /**
     * 未认证
     *
     * @return 401
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<Void> unauthorizedHandler(HttpServletRequest request) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("{} try to access {}, but not signin", request.getRemoteHost(), request.getRequestURI());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * 权限不足时的异常处理
     *
     * @return 403
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Void> forbiddenHandler(HttpServletRequest request) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("{} try to access {}, but miss permission", request.getRemoteHost(), request.getRequestURI());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * 请求方法不允许
     *
     * @return 405
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Void> methodNotAllowedHandler(HttpServletRequest request) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("{} try to access {}, but method not allowed", request.getRemoteHost(), request.getRequestURI());
        }
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    /**
     * 通用异常处理
     *
     * @return 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> exceptionHandler(HttpServletRequest request, Exception e) {
        LOGGER.error("Error happen in: {}", request.getRequestURI(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
