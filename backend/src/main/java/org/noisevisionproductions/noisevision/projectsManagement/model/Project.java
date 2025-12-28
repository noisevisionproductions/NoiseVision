package org.noisevisionproductions.noisevision.projectsManagement.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "projects")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(length = 3000)
    private String description;

    @Column
    private String repositoryUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedAt;

    @ElementCollection
    @CollectionTable(
            name = "project_features",
            joinColumns = @JoinColumn(name = "project_id")
    )
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "project_technologies",
            joinColumns = @JoinColumn(name = "project_id")
    )
    @Column(name = "technology")
    private List<String> technologies = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "project_contributors",
            joinColumns = @JoinColumn(name = "project_id")
    )
    private List<Contributor> contributors = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ImageFromProject> projectImages = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        lastModifiedAt = new Date();

        if (startDate == null) {
            startDate = new Date();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedAt = new Date();
    }
}
