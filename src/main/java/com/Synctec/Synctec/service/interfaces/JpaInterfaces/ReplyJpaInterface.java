package com.Synctec.Synctec.service.interfaces.JpaInterfaces;

import com.Synctec.Synctec.domains.Reply;

import java.util.Optional;

public interface ReplyJpaInterface {
    Reply createReply(Reply reply);
    Optional<Reply> findById(String replyId);

}
