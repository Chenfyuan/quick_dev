#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.app.controller;


import cc.rc.framework.core.constant.RcConstant;
import cc.rc.framework.web.model.response.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ${package}.module.app.constant.AppConstant;


/**
 * @author linweijian
 */
@Slf4j
@Api(value = "APP鉴权接口", tags = {"APP鉴权接口"})
@RequestMapping(AppConstant.APP_MODULE_CONTEXT_PATH + RcConstant.Version.HTTP_API_VERSION_V1 + "/auth")
@RestController
public class AppAuthController {

    @ApiOperation(value = "登录", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/login")
    public ApiResult<?> login() {
        /*
        编码时请参考AdminAuthController${symbol_pound}login
         */

        return ApiResult.success();
    }

}
