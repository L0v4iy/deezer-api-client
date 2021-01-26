/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.L0v4iy.deezer.domain;

import java.util.Date;
public class Track {

    private Long id;
    private Boolean readable;
    private String title;
    private String title_short;
    private String title_version;
    private String isrc;
    private String link;
    private String share;
    private Integer duration;
    private Integer track_position;
    private Integer disk_number;
    private Integer rank;
    private String release_date;
    private boolean explicit_lyrics;
    private Integer explicit_content_lyrics;
    private Integer explicit_content_cover;
    private String preview;
    private Double bpm;
    private Double gain;
    private String[] available_countries;
    private Contributor[] contributors;
    private String md5_image;
    private Artist artist;
    private Album album;
    private String type;
    private boolean unseen;
    private Date time_add;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getReadable() {
        return readable;
    }

    public void setReadable(Boolean readable) {
        this.readable = readable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_short() {
        return title_short;
    }

    public void setTitle_short(String title_short) {
        this.title_short = title_short;
    }

    public String getTitle_version() {
        return title_version;
    }

    public void setTitle_version(String title_version) {
        this.title_version = title_version;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public boolean isExplicit_lyrics() {
        return explicit_lyrics;
    }

    public void setExplicit_lyrics(boolean explicit_lyrics) {
        this.explicit_lyrics = explicit_lyrics;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setUnseen(boolean unseen) {
        this.unseen = unseen;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public void setType(String type) {
        this.type = type;

    }

    public Integer getTrack_position() {
        return track_position;
    }

    public void setTrack_position(Integer track_position) {
        this.track_position = track_position;
    }

    public Integer getDisk_number() {
        return disk_number;
    }

    public void setDisk_number(Integer disk_number) {
        this.disk_number = disk_number;
    }

    public Double getBpm() {
        return bpm;
    }

    public void setBpm(Double bpm) {
        this.bpm = bpm;
    }

    public Double getGain() {
        return gain;
    }

    public void setGain(Double gain) {
        this.gain = gain;
    }

    public String[] getAvailable_countries() {
        return available_countries;
    }

    public void setAvailable_countries(String[] available_countries) {
        this.available_countries = available_countries;
    }

    public Date getTime_add() {
        return time_add;
    }

    public void setTime_add(Date time_add) {
        this.time_add = time_add;
    }

    public Integer getExplicit_content_lyrics() {
        return explicit_content_lyrics;
    }

    public void setExplicit_content_lyrics(Integer explicit_content_lyrics) {
        this.explicit_content_lyrics = explicit_content_lyrics;
    }

    public Integer getExplicit_content_cover() {
        return explicit_content_cover;
    }

    public void setExplicit_content_cover(Integer explicit_content_cover) {
        this.explicit_content_cover = explicit_content_cover;
    }

    public Contributor[] getContributors() {
        return contributors;
    }

    public void setContributors(Contributor[] contributors) {
        this.contributors = contributors;
    }

    public String getMd5_image() {
        return md5_image;
    }

    public void setMd5_image(String md5_image) {
        this.md5_image = md5_image;
    }
}
