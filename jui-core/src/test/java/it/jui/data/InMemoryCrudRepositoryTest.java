package it.jui.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class InMemoryCrudRepositoryTest {

    record Customer(long id, String name) {}

    @Test
    void createUpdateDeleteUseStableIdentity() {
        List<Customer> values = new ArrayList<>(List.of(new Customer(1, "Ada")));
        InMemoryCrudRepository<Customer, Long> repository =
                new InMemoryCrudRepository<>(values, Customer::id);

        repository.create(new Customer(2, "Alan"));
        repository.update(1L, new Customer(1, "Ada Updated"));

        assertEquals("Ada Updated", repository.findById(1L).orElseThrow().name());
        assertEquals(2, repository.findAll().size());

        repository.deleteById(2L);
        assertTrue(repository.findById(2L).isEmpty());
        assertEquals(1, repository.findAll().size());
    }
}
