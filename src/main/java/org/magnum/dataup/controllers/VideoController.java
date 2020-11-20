package org.magnum.dataup.controllers;

import org.magnum.dataup.dao.VideoMetaDataStorage;
import org.magnum.dataup.dao.VideoFileManager;
import org.magnum.dataup.VideoSvcApi;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

@Controller
public class VideoController {

    @Autowired
    private VideoMetaDataStorage storage;
    @Autowired
    private VideoFileManager videoDataMgr;


    @GetMapping(VideoSvcApi.VIDEO_SVC_PATH)
    public @ResponseBody
    Collection<Video> getAddedVideos() {
        Collection<Video> videoList = storage.getVideoList();
        return videoList;
    }

    @PostMapping(VideoSvcApi.VIDEO_SVC_PATH)
    public @ResponseBody
    Video addVideo(@RequestBody Video v) {
        return storage.addVideo(v);
    }

    @PostMapping(VideoSvcApi.VIDEO_DATA_PATH)
    public @ResponseBody
    VideoStatus addVideoDataId(@PathVariable("id") long id,
                               @RequestParam("data") MultipartFile data,
                               HttpServletResponse response) throws IOException {
        Video v = storage.getVideoById(id);
        if (v == null) {
            response.sendError(404);
        } else {
            InputStream is = data.getInputStream();
            videoDataMgr.saveVideoData(v, is);
        }
        return new VideoStatus(VideoStatus.VideoState.READY);
    }

    @GetMapping(VideoSvcApi.VIDEO_DATA_PATH)
    public void getVideoDataById(@PathVariable("id") long id,
                       HttpServletResponse response) throws IOException {
        Video v = storage.getVideoById(id);
        if ((v == null) || (!videoDataMgr.hasVideoData(v)))
            response.sendError(404);
        else {
            OutputStream out = response.getOutputStream();
            videoDataMgr.copyVideoData(v, out);
        }
        //return v;
    }


}