package org.ilrt.mca.domain.feeds;

import org.ilrt.mca.domain.BaseItem;
import org.ilrt.mca.domain.Item;

import java.util.Date;

import com.google.gson.annotations.Expose;

public class FeedItemImpl extends BaseItem implements FeedItem {

    public FeedItemImpl() {
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public int compareTo(Item item) {
        return ((FeedItem)item).getDate().compareTo(this.getDate());
    }

    @Expose private Date date = null;
    @Expose private String link = null;
}