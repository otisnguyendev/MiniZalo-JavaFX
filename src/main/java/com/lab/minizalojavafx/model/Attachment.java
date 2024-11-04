package com.lab.minizalojavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    private int id;
    private int messageId;
    private String filePath;
    private String fileType;
}
