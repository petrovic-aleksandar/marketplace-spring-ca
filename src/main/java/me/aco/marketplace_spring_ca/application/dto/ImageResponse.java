package me.aco.marketplace_spring_ca.application.dto;

import me.aco.marketplace_spring_ca.domain.entities.Image;

public class ImageResponse {

    private long id;
	private String path;
	private boolean front;

    public ImageResponse(Image image) {
        this.id = image.getId();
        this.path = image.getPath();
        this.front = image.isFront();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFront() {
        return front;
    }

    public void setFront(boolean front) {
        this.front = front;
    }
}
