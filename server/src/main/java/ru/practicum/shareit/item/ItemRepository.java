package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', :text, '%'))) " +
            "AND i.available = true")
    List<Item> searchByText(String text);

    List<Item> findByRequestId(Long requestId);
}