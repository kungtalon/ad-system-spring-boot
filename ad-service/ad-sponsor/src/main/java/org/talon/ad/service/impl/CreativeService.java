package org.talon.ad.service.impl;

import org.springframework.stereotype.Service;
import org.talon.ad.constant.Consts;
import org.talon.ad.datamodel.Creative;
import org.talon.ad.exception.AdException;
import org.talon.ad.repository.ICreativeRepository;
import org.talon.ad.service.ICreativeService;
import org.talon.ad.vo.CreativeRequest;
import org.talon.ad.vo.CreativeResponse;

/**
 * Created by Zelong
 * On 2022/5/2
 **/
@Service
public class CreativeService implements ICreativeService {

    private final ICreativeRepository creativeRepository;

    public CreativeService(ICreativeRepository creativeRepository) {
        this.creativeRepository = creativeRepository;
    }

    @Override
    public CreativeResponse createCreative(CreativeRequest request) throws AdException {
        if (!request.validate()) {
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        Creative oldCreative = creativeRepository.findByNameAndUserId(
                request.getName(), request.getUserId());
        if (oldCreative != null) {
            throw new AdException(Consts.ErrorMsg.CREATIVE_NAME_EXISTS_ERROR);
        }

        Creative creative = creativeRepository.save(
                request.convertToEntity()
        );
        return new CreativeResponse(creative.getId(), creative.getName());
    }
}
