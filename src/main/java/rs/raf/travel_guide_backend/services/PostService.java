package rs.raf.travel_guide_backend.services;

import rs.raf.travel_guide_backend.entities.Post;
import rs.raf.travel_guide_backend.respositories.post.PostRepository;

import javax.inject.Inject;
import java.util.List;

public class PostService extends AbstractIsAuthorized {

    @Inject
    private PostRepository postRepository;

    public Post addPost(Integer destination_id, Post post){
        return postRepository.addPost(destination_id, post);
    }

    public void deletePost(Integer id){
        postRepository.deletePost(id);
    }

    public Post findPost(Integer id){
        return postRepository.findPost(id);
    }

    public List<Post> getAllPosts(){
        return postRepository.getAllPosts();
    }

    public List<Post> filterByDestination(Integer destination_id){
        return postRepository.filterByDestination(destination_id);
    }

    public List<Post> theMostViewedPosts(){
        return postRepository.theMostViewedPosts();
    }

    public List<Post> filterByActivity(Integer activity_id){
        return postRepository.filterByActivity(activity_id);
    }

    public Post updatePost(Integer id, Post post){
        return postRepository.updatePost(id, post);
    }

    public List<Post> favoritePosts(Integer user_id){
        return postRepository.favoritePosts(user_id);
    }

    public void addFavoritePost(Integer post_id, Integer user_id){
        postRepository.addFavoritePost(post_id, user_id);
    }

    public void removeFromFavorites(Integer post_id, Integer user_id){
        postRepository.removeFromFavorites(post_id, user_id);
    }
}
