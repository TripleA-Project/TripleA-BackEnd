package com.triplea.triplea.model.image;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class UploadImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originFileName;

    private String uploadFileName;

    public UploadImage (String originFileName, String uploadFileName){
        this.originFileName = originFileName;
        this.uploadFileName = uploadFileName;
    }
}
