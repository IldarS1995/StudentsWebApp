package ru.ildar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.ildar.database.entities.News;
import ru.ildar.services.NewsService;

import java.util.List;

@Controller
@RequestMapping("/news")
public class ViewNewsController
{
    public static final int newsPerPage = 5;

    @Autowired
    private NewsService newsService;

    @RequestMapping(value = "{pageNumber}", method = RequestMethod.GET)
    @ResponseBody
    public List<News> news(@PathVariable("pageNumber") Integer page)
    {
        //Path variable pageNumber is 1-based, input parameter in PageRequest is 0-based
        List<News> news = newsService.getNews(page - 1, newsPerPage);
        //Setting full description to null because it won't be needed while viewing news briefly
        news.stream().forEach((n) -> n.setFullDescription(null));
        return news;
    }

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public ModelAndView viewNews(@RequestParam("newsId") int newsId)
    {
        News news = newsService.getNews(newsId);
        return new ModelAndView("viewNews", "newsObj", news);
    }
}