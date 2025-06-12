package com.kamehoot.kamehoot_backend.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "userQuestions", "userQuizzes" })
public class AppUser {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 100, unique = true, nullable = false)
    private String username;

    @Column(length = 256, nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean deleted = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = { @JoinColumn(name = "user_id") })
    @Column(name = "role", length = 100, nullable = false)
    private Set<String> roles = new HashSet<>();

    @Column(name = "two_fa_enabled", nullable = false)
    private boolean twoFaEnabled = false;

    @Column(name = "two_fa_secret", length = 32)
    private String twoFaSecret;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Question> userQuestions;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Quiz> userQuizzes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserMatch> userMatches;

}
