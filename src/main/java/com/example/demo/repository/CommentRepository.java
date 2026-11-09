package com.example.demo.repository;

import com.example.demo.model.Comment;
import com.example.demo.model.Player;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Integer> {
    List<Comment> findByPlayer(Player player);
}
