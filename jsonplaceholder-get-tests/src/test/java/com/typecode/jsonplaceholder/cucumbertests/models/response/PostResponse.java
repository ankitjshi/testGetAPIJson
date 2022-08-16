package com.typecode.jsonplaceholder.cucumbertests.models.response;

import java.util.Objects;

/**
 * Response POJO
 * The API response is mapped to this Object Class with required fields
 */
public class PostResponse {

    /**
     * Initialised all the important fields from the JSON type response
     * Maps Getters and Setters Accordingly and other members are private
     */
    private int userId;
    private int id;
    private String title;
    private String body;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Methods used to compare to objects of this type
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostResponse)) return false;
        PostResponse posts = (PostResponse) o;
        return getUserId() == posts.getUserId()
                && getId() == posts.getId() && getTitle().equals(posts.getTitle())
                && getBody().equals(posts.getBody());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getId(), getTitle(), getBody());
    }

    @Override
    public String toString() {
        return "PostResponse{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

}
