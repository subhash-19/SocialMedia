package com.master.socialmedia.dto;

import com.master.socialmedia.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private String username;

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.createdAt = comment.getCreatedAt();
        this.username = comment.getUser().getUserName();  // Assuming you want the username of the user who commented
    }
}
