package springboot.app.brewery.web.controllers.api;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springboot.app.brewery.config.annotations.order.BeerOrderCreation;
import springboot.app.brewery.config.annotations.order.BeerOrderPickup;
import springboot.app.brewery.config.annotations.order.BeerOrderRead;
import springboot.app.brewery.services.BeerOrderService;
import springboot.app.brewery.web.model.BeerOrderDto;
import springboot.app.brewery.web.model.BeerOrderPagedList;

import java.util.UUID;

@RequestMapping("/api/v1/customers/{customerId}/")
@RestController
public class BeerOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    public BeerOrderController(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    // note: in the pre authorize, we use spel aand use the bean defined in BeerOrderAuthenticationManager class.
    // we can call the method implemented, we use # for using a param passed in the method
    @BeerOrderRead
    @GetMapping("orders")
    public BeerOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize){

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return beerOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @BeerOrderCreation
    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public BeerOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody BeerOrderDto beerOrderDto){
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    @GetMapping("orders/{orderId}")
    public BeerOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId){
        return beerOrderService.getOrderById(customerId, orderId);
    }

    @BeerOrderPickup
    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId){
        beerOrderService.pickupOrder(customerId, orderId);
    }
}