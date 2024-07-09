package com.Synctec.Synctec.service.impl.jpaImpl;

import com.Synctec.Synctec.domains.WaitList;
import com.Synctec.Synctec.repository.WaitListRepository;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.WaitListJpaInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitListJpaImpl implements WaitListJpaInterface {

    private final WaitListRepository waitListRepository;


    @Override
    public WaitList createWaitList(WaitList waitList) {
        return waitListRepository.save(waitList);
    }
}
