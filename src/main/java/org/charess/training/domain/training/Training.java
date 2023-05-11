package org.charess.training.domain.training;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.charess.training.domain.security.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "training",
        uniqueConstraints = @UniqueConstraint(columnNames = {"start_date", "end_date", "topic"}))
public class Training extends Audit {

    @ManyToOne
    @JoinColumn(name = "topic", nullable = false)
    private Topic topic; //training title

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "location", nullable = false) //institution where the training will be hold
    private Place location;

    @Column(name = "capacity", length = 4)
    private Integer capacity; //training room capacity: number of participants

    @ManyToMany(cascade={CascadeType.MERGE})
    @JoinTable(name="training_category", joinColumns={@JoinColumn(name="training")}, inverseJoinColumns={@JoinColumn(name="category")})
    private List<Category> categories; //group of participants

    @ManyToMany(cascade={CascadeType.MERGE})
    @JoinTable(name="training_partner", joinColumns={@JoinColumn(name="training")}, inverseJoinColumns={@JoinColumn(name="place")})
    private List<Place> partners; //Partners to take part of the training.

    @Column(name = "cyclic", length = 1)
    private String cyclic;

    @Column(name = "mode", length = 20)
    private String mode = Status.MODE_ON_SITE.toString();

    @Column(name = "test_type", length = 30)
    private String testType = Status.TEST_TYPE_PRE_AND_POST.toString();

    @JsonManagedReference
    @OneToMany(mappedBy="training")
    private List<Participant> participants;

    @ManyToOne
    @JoinColumn(name = "requester")
    private Place requester;

    @Column(name = "requested")
    private LocalDate requested;

    @ManyToOne
    @JoinColumn(name = "sponsor")
    private Place sponsor;

    @ManyToOne
    @JoinColumn(name = "facilitator")
    private Place facilitator;

    @Column(name = "status", length = 30, nullable = false)
    private String status = Status.TRAINING_VALIDATED.toString();

    @Column(name = "status_date")
    private LocalDateTime statusDatetime = LocalDateTime.now();


    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Place getLocation() {
        return location;
    }

    public void setLocation(Place location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
//        return partners.size()<1?capacity:partners.size() * capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Place> getPartners() {
        return partners;
    }

    public void setPartners(List<Place> partners) {
        this.partners = partners;
    }

    public String getCyclic() {
        return cyclic;
    }

    public void setCyclic(String cyclic) {
        this.cyclic = cyclic;
    }

    public Place getRequester() {
        return requester;
    }

    public void setRequester(Place requester) {
        this.requester = requester;
    }

    public LocalDate getRequested() {
        return requested;
    }

    public void setRequested(LocalDate requested) {
        this.requested = requested;
    }

    public Place getSponsor() {
        return sponsor;
    }

    public void setSponsor(Place sponsor) {
        this.sponsor = sponsor;
    }

    public Place getFacilitator() {
        return facilitator;
    }

    public void setFacilitator(Place facilitator) {
        this.facilitator = facilitator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStatusDatetime() {
        return statusDatetime;
    }

    public void setStatusDatetime(LocalDateTime statusDatetime) {
        this.statusDatetime = statusDatetime;
    }
}
