package kitchenpos.application;

import java.util.List;
import java.util.stream.Collectors;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.repository.MenuRepository;
import kitchenpos.domain.repository.OrderLineItemRepository;
import kitchenpos.domain.repository.OrderRepository;
import kitchenpos.domain.repository.OrderTableRepository;
import kitchenpos.ui.dto.order.CreateOrderRequest;
import kitchenpos.ui.dto.order.OrderLineItemDto;
import kitchenpos.ui.dto.order.UpdateOrderRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final OrderTableRepository orderTableRepository;

    public OrderService(
            final MenuRepository menuRepository,
            final OrderRepository orderRepository,
            final OrderLineItemRepository orderLineItemRepository,
            final OrderTableRepository orderTableRepository
    ) {
        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.orderTableRepository = orderTableRepository;
    }

    @Transactional
    public Order create(final CreateOrderRequest createOrderRequest) {
        validateMenuIds(createOrderRequest);

        final OrderTable orderTable = orderTableRepository.findById(createOrderRequest.getOrderTableId())
                .orElseThrow(IllegalArgumentException::new);

        final Order order = orderRepository.save(Order.createBy(orderTable));
        saveOrderLineItems(createOrderRequest, order.getId());
        return order;
    }

    private void saveOrderLineItems(final CreateOrderRequest createOrderRequest, final Long orderId) {
        for (final OrderLineItemDto orderLineItemDto : createOrderRequest.getOrderLineItems()) {
            final OrderLineItem orderLineItem = new OrderLineItem(orderId, orderLineItemDto.getMenuId(), orderLineItemDto.getQuantity());
            orderLineItemRepository.save(orderLineItem);
        }
    }

    private void validateMenuIds(final CreateOrderRequest createOrderRequest) {
        final List<Long> menuIds = createOrderRequest.getOrderLineItems().stream()
                .map(OrderLineItemDto::getMenuId)
                .collect(Collectors.toList());
        if (menuIds.size() != menuRepository.countByIdIn(menuIds)) {
            throw new IllegalArgumentException();
        }
    }

    public List<Order> list() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order changeOrderStatus(final Long orderId, final UpdateOrderRequest updateOrderRequest) {
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(IllegalArgumentException::new);

        order.updateStatus(updateOrderRequest.getOrderStatus());
        return orderRepository.save(order);
    }
}
