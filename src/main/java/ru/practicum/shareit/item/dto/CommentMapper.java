package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItemId(comment.getItemId());
        commentDto.setCreated(comment.getCreated());
        commentDto.setAuthorName(comment.getAuthor().getName());

        return commentDto;
    }

    public static List<CommentDto> toDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toDto).toList();
    }
}
