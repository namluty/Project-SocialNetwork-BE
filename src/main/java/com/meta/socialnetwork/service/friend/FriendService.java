package com.meta.socialnetwork.service.friend;

import com.meta.socialnetwork.model.Friend;
import com.meta.socialnetwork.model.User;
import com.meta.socialnetwork.repository.IFriendRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendService implements IFriendService{
    @Autowired
    IFriendRepo friendRepo;
    @Override
    public Iterable<Friend> findAll() {
        return friendRepo.findAll();
    }

    @Override
    public Optional<Friend> findById(Long id) {
        return friendRepo.findById(id);
    }

    @Override
    public void save(Friend friend) {
        friendRepo.save(friend);
    }

    @Override
    public void remove(Long id) {
        friendRepo.deleteById(id);
    }

    @Override
    public Friend findByUser_idAndFriend_id(User user, User friend) {
        return friendRepo.findByUser_idAndFriend_id(user, friend);
    }

    @Override
    public List<Friend> findAllByIdAcc(User account, Boolean status1, User friend, Boolean status2) {
        return friendRepo.findAllByIdAcc(account, status1, friend, status2);
    }

    @Override
    public List<Friend> findFriendAdd(User user, Boolean status) {
        return friendRepo.findFriendAdd(user, status);
    }

    @Override
    public List<Friend> findFriendRequest(User user, Boolean status) {
        return friendRepo.findFriendRequest(user, status);
    }

    @Override
    public Friend suggestion(User user, User friend, Boolean status1, User user1, User friend1, Boolean status) {
        return friendRepo.suggestion(user, friend,status1, user1,  friend1, status);
    }


}
