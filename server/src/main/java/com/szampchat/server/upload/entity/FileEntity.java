package com.szampchat.server.upload.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "files")
public class FileEntity {
    @Column("file_id")
    private UUID id; //Using uuid for backwards compatibility
    @Column("path")
    private String path;
}
