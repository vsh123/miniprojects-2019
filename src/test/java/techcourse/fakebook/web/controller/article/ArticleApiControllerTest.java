package techcourse.fakebook.web.controller.article;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import techcourse.fakebook.service.article.dto.ArticleRequest;
import techcourse.fakebook.service.article.dto.ArticleResponse;
import techcourse.fakebook.service.user.dto.LoginRequest;
import techcourse.fakebook.web.controller.ControllerTestHelper;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class ArticleApiControllerTest extends ControllerTestHelper {
    @LocalServerPort
    private int port;

    private LoginRequest loginRequest = new LoginRequest("van@van.com", "Password!1");
    private String sessionId;

    @BeforeEach
    void setUp() {
        sessionId = getSessionId(login(loginRequest));
    }

    @Test
    void 글_목록을_잘_불러오는지_확인한다() {
        writeArticle();

        given().
                port(port).
                sessionId(sessionId).
        when().
                get("/api/articles").
        then().
                statusCode(HttpStatus.OK.value()).
                body("size", greaterThanOrEqualTo(2));
    }

    @Test
    void 글을_잘_작성하는지_확인한다() {
        ArticleRequest articleRequest = new ArticleRequest("hello");

        given().
                port(port).
                sessionId(sessionId).
                formParam("content", "hello").
        when().
                post("/api/articles").
        then().
                statusCode(HttpStatus.CREATED.value()).
                body("content", equalTo(articleRequest.getContent()));
    }

    @Test
    void 글을_잘_삭제하는지_확인한다() {
        given().
                port(port).
                sessionId(sessionId).
        when().
                delete("/api/articles/2").
        then().
                statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 글을_잘_수정하는지_확인한다() {
        ArticleRequest articleRequest = new ArticleRequest("수정된 글입니다.");

        given().
                port(port).
                contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).
                sessionId(sessionId).
                body(articleRequest).
        when().
                put("/api/articles/1").
        then().
                statusCode(HttpStatus.OK.value()).
                body("content", equalTo(articleRequest.getContent()));
    }

    @Test
    void 좋아요_여부를_확인한다() {
        ArticleResponse article = writeArticle();

        given().
                port(port).
                sessionId(sessionId).
        when().
                get("api/articles/" + article.getId() + "/like").
        then().
                statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 좋아요가_잘_등록되는지_확인한다() {
        ArticleResponse article = writeArticle();

        given().
                port(port).
                sessionId(sessionId).
        when().
                post("/api/articles/" + article.getId() + "/like").
        then().
                statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void 좋아요가_잘_삭제되는지_확인한다() {
        ArticleResponse article = writeArticle();

        given().
                port(port).
                sessionId(sessionId).
        when().
                post("/api/articles/" + article.getId() + "/like").
        then().
                statusCode(HttpStatus.CREATED.value());

        given().
                port(port).
                sessionId(sessionId).
        when().
                post("/api/articles/" + article.getId() + "/like").
        then().
                statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 좋아요_개수를_잘_불러오는지_확인한다() {
        ArticleResponse articleResponse = writeArticle();

        //좋아요를 누른다.
        given().
                port(port).
                contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).
                sessionId(sessionId).
        when().
                post("/api/articles/" + articleResponse.getId() + "/like").
        then().
                statusCode(HttpStatus.CREATED.value());

        given().
                port(port).
                sessionId(sessionId).
        when().
                get("/api/articles/" + articleResponse.getId() + "/like/count").
        then().
                statusCode(HttpStatus.OK.value()).
                body(equalTo("1"));
    }

    @Test
    void 게시글_이미지_포함_업로드가_잘_되는지_확인한다() {
        given().
                port(port).
                sessionId(sessionId).
                multiPart("files", new File("src/test/resources/static/images/logo/res9-logo.gif")).
                formParam("content", "hello").
        when().
                post("/api/articles").
        then().
                body("attachments.size", equalTo(1));
    }
}