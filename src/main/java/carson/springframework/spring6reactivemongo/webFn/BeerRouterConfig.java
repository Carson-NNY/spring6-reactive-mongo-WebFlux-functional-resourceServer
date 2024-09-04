package carson.springframework.spring6reactivemongo.webFn;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class BeerRouterConfig {
    public static final String BEER_PATH = "/api/v3/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    private final BeerHandler beerHandler;

    @Bean
    public RouterFunction<ServerResponse> beerRoutes(BeerHandler beerHandler) {
//        The first line under route() defines an HTTP GET route for the path specified by BEER_PATH. When a request is made to this path and expects a JSON response, the listBeers method from the beerHandler will be invoked to handle the request. Finally, the route configuration is built and returned.
        return route().
                GET(BEER_PATH, accept(APPLICATION_JSON), beerHandler::listBeers )
                .GET(BEER_PATH_ID, accept(APPLICATION_JSON), beerHandler::getBeerById)
                .POST(BEER_PATH, accept(APPLICATION_JSON), beerHandler::createNewBeer)
                .PUT(BEER_PATH_ID, accept(APPLICATION_JSON),beerHandler::updateBeerById)
                .PATCH(BEER_PATH_ID, accept(APPLICATION_JSON),beerHandler::patchBeerById)
                .DELETE(BEER_PATH_ID, accept(APPLICATION_JSON),beerHandler::deleteBeerById)
                .build();


    }
}
