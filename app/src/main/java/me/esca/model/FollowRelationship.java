package me.esca.model;

/**
 * Created by Me on 12/08/2017.
 */

public class FollowRelationship {

    private Long id;

    private Cook follower;

    private Cook followee;

    public FollowRelationship() {
        //JPA
    }

    public FollowRelationship(Long id, Cook follower, Cook followee) {
        this.id = id;
        this.follower = follower;
        this.followee = followee;
    }

    public Long getId() {
        return id;
    }

    public Cook getFollower() {

        return follower;
    }

    public void setFollower(Cook follower) {
        this.follower = follower;
    }

    public Cook getFollowee() {
        return followee;
    }

    public void setFollowee(Cook followee) {
        this.followee = followee;
    }
}
