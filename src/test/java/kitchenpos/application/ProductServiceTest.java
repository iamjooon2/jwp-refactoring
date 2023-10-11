package kitchenpos.application;

import java.math.BigDecimal;
import java.util.List;

import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static kitchenpos.fixture.FixtureFactory.상품_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    void 상품을_저장할_수_있다() {
        // given
        final Product expected = 상품_생성("치즈피자", new BigDecimal(7_000));

        // when
        final Product actual = productService.create(expected);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo("치즈피자"),
                () -> assertThat(actual.getPrice()).isEqualByComparingTo(new BigDecimal(7_000))
        );
    }

    @Test
    void 상품_가격이_0보다_작다면_예외가_발생한다() {
        // given
        final Product expected = 상품_생성("치즈피자", new BigDecimal(-1));

        // expect
        assertThatThrownBy(() -> productService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_가격이_없으면_예외가_발생한다() {
        // given
        final Product expected = 상품_생성("치즈피자", null);

        // expect
        assertThatThrownBy(() -> productService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void 저장된_Product를_모두_가져올_수_있다() {
        // when
        final List<Product> actual = productService.list();

        // then
        assertThat(actual).hasSize(6);
    }

}
