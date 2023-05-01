package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequester_Id(long requesterId);

    @Query(value = "SELECT r.* from shareit.request as r " +
            "WHERE r.requester_id <> ?1 " +
            "ORDER BY r.created DESC LIMIT ?3 OFFSET ?2",
            nativeQuery = true)
    List<ItemRequest> findAllForOtherUser(long requesterId, int from, int size);


}
