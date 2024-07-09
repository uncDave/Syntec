package com.Synctec.Synctec.service.impl.jpaImpl;

import com.Synctec.Synctec.domains.Reply;
import com.Synctec.Synctec.repository.ReplyRepository;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.ReplyJpaInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReplyJpaImpl implements ReplyJpaInterface {
    private final ReplyRepository repository;
    @Override
    public Reply createReply(Reply reply) {
        return repository.save(reply);

    }

    @Override
    public Optional<Reply> findById(String replyId) {
       return repository.findById(replyId);
    }
}
