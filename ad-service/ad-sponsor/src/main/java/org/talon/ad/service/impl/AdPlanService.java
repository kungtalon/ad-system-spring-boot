package org.talon.ad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.talon.ad.constant.CommonStatus;
import org.talon.ad.constant.Consts;
import org.talon.ad.datamodel.AdPlan;
import org.talon.ad.datamodel.AdUser;
import org.talon.ad.exception.AdException;
import org.talon.ad.repository.IAdPlanRepository;
import org.talon.ad.repository.IAdUserRepository;
import org.talon.ad.service.IAdPlanService;
import org.talon.ad.utils.CommonUtils;
import org.talon.ad.vo.AdPlanGetRequest;
import org.talon.ad.vo.AdPlanRequest;
import org.talon.ad.vo.AdPlanResponse;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by Zelong
 * On 2022/5/2
 **/
@Service
public class AdPlanService implements IAdPlanService {

    private final IAdUserRepository userRepository;
    private final IAdPlanRepository planRepository;

    @Autowired
    public AdPlanService(IAdUserRepository userRepository, IAdPlanRepository planRepository) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
    }

    @Override
    @Transactional
    public AdPlanResponse createAdPlan(AdPlanRequest request) throws AdException {
        if (!request.createValidate()){
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        // make sure the corresponding user exists
        Optional<AdUser> adUser = userRepository.findById(request.getUserId());
        if (!adUser.isPresent()) {
            throw new AdException(Consts.ErrorMsg.RECORD_NOT_FOUND_ERROR);
        }

        AdPlan oldPlan = planRepository.findByUserIdAndPlanName(
                request.getUserId(), request.getPlanName());
        if (oldPlan != null) {
            throw new AdException(Consts.ErrorMsg.PLAN_NAME_EXISTS_ERROR);
        }

        AdPlan newPlan = planRepository.save(
            new AdPlan(
                request.getUserId(),
                request.getPlanName(),
                CommonUtils.parseStringDate(request.getStartDate()),
                CommonUtils.parseStringDate(request.getEndDate())
        ));

        return new AdPlanResponse(
                newPlan.getId(),
                newPlan.getPlanName()
        );
    }

    @Override
    public List<AdPlan> getAdPlanByIds(AdPlanGetRequest request) throws AdException {
        if (!request.validate()) {
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        return planRepository.findAllByIdInAndUserId(
                request.getIds(), request.getUserId()
        );
    }

    @Override
    @Transactional
    public AdPlanResponse updateAdPlan(AdPlanRequest request) throws AdException {
        if (!request.updateValidate()){
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        AdPlan plan = planRepository.findByIdAndUserId(
                request.getId(), request.getUserId()
        );

        if (plan == null) {
            throw new AdException(Consts.ErrorMsg.RECORD_NOT_FOUND_ERROR);
        }

        if (request.getPlanName() != null) {
            // make sure updated plan name does not exist
            AdPlan oldPlan = planRepository.findByUserIdAndPlanName(
                    request.getUserId(), request.getPlanName());
            if (oldPlan != null) {
                throw new AdException(Consts.ErrorMsg.PLAN_NAME_EXISTS_ERROR);
            }
            plan.setPlanName(request.getPlanName());
        }

        if (request.getStartDate() != null) {
            plan.setStartDate(CommonUtils.parseStringDate(request.getStartDate()));
        }

        if (request.getEndDate() != null) {
            plan.setEndDate(CommonUtils.parseStringDate(request.getEndDate()));
        }

        plan.setUpdateTime(new Date());
        plan = planRepository.save(plan);

        return new AdPlanResponse(
                plan.getId(), plan.getPlanName()
        );
    }

    @Override
    @Transactional
    public void deleteAdPlan(AdPlanRequest request) throws AdException {

        if (!request.deleteValidate()){
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        AdPlan plan = planRepository.findByIdAndUserId(
                request.getId(), request.getUserId()
        );
        if (plan == null) {
            throw new AdException(Consts.ErrorMsg.RECORD_NOT_FOUND_ERROR);
        }

        plan.setPlanStatus(CommonStatus.INVALID.getStatus());
        plan.setUpdateTime(new Date());
        planRepository.save(plan);
    }
}
