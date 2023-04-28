package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

   @Query("Select item from Item as item " +
            "where item.available = true " +
            "and (LOWER(item.name) like %?1% " +
            "or LOWER(item.description) like %?1% )")
   List<Item> searchItemByText(String text);

}
