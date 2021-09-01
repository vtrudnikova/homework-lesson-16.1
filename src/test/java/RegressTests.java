import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RegressTests {

    private final String baseUrl = "https://reqres.in/";

    @Test
    public void checkMessageForRequestSingleUserNotFound() {
        Response response = given()
                .baseUri(baseUrl)
                .basePath("/api/users/23")
                .get();
        response
                .then()
                .body(equalTo("{}"))
                .assertThat().statusCode(404);
    }

    @Test
    public void createUser() {
        String name = "morpheus";
        String job = "cleaner";

        //отправка запроса
        Response response = given().baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(String.format("{\"name\": \"%s\",\"job\": \"%s\"}", name, job))
                .post("/api/users");

        //проверка ответа
        response.then().statusCode(201);
        response.then().body("name", equalTo(name));
        response.then().body("job", equalTo(job));

        //получение данных из ответа
        String json = response.asString();
        int idUser = JsonPath.from(json).getInt("id");

        //запросить пользователя по id
        Response responseGet = given().baseUri(baseUrl)
                .get("/api/users/{id}", idUser);
        responseGet.then().statusCode(200);
    }

    @Test
    public void updateUser() {
        String name = "morpheus";
        String job = "zion resident";
        Response response = given()
                .baseUri(baseUrl)
                .basePath("/api/users/4")
                .contentType(ContentType.JSON)
                .body(String.format("{ \"name\": \"%s\", \"job\": \"%s\"}", name, job))
                .put();
        response.then().statusCode(200)
                .body("name", equalTo(name))
                .body("job", equalTo(job));

    }

    @Test
    public void deleteUser() {
        Response response = given()
                .baseUri(baseUrl)
                .basePath("/api/user/4")
                .delete();
        response.then().statusCode(204);

    }

    @Test
    public void getSingleResourceForSupport() {
        Response response = given()
                .baseUri(baseUrl)
                .basePath("/api/unknown/2")
                .get();
        response.then().statusCode(200);
        String json = response.asString();
        String text = JsonPath.from(json).getString("support.text");
        response.then().body(text, equalTo("To keep ReqRes free, contributions towards server costs are appreciated!"));
    }
}
