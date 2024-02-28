package com.triplea.triplea.model.notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notice_tb")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    public void modifyNotice(String title, String content){
        System.out.println("=========");
        System.out.println(title);
        System.out.println(content);
        System.out.println("=========");
        this.title = title;
        this.content = content;
        System.out.println(this.title);
        System.out.println(this.content);
    }
}
