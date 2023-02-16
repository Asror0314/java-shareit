package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequester_Id(long requesterId);

    @Query(value = "SELECT request.* from request " +
            "WHERE request.requester_id <> ?1 " +
            "ORDER BY request.created DESC LIMIT ?3 OFFSET ?2",
            nativeQuery = true)
    List<ItemRequest> findAllForOtherUserWithPagination(long requesterId, String from, String size);

    @Query(value = "SELECT request from ItemRequest as request " +
            "WHERE request.requester.id <> ?1 ORDER BY request.created DESC")
    List<ItemRequest> findAllForOtherUser(long requesterId);

}