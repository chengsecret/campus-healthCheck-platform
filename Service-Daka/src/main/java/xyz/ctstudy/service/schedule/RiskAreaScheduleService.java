package xyz.ctstudy.service.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xyz.ctstudy.utils.RiskAreaUtil;

@Service
public class RiskAreaScheduleService {
    @Autowired
    private RiskAreaUtil riskAreaUtil;

    //每天凌晨4点更新riskarea
    @Scheduled(cron = "0 0 4 * * ?")
    public void updateRiskArea(){
        riskAreaUtil.getRiskArea();
    }
}
