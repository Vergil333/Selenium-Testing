package com.machava.selenium.managers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.machava.selenium.dtos.BoardDto;
import com.machava.selenium.dtos.ListDto;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

public class ApiTrello {

    private final static String baseUrl = "https://api.trello.com/1/";
    private final static String createObjectUrl = baseUrl + "{object}/";
    private final static String deleteObjectUrl = baseUrl + "{object}/{id}";
    private final static String boardListsUrl = baseUrl + "boards/{id}/lists";

    @Test
    public void testMethodGetBoards() throws IOException {
        Assert.assertNotNull(getAllBoards());
    }

    @Test
    public void testMethodGetLists() throws IOException {
        Assert.assertNotNull(getAllLists("5d1a2c9da06d884e54aa3ddf"));
    }

    @Test
    public void testMethodDeleteBoards() throws IOException {
        HashMap<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(200, "OK");

        deleteAllBoards("board").forEach(object -> Assert.assertEquals(object, expected));
    }

    @Test
    public void testMethodDeleteListsInBoard() throws IOException {
        HashMap<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(200, "OK");

        archiveAllLists("lists", "5d1a2c9da06d884e54aa3ddf").forEach(object -> Assert.assertEquals(object, expected));
    }

    @Test
    public void testCreateBoards() throws IOException {
        Assert.assertNotNull(createBoard("demo board"));
    }

    public static List<BoardDto> getAllBoards() throws IOException {
        Unirest.config().enableCookieManagement(false);

        HttpResponse<String> response = null;

        try {
            response = Unirest.get(baseUrl + "members/me/boards")
                    .queryString("key", "77c295ce5af4dcf6e1878306ace9d3ca")
                    .queryString("token", "1a1cfab1a058439ea474fd2de6d58cc3c37536dbdbb7e3a83bc6ce91bd799c7d")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        assert response != null;
        List<BoardDto> boards = mapper.readValue(response.getBody(), new TypeReference<List<BoardDto>>() {});

        return boards;
    }

    private static List<ListDto> getAllLists(String boardId) throws IOException {
        HttpResponse<String> response = null;

        try {
            response = Unirest.get(boardListsUrl)
                    .routeParam("id", boardId)
                    .queryString("key", "77c295ce5af4dcf6e1878306ace9d3ca")
                    .queryString("token", "1a1cfab1a058439ea474fd2de6d58cc3c37536dbdbb7e3a83bc6ce91bd799c7d")
                    .queryString("filter", "open")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        assert response != null;
        List<ListDto> lists = mapper.readValue(response.getBody(), new TypeReference<List<ListDto>>() {});

        return lists;
    }

    public static BoardDto createBoard(String name) throws IOException {
        HttpResponse<String> response = null;

        try {
            response = Unirest.post(createObjectUrl)
                    .routeParam("object", "boards")
                    .queryString("name", name)
                    .queryString("key", "77c295ce5af4dcf6e1878306ace9d3ca")
                    .queryString("token", "1a1cfab1a058439ea474fd2de6d58cc3c37536dbdbb7e3a83bc6ce91bd799c7d")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        assert response != null;
        BoardDto newBoard = mapper.readValue(response.getBody(), new TypeReference<BoardDto>() {});

        return newBoard;
    }

    public static List<HashMap<Integer, String>> deleteAllBoards(String object) throws IOException {
        Unirest.config().enableCookieManagement(false);

        List<String> boardIds = getAllBoards().stream().map(BoardDto::getId).collect(Collectors.toList());

        return boardIds.stream().map(id -> deleteObject(object, id)).collect(Collectors.toList());
    }

    public static ListDto createList(String boardId, String name) throws IOException {
        HttpResponse<String> response = null;

        try {
            response = Unirest.post(createObjectUrl)
                    .routeParam("object", "lists")
                    .queryString("name", name)
                    .queryString("idBoard", boardId)
                    .queryString("key", "77c295ce5af4dcf6e1878306ace9d3ca")
                    .queryString("token", "1a1cfab1a058439ea474fd2de6d58cc3c37536dbdbb7e3a83bc6ce91bd799c7d")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        assert response != null;
        ListDto newList = mapper.readValue(response.getBody(), new TypeReference<ListDto>() {});

        return newList;
    }

    public static List<HashMap<Integer, String>> archiveAllLists(String object, String boardId) throws IOException {
        List<String> listIds = getAllLists(boardId).stream().map(ListDto::getId).collect(Collectors.toList());

        return listIds.stream().map(id -> archiveList(object, id)).collect(Collectors.toList());
    }

    private static HashMap<Integer, String> archiveList(String object, String id) {
        int responseCode = 0;
        String responseText = null;

        try {
            HttpResponse<String> response = Unirest.put(deleteObjectUrl + "/closed")
                    .routeParam("object", object)
                    .routeParam("id", id)
                    .queryString("value", "true")
                    .queryString("key", "77c295ce5af4dcf6e1878306ace9d3ca")
                    .queryString("token", "1a1cfab1a058439ea474fd2de6d58cc3c37536dbdbb7e3a83bc6ce91bd799c7d")
                    .asString();

            responseCode = response.getStatus();
            responseText = response.getStatusText();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        HashMap<Integer, String> responseMap = new HashMap<>();
        responseMap.put(responseCode, responseText);

        return responseMap;
    }

    private static HashMap<Integer, String> deleteObject(String object, String id) {
        int responseCode = 0;
        String responseText = null;

        try {
            HttpResponse<String> response = Unirest.delete(deleteObjectUrl)
                    .routeParam("object", object)
                    .routeParam("id", id)
                    .queryString("key", "77c295ce5af4dcf6e1878306ace9d3ca")
                    .queryString("token", "1a1cfab1a058439ea474fd2de6d58cc3c37536dbdbb7e3a83bc6ce91bd799c7d")
                    .asString();

            responseCode = response.getStatus();
            responseText = response.getStatusText();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        HashMap<Integer, String> responseMap = new HashMap<>();
        responseMap.put(responseCode, responseText);

        return responseMap;
    }

}
