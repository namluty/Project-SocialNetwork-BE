package com.meta.socialnetwork.service.friend;

import com.meta.socialnetwork.model.Friend;
import com.meta.socialnetwork.model.User;
import com.meta.socialnetwork.service.IService;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IFriendService extends IService<Friend> {
    Friend findByUser_idAndFriend_id(User user, User friend);

    List<Friend> findAllByIdAcc(User account, Boolean status1, User friend, Boolean status2);

    List<Friend> findFriendAdd(User user, Boolean status);

    List<Friend> findFriendRequest(User user, Boolean status);

    Friend suggestion(User user, User friend,Boolean status1, User user1, User friend1, Boolean status);


}
