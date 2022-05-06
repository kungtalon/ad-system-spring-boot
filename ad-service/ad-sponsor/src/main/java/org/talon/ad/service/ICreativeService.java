package org.talon.ad.service;

import org.talon.ad.exception.AdException;
import org.talon.ad.vo.CreativeRequest;
import org.talon.ad.vo.CreativeResponse;

public interface ICreativeService {
    CreativeResponse createCreative(CreativeRequest request) throws AdException;
}
