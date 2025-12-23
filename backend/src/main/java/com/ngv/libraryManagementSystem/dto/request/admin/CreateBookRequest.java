package com.ngv.libraryManagementSystem.dto.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class CreateBookRequest {

    @NotBlank(message = "Tiêu đề sách không được để trống")
    @Size(max = 255, message = "Tiêu đề sách không được vượt quá 255 ký tự")
    private String title;

    @NotNull(message = "Năm xuất bản không được để trống")
    @Min(value = 0, message = "Năm xuất bản không hợp lệ")
    private Integer publicationYear;

    @NotBlank(message = "ISBN không được để trống")
    @Size(max = 50, message = "ISBN không được vượt quá 50 ký tự")
    private String isbn;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không hợp lệ")
    private Integer quantity;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;

    @NotBlank(message = "Ảnh bìa không được để trống")
    private String image;

    @NotNull(message = "Thể loại không được để trống")
    private Long categoryId;

    /**
     * Danh sách id tác giả.
     * Có thể để trống nếu bạn muốn thêm sách trước rồi gán tác giả sau.
     */
    private Set<Long> authorIds;

    /**
     * Danh sách tên tác giả.
     * Nếu tên đã tồn tại trong hệ thống thì sẽ sử dụng lại, nếu chưa có sẽ tạo mới.
     */
    private Set<String> authorNames;
}


