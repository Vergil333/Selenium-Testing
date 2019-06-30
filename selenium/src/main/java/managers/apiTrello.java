package managers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dtos.BoardDto;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

public class apiTrello {

    private String baseUrl = "https://api.trello.com/1/";
    private String deleteObjectUrl = baseUrl + "{object}/{id}";
    private String createBoardUrl = baseUrl + "{object}/";

    @Test
    public void testMethodGetBoards() throws IOException {
        Assert.assertNotNull(getAllBoards());
    }

    @Test
    public void testMethodDeleteBoards() throws IOException {
        HashMap<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(200, "OK");

        deleteAllObjects("board").forEach(object -> Assert.assertEquals(object, expected));
    }

    @Test
    public void testCreateBoards() throws IOException {
        Assert.assertNotNull(createBoard("boards", "demo board"));
    }

    public List<BoardDto> getAllBoards() throws IOException {
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

        //System.out.println("response: " + response);
        System.out.println("response.getBody(): " + response.getBody());
        boards.forEach((board) -> System.out.println("Board Name: " + board.getName()));

        return boards;
    }

    private BoardDto createBoard(String object, String name) throws IOException {
        HttpResponse<String> response = null;

        try {
            response = Unirest.post(createBoardUrl)
                    .routeParam("object", object)
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

        System.out.println(newBoard);
        return newBoard;
    }

    public List<HashMap<Integer, String>> deleteAllObjects(String object) throws IOException {
        Unirest.config().enableCookieManagement(false);

        List<String> boardIds = getAllBoards().stream().map(BoardDto::getId).collect(Collectors.toList());

        return boardIds.stream().map(id -> deleteObject(object, id)).collect(Collectors.toList());
    }

    private HashMap<Integer, String> deleteObject(String object, String id) {
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
