package kitchenpos.ui.dto.tablegroup;

public class OrderTableIdRequest {

    private final Long id;

    public OrderTableIdRequest(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}