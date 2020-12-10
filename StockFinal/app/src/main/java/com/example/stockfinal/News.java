package com.example.stockfinal;

public class News {
    private String url, urlToImage, publisher, publishedAt, title, description;

    public News( String url,String urlToImage, String publisher, String publishedAt,String title,String description)
    {
        this.url = url;
        this.urlToImage = urlToImage;
        this.publisher = publisher;
        this.publishedAt = publishedAt;
        this.title = title;
        this.description = description;
    }
    public String getUrl (){

        return url;
    }

    public  String getUrlToImage() {

        return  urlToImage;
    }

    public String getPublisher() {
        return publisher;

    }

    public String getPublishedAt () {

        return publishedAt;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
}
