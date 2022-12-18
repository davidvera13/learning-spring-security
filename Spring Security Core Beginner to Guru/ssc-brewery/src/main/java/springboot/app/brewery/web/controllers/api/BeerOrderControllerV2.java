package springboot.app.brewery.web.controllers.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springboot.app.brewery.config.annotations.order.BeerOrderReadV2;
import springboot.app.brewery.domain.security.UserEntity;
import springboot.app.brewery.services.BeerOrderService;
import springboot.app.brewery.web.model.BeerOrderDto;
import springboot.app.brewery.web.model.BeerOrderPagedList;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/orders/")
public class BeerOrderControllerV2 {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    @BeerOrderReadV2
    @GetMapping
    public BeerOrderPagedList listOrders(@AuthenticationPrincipal UserEntity user,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize){

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if (user.getCustomer() != null) {
            return beerOrderService.listOrders(user.getCustomer().getId(), PageRequest.of(pageNumber, pageSize));
        } else {
            return beerOrderService.listOrders(PageRequest.of(pageNumber, pageSize));
        }
    }

    @BeerOrderReadV2
    // @GetMapping("orders/{orderId}")
    @GetMapping("{orderId}")
    public BeerOrderDto getOrder(@PathVariable("orderId") UUID orderId){
        BeerOrderDto beerOrderDto = beerOrderService.getOrderById(orderId);
        if(beerOrderDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        log.debug("Found order with id " + beerOrderDto.getId());

        return beerOrderDto;
    }
}