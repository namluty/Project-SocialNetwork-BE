package com.meta.socialnetwork.controller;

import com.meta.socialnetwork.dto.response.Response;
import com.meta.socialnetwork.dto.response.ResponseMessage;
import com.meta.socialnetwork.model.*;
import com.meta.socialnetwork.security.userPrinciple.UserDetailServiceImpl;
import com.meta.socialnetwork.service.IChatService;
import com.meta.socialnetwork.service.comment.ICommentService;
import com.meta.socialnetwork.service.friend.IFriendService;
import com.meta.socialnetwork.service.like.ILikeService;
import com.meta.socialnetwork.service.notification.INotificationService;
import com.meta.socialnetwork.service.post.IPostService;
import com.meta.socialnetwork.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    IPostService postService;
    @Autowired
    IUserService userService;
    @Autowired
    ILikeService likeService;
    @Autowired
    ICommentService commentService;
    @Autowired
    IFriendService friendService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserDetailServiceImpl userDetailService;
    @Autowired
    INotificationService notificationService;
    @Autowired
    IChatService chatService;

    @GetMapping("/getMess")
    public ResponseEntity<?> getMess(){
        List<Chat> list = (List<Chat>) chatService.findAll();
        Chat chat = chatService.findById(list.get(list.size()-1).getId()).get();
        return new ResponseEntity<>(chat, HttpStatus.OK);
    }

    @GetMapping("showMess/{count}")
    public ResponseEntity<List<Chat>> showChat(@PathVariable Long count){
        List<Chat> chats = (List<Chat>) chatService.findAll();
        List<Chat> list = new ArrayList<>();
        for (int i = chats.size()-1; i>= chats.size()-count; i --){
            list.add(chats.get(i));
        }
        return  new ResponseEntity<>(list, HttpStatus.OK);
    }

    // timeline
    @GetMapping("/showPost")
    public ResponseEntity<?> getListPost() {
        List<Post> postPage = (List<Post>) postService.findAllByOrderByIdDesc();
        List<Post> newPost = new ArrayList<>();
        for (int i = 0; i < postPage.size(); i++) {
            if (postPage.get(i).getStatus().equals("public") || postPage.get(i).getStatus().equals("friend")) {
                newPost.add(postPage.get(i));
                if (newPost.size() == 12) {
                    return new ResponseEntity<>(newPost, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(newPost, HttpStatus.OK);
    }

    @GetMapping("/showPostProfile")
    public ResponseEntity<Iterable<Post>> postProfile() {
        Iterable<Post> posts = postService.findPostsByUser_Id(userDetailService.getCurrentUser().getId());
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    // show post theo trạng thái
    @GetMapping("/showPostPublic")
    public ResponseEntity<?> getPostPublic(@RequestParam String status) {
        Iterable<Post> list = postService.findByStatusOrderByIdDesc(status);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    //show post theo id_user
    @GetMapping("/showPostUser/{idPost}")
    public ResponseEntity<?> showPostUser(@PathVariable Long idPost) {
        List<Post> list = postService.findPostsByStatus(idPost, "public", "friend");
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // tạo bài post
    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(@RequestBody Post post) {
        User user = userDetailService.getCurrentUser();
        post.setUser(user);
        LocalDate localDate = LocalDate.now();
        post.setCreated_date(localDate);
        postService.save(post);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    // sửa bài post
    @PutMapping("updatePost/{id}")
    public ResponseEntity<Response> editPost(@PathVariable Long id, @RequestBody Post post) {
        Optional<Post> currentPost = postService.findById(id);// ra 1 doi tuong
        if (!currentPost.isPresent()) {
            return new ResponseEntity<>(new Response("200", "Ok", post), HttpStatus.OK);
        }
        post.setId(id);
        post.setUser(userDetailService.getCurrentUser());
        post.setCommentId(currentPost.get().getCommentId());
        post.setCommentList(currentPost.get().getCommentList());
        post.setLikeList(currentPost.get().getLikeList());
        post.setCreated_date(currentPost.get().getCreated_date());
        if (post.getContent() == null || post.getContent().trim().equals("")) {
            post.setContent(currentPost.get().getContent());
        }
        if (post.getStatus() == null || post.getStatus().trim().equals("")) {
            post.setStatus(currentPost.get().getStatus());
        }
        if (post.getImageUrl() == null) {
            post.setImageUrl(currentPost.get().getImageUrl());
        }

        postService.save(post);
        return new ResponseEntity<>(new Response("200", "Ok", post), HttpStatus.OK);
    }

    //xóa bài post
    @GetMapping("/deletepost/{idPost}")
    public ResponseEntity<String> deletePost(@PathVariable Long idPost) {
        Iterable<Comment> listComment = commentService.findAllByPost_Id(idPost);
        commentService.deleteAll(listComment);
        Iterable<Like> likes = likeService.findAllLikeByPosts_Id(idPost);
        likeService.deleteAll(likes);
        postService.remove(idPost);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @GetMapping("/findPost/{idPost}")
    public ResponseEntity<?> findPostById(@PathVariable("idPost") Long idPost) {
        Optional<Post> post = postService.findById(idPost);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    // tạo like
    @GetMapping("/likeshow/{idPost}")
    public ResponseEntity<Like> createlike(@PathVariable("idPost") Long idPost) {
        User user = userDetailService.getCurrentUser();
        Like like = likeService.findByPostsIdAndUserId(idPost, user.getId());
        if (like != null) {
            likeService.remove(like.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            Post post = postService.findById(idPost).get();
            Like like1 = new Like();
            like1.setUser(user);
            like1.setPosts(post);
            likeService.saves(like1);
            return new ResponseEntity<>(like1, HttpStatus.OK);
        }
    }

    //listlike
    @GetMapping("/listlike")
    public ResponseEntity<?> getlistlike() {
        List<Like> lists = (List<Like>) likeService.findAll();
        int count = lists.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    //showlike theo post
    @GetMapping("/listlike/{id}")
    public ResponseEntity<Iterable<Like>> getlistlikePost(@PathVariable Long id) {
        Iterable<Like> lists = likeService.findAllLikeByPosts_Id(id);
        return new ResponseEntity<>(lists, HttpStatus.OK);
    }

    // create comment
    @PostMapping("/comment/{idPost}")
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment, @PathVariable("idPost") Long idPost) {
        User user = userDetailService.getCurrentUser();
        Post post = new Post();
        post.setId(idPost);
        comment.setUser(user);
        comment.setPost(post);
        LocalDate localDate = LocalDate.now();
        comment.setCreated_date(localDate);
        Comment newComment = commentService.saves(comment);
        Notification notification = new Notification();
        notification.setComment(newComment);
        notification.setNotify(notification.getComment().getUser().getFullName() + " đã comment bài viết");
        notificationService.saves(notification);
        return new ResponseEntity<>(newComment, HttpStatus.OK);
    }

    @GetMapping("/showComment/{idPost}")
    public ResponseEntity<?> listComment(@PathVariable("idPost") Long id) {
        Iterable<Comment> comments = commentService.findAllByPost_Id(id);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // sửa comment
    @PutMapping("/updatecomment/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        Comment comment1 = commentService.findById(id).get();
        comment1.setContent(comment.getContent());
        LocalDate localDate = LocalDate.now();
        comment1.setModified_date(localDate);
        return new ResponseEntity<>(commentService.saves(comment1), HttpStatus.OK);
    }

    // xóa comment
    @DeleteMapping("/deletecomment/{id}")
    public ResponseEntity<Response> deleteComment(@PathVariable("id") Long id) {
        User user = userDetailService.getCurrentUser();
        if (commentService.findById(id).get().getUser() == user) {
            commentService.remove(id);
            return new ResponseEntity<>(new Response("200", "deleted", "ok"), HttpStatus.OK);
        } else return new ResponseEntity<>(new Response("101","not comment","ok" ),HttpStatus.OK);
    }

    // tìm kiếm bạn theo tên
    @GetMapping("findFriend/{username}")
    public ResponseEntity<?> findFriend(@PathVariable String username) {
        Iterable<User> user1 = userService.findAllByUsernameIsContaining(username);
        return new ResponseEntity<>(user1, HttpStatus.OK);
    }

    // gửi yêu cầu kết bạn
    @GetMapping("/sendaddfriend/{idFriend}")
    public ResponseEntity<Friend> sendAddFriend(@PathVariable("idFriend") Long idFriend) {
        User user = userDetailService.getCurrentUser();
        User friend = userService.findById(idFriend).get();
        Friend friend1 = friendService.findByUser_idAndFriend_id(user, friend);
        if (friend1 == null) {
            Friend newFriend = new Friend();
            newFriend.setUser(user);
            newFriend.setFriend(friend);
            newFriend.setStatus(false);
            friendService.save(newFriend);
            return new ResponseEntity<>(newFriend, HttpStatus.OK);
        }
        return new ResponseEntity<>(friend1, HttpStatus.OK);
    }

    // chấp nhận kết bạn
    @GetMapping("/confirmfriend/{idFriend}")
    public ResponseEntity<User> confirmFriend(@PathVariable("idFriend") Long idFriend) {
        User user = userDetailService.getCurrentUser();
        User friend = userService.findById(idFriend).get();
        Friend friend2 = friendService.findByUser_idAndFriend_id(friend, user);
        friend2.setStatus(true);
        friendService.save(friend2);
        return new ResponseEntity<>(friend, HttpStatus.OK);
    }

    @GetMapping("/setFriend/{idFriend}")
    public ResponseEntity<String> setFriend(@PathVariable("idFriend") Long idFriend) {
        User user = userDetailService.getCurrentUser();
        User friend = userService.findById(idFriend).get();
        Friend friend2 = friendService.findByUser_idAndFriend_id(friend, user);
        if (friend2 != null) {
            return new ResponseEntity<>("Da send add friend", HttpStatus.OK);
        }
        return new ResponseEntity<>("Chua ket ban", HttpStatus.OK);
    }

    // từ chối kết bạn
    @DeleteMapping("/refuse/{idFriend}")
    public ResponseEntity<User> refuseFriend(@PathVariable("idFriend") Long idFriend) {
        User user = userDetailService.getCurrentUser();
        User friend = userService.findById(idFriend).get();
        Friend f = friendService.findByUser_idAndFriend_id(friend, user);
        friendService.remove(f.getId());
        return new ResponseEntity<>(friend, HttpStatus.OK);
    }

    // xóa bạn
    @DeleteMapping("deleteFriend/{id}")
    public ResponseEntity<ResponseMessage> removeFriend(@PathVariable("id") Long id) {
        User user = userService.findById(id).get();
        List<Friend> list = friendService.findAllByIdAcc(user, true, user, true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                friendService.remove(list.get(i).getId());
            }
            return new ResponseEntity<>(new ResponseMessage("remove"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage("null"), HttpStatus.OK);
    }

    // xem danh sách bạn
    @GetMapping("/showfriend")
    public ResponseEntity<List<User>> showListFriend() {
        User user = userDetailService.getCurrentUser();
        List<Friend> list = friendService.findAllByIdAcc(user, true, user, true);
        List<User> userList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUser().getId() == user.getId()) {
                    userList.add(list.get(i).getFriend());
                } else {
                    userList.add(list.get(i).getUser());
                }
            }
        }
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    // hien user gui kb
    @GetMapping("/showfriendadd")
    public ResponseEntity<List<User>> showAddFriend() {
        User user = userDetailService.getCurrentUser();
        List<Friend> list = friendService.findFriendAdd(user, false);
        List<User> userList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                userList.add(list.get(i).getUser());
            }
        }
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    // danh sách lời kết bạn đã gửi
    @GetMapping("/showFriendRequest")
    public ResponseEntity<List<User>> getFriendResquest() {
        User user = userDetailService.getCurrentUser();
        List<Friend> list = friendService.findFriendRequest(user, false);
        List<User> userList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                userList.add(list.get(i).getFriend());
            }
        }
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    // gỡ lời mời kết bạn đã gửi
    @DeleteMapping("/deleteRequest/{idFriend}")
    public ResponseEntity<User> deleteRequest(@PathVariable("idFriend") Long idFriend) {
        User user = userDetailService.getCurrentUser();
        User friend = userService.findById(idFriend).get();
        Friend f = friendService.findByUser_idAndFriend_id(user, friend);
        friendService.remove(f.getId());
        return new ResponseEntity<>(friend, HttpStatus.OK);
    }

    @GetMapping("/listNotify")
    public ResponseEntity<Iterable<Notification>> getNot() {
//        Iterable<Notification> notification = notificationService.findAllByComment_Post_User(userDetailService.getCurrentUser());
        Iterable<Notification> notification = notificationService.findAllByComment_Post_UserOrderByComment(userDetailService.getCurrentUser());
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @GetMapping("/listNotifyByLike")
    public ResponseEntity<Iterable<Notification>> getNotByLike() {
        Iterable<Notification> notification = notificationService.findAllByLike_Posts_UserOrderByCommentDesc(userDetailService.getCurrentUser());
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @GetMapping("/showPostNotification/{id}")
    public ResponseEntity<Post> showPostNotification(@PathVariable Long id) {
        Post post = postService.findById(id).get();
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

}
