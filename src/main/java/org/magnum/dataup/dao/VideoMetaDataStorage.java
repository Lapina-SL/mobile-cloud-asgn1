package org.magnum.dataup.dao;

import org.magnum.dataup.model.Video;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class VideoMetaDataStorage {
    private static final AtomicLong currentId = new AtomicLong(0L);

    private Map<Long, Video> videos = new ConcurrentHashMap<>();

    public Collection<Video> getVideoList() {
        return videos.values();
    }

    public Video getVideoById(long id) {
        return videos.get(id);
    }

    public boolean invalidId(long id) {
        return !validId(id);
    }

    public boolean validId(long id) {
        return videos.containsKey(id);
    }


    public Video addVideo(Video entity) {
        Video v = setIdAndUrl(entity);
        videos.put(v.getId(), v);
        return v;
    }

    private Video setIdAndUrl(Video entity) {
        Video v = setId(entity);
        v.setDataUrl(getDataUrl(v.getId()));
        return v;
    }

    private Video setId(Video entity) {
        Video v = entity;
        v.setId(currentId.incrementAndGet());
        return v;
    }

    private String getDataUrl(long videoId) {
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }

    private String getUrlBaseForLocalServer() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String base =
                "http://" + request.getServerName()
                        + ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
        return base;
    }
}