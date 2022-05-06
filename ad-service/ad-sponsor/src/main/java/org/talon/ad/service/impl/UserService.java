package org.talon.ad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.talon.ad.constant.Consts;
import org.talon.ad.datamodel.AdUser;
import org.talon.ad.exception.AdException;
import org.talon.ad.repository.IAdUserRepository;
import org.talon.ad.service.IUserService;
import org.talon.ad.utils.CommonUtils;
import org.talon.ad.vo.CreateUserRequest;
import org.talon.ad.vo.CreateUserResponse;

import javax.transaction.Transactional;

/**
 * Created by Zelong
 * On 2022/5/2
 **/
@Service
public class UserService implements IUserService {

    private final IAdUserRepository userRepository;

    @Autowired
    public UserService(IAdUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) throws AdException {

        if (!request.validate()) {
            throw new AdException(Consts.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        AdUser oldUser = userRepository.findByUsername(request.getUserName());
        if (oldUser != null) {
            throw new AdException(Consts.ErrorMsg.USER_NAME_EXISTS_ERROR);
        }

        AdUser newUser = userRepository.save(new AdUser(
                request.getUserName(),
                CommonUtils.md5(request.getUserName())
        ));

        return new CreateUserResponse(
                newUser.getId(),
                newUser.getUsername(),
                newUser.getToken(),
                newUser.getCreateTime(),
                newUser.getUpdateTime()
        );
    }
}
