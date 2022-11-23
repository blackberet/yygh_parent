package com.atguigu.yygh.hosp.sentinel;

import com.atguigu.yygh.hosp.client.HospFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class HospFeignClientFallBack implements FallbackFactory<HospFeignClient> {
    @Override
    public HospFeignClient create(Throwable throwable) {
        return new HospFeignClient() {
            @Override
            public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
                return new ScheduleOrderVo();
            }
        };
    }
}
