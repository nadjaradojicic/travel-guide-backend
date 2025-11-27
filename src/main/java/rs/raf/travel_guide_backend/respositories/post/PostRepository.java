package rs.raf.travel_guide_backend.respositories.post;

import rs.raf.travel_guide_backend.entities.Post;

import java.util.List;

public interface PostRepository {

    List<Post> filterByDestination(Integer destination_id);

    public Post addPost(Integer destination_id, Post post);
    public List<Post> getAllPosts();
    public Post findPost(Integer id);
    public void deletePost(Integer id);

    public void incrementViews(Integer clanakId);

    public List<Post> theMostViewedPosts();

    public List<Post> filterByActivity(Integer activity_id);

    public Post updatePost(Integer id, Post updatedPost);

    public List<Post> favoritePosts(Integer user_id);

    public void addFavoritePost(Integer post_id, Integer user_id);

    public void removeFromFavorites(Integer post_id, Integer user_id);
}
