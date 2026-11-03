package in.findmypg.api;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

enum Role { TENANT, OWNER, ADMIN }
enum BookingStatus { REQUESTED, ACCEPTED, DECLINED, CANCELLED, COMPLETED }
enum VisitStatus { REQUESTED, CONFIRMED, CANCELLED, COMPLETED }

@Entity @Table(name="accounts")
class Account {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @Column(nullable=false, unique=true, length=254) String email;
    @Column(nullable=false) String passwordHash;
    @Column(nullable=false, length=120) String name;
    @Column(length=24) String phone;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=16) Role role = Role.TENANT;
    @Column(nullable=false) boolean enabled = true;
    @Column(nullable=false) boolean demo = false;
    @Column(nullable=false) Instant createdAt = Instant.now();
    protected Account() {}
    Account(String email, String passwordHash, String name, Role role) { this.email=email; this.passwordHash=passwordHash; this.name=name; this.role=role; }
}

@Entity @Table(name="listings")
class Listing {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="owner_id") Account owner;
    @Column(nullable=false, length=180) String title;
    @Column(nullable=false, unique=true, length=200) String slug;
    @Column(nullable=false, length=100) String city;
    @Column(nullable=false, length=120) String locality;
    @Column(length=160) String landmark;
    @Column(length=500) String address;
    @Column(length=1000) String description;
    @Column(nullable=false) int monthlyRent;
    @Column(nullable=false) int deposit;
    @Column(nullable=false) int availableBeds = 1;
    @Column(nullable=false, length=20) String genderPreference = "Any";
    @Column(nullable=false) boolean verified = false;
    @Column(nullable=false) boolean demo = false;
    @Column(nullable=false) boolean available = true;
    @Column(length=500) String coverImage;
    @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="listing_amenities", joinColumns=@JoinColumn(name="listing_id")) @Column(name="amenity") Set<String> amenities = new LinkedHashSet<>();
    @Column(nullable=false) Instant createdAt = Instant.now();
    protected Listing() {}
}

@Entity @Table(name="favorites", uniqueConstraints=@UniqueConstraint(columnNames={"tenant_id","listing_id"}))
class Favorite {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(optional=false) @JoinColumn(name="tenant_id") Account tenant;
    @ManyToOne(optional=false) @JoinColumn(name="listing_id") Listing listing;
    @Column(nullable=false) Instant createdAt=Instant.now();
    protected Favorite() {}
    Favorite(Account tenant, Listing listing) { this.tenant=tenant; this.listing=listing; }
}

@Entity @Table(name="bookings")
class Booking {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(optional=false) @JoinColumn(name="tenant_id") Account tenant;
    @ManyToOne(optional=false) @JoinColumn(name="listing_id") Listing listing;
    @Column(nullable=false) LocalDate moveInDate;
    @Column(nullable=false) int monthlyRent;
    @Column(nullable=false) int deposit;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) BookingStatus status=BookingStatus.REQUESTED;
    @Column(nullable=false) Instant createdAt=Instant.now();
    protected Booking() {}
}

@Entity @Table(name="visits")
class Visit {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(optional=false) @JoinColumn(name="tenant_id") Account tenant;
    @ManyToOne(optional=false) @JoinColumn(name="listing_id") Listing listing;
    @Column(nullable=false) Instant requestedFor;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) VisitStatus status=VisitStatus.REQUESTED;
    @Column(length=500) String note;
    @Column(nullable=false) Instant createdAt=Instant.now();
    protected Visit() {}
}

@Entity @Table(name="reviews", uniqueConstraints=@UniqueConstraint(columnNames={"tenant_id","listing_id"}))
class Review {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(optional=false) @JoinColumn(name="tenant_id") Account tenant;
    @ManyToOne(optional=false) @JoinColumn(name="listing_id") Listing listing;
    @Column(nullable=false) int rating;
    @Column(length=1200) String body;
    @Column(nullable=false) boolean demo=false;
    @Column(nullable=false) Instant createdAt=Instant.now();
    protected Review() {}
}

@Entity @Table(name="listing_reports")
class ListingReport {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(optional=false) @JoinColumn(name="reporter_id") Account reporter;
    @ManyToOne(optional=false) @JoinColumn(name="listing_id") Listing listing;
    @Column(nullable=false,length=80) String reason;
    @Column(length=1200) String details;
    @Column(nullable=false,length=20) String status="OPEN";
    @Column(nullable=false) Instant createdAt=Instant.now();
    protected ListingReport(){}
}

@Entity @Table(name="conversations",uniqueConstraints=@UniqueConstraint(columnNames={"tenant_id","listing_id"}))
class Conversation {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(optional=false) @JoinColumn(name="tenant_id") Account tenant;
    @ManyToOne(optional=false) @JoinColumn(name="owner_id") Account owner;
    @ManyToOne(optional=false) @JoinColumn(name="listing_id") Listing listing;
    @Column(nullable=false) Instant createdAt=Instant.now();
    protected Conversation(){}
}

@Entity @Table(name="messages")
class Message {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(optional=false) @JoinColumn(name="conversation_id") Conversation conversation;
    @ManyToOne(optional=false) @JoinColumn(name="sender_id") Account sender;
    @Column(nullable=false,length=2000) String body;
    @Column(nullable=false) Instant createdAt=Instant.now();
    protected Message(){}
}

@Entity @Table(name="notifications")
class Notification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(optional=false) @JoinColumn(name="recipient_id") Account recipient;
    @Column(nullable=false,length=180) String title;
    @Column(nullable=false,length=500) String body;
    @Column(length=500) String href;
    @Column(nullable=false) boolean read=false;
    @Column(nullable=false) Instant createdAt=Instant.now();
    protected Notification(){}
    Notification(Account recipient,String title,String body,String href){this.recipient=recipient;this.title=title;this.body=body;this.href=href;}
}

@Entity @Table(name="audit_events")
class AuditEvent {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(optional=false) @JoinColumn(name="actor_id") Account actor;
    @Column(nullable=false,length=80) String action;
    @Column(nullable=false,length=80) String entityType;
    @Column(nullable=false) Long entityId;
    @Column(length=500) String detail;
    @Column(nullable=false) Instant createdAt=Instant.now();
    protected AuditEvent(){}
    AuditEvent(Account actor,String action,String entityType,Long entityId,String detail){this.actor=actor;this.action=action;this.entityType=entityType;this.entityId=entityId;this.detail=detail;}
}
