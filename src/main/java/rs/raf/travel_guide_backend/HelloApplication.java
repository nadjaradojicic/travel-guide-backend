package rs.raf.travel_guide_backend;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import rs.raf.travel_guide_backend.respositories.activity.ActivityRepository;
import rs.raf.travel_guide_backend.respositories.activity.MySQLActivityRepository;
import rs.raf.travel_guide_backend.respositories.comment.CommentRepository;
import rs.raf.travel_guide_backend.respositories.comment.MySQLCommentRepository;
import rs.raf.travel_guide_backend.respositories.destination.DestinationRepository;
import rs.raf.travel_guide_backend.respositories.destination.MySQLDestinationRepository;
import rs.raf.travel_guide_backend.respositories.post.MySQLPostRepository;
import rs.raf.travel_guide_backend.respositories.post.PostRepository;
import rs.raf.travel_guide_backend.respositories.user.MySQLUserRepository;
import rs.raf.travel_guide_backend.respositories.user.UserRepository;
import rs.raf.travel_guide_backend.services.*;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class HelloApplication extends ResourceConfig {

    public HelloApplication() {

        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

        AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {

                this.bind(MySQLUserRepository.class).to(UserRepository.class).in(Singleton.class);
                this.bind(MySQLActivityRepository.class).to(ActivityRepository.class).in(Singleton.class);
                this.bind(MySQLDestinationRepository.class).to(DestinationRepository.class).in(Singleton.class);
                this.bind(MySQLPostRepository.class).to(PostRepository.class).in(Singleton.class);
                this.bind(MySQLCommentRepository.class).to(CommentRepository.class).in(Singleton.class);



                this.bindAsContract(UserService.class);
                this.bindAsContract(ActivityService.class);
                this.bindAsContract(DestinationService.class);
                this.bindAsContract(PostService.class);
                this.bindAsContract(CommentService.class);

            }
        };
        register(binder);

        packages("rs.raf.travel_guide_backend");
    }

}