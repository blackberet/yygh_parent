package com.atguigu.yygh.common.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2 //开启Swagger支持
public class Swagger2Config {

    @Bean
    public Docket adminApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("管理员组")
                .apiInfo(adminApiInfo())
                .select()
                //只显示admin路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }

    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder()
                .title("尚医通预约挂号平台")
                .description("尚医通预约挂号平台管理员系统")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "xxxx@qq.com"))
                .build();
    }

    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("第三方医院组")
                .apiInfo(webApiInfo())
                .select()
                //只显示api路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }

    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("尚医通预约挂号平台第三方医院")
                .description("尚医通预约挂号平台第三方医院接口")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "xxx@qq.com"))
                .build();
    }


    @Bean
    public Docket userApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户组")
                .apiInfo(userApiInfo())
                .select()
                //只显示admin路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/user/.*")))
                .build();
    }

    private ApiInfo userApiInfo(){
        return new ApiInfoBuilder()
                .title("尚医通预约挂号平台")
                .description("尚医通预约挂号平台用户系统")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "xxxx@qq.com"))
                .build();
    }

}
