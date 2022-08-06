package com.example.officefiles.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.docx4j.wml.UnderlineEnumeration;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
public class Segment {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long segmentId;

    @Nationalized
    private String text;

    private Boolean bold;

    private Boolean strike;

    private Boolean italic;

    private UnderlineEnumeration underlineEnumeration;

    //Paragraph (1 đoạn)
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "paragraph", foreignKey = @ForeignKey(name = "FK_SEGMENT_PARAGRAPH_RUN"))
    private Segment paragraph;

    //Run (1 chữ)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "paragraph")
    private List<Segment> runs = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "document_id")
//    @JsonIgnore
    private Document document;
}
