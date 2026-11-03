package in.findmypg.api;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

record RegisterRequest(@NotBlank @Size(max=120) String name, @Email @NotBlank String email, @NotBlank @Size(min=10,max=72) String password, @NotNull Role role) {}
record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
record AuthResponse(Map<String,Object> user) {}
record ListingRequest(@NotBlank @Size(max=180) String title, @NotBlank String city, @NotBlank String locality,
    String landmark, @Size(max=1000) String description, @Min(0) int monthlyRent, @Min(0) int deposit,
    @Min(1) int availableBeds, String genderPreference, String coverImage, Set<String> amenities) {}
record BookingRequest(@NotNull @Future LocalDate moveInDate, @Positive Long listingId) {}
record VisitRequest(@Positive Long listingId, @NotNull @Future Instant requestedFor, @Size(max=500) String note) {}
record ReviewRequest(@Positive Long listingId, @Min(1) @Max(5) int rating, @Size(max=1200) String body) {}
record ReportRequest(@Positive Long listingId,@NotBlank @Size(max=80) String reason,@Size(max=1200) String details) {}
record MessageRequest(@Positive Long listingId,@NotBlank @Size(max=2000) String body) {}
record ListingView(Long id, String slug, String title, String city, String locality, String landmark, String description,
    int monthlyRent, int deposit, int availableBeds, String genderPreference, boolean verified, boolean demo, String coverImage, Set<String> amenities,
    String ownerName, double averageRating, int reviewCount) {}
record BookingView(Long id, Long listingId, String listingTitle, String locality, LocalDate moveInDate, int monthlyRent, int deposit, String status, Instant createdAt) {}
record VisitView(Long id, String listingTitle, String locality, Instant requestedFor, String status, String note) {}
record ReviewView(Long id, int rating, String body, String tenantName, boolean demo, Instant createdAt) {}
record ReportView(Long id, Long listingId, String listingTitle, String reason, String details, String status, Instant createdAt, String reporterName) {}
record MessageView(Long id, Long conversationId, String body, String senderName, Instant createdAt) {}
record ConversationView(Long id, Long listingId, String listingTitle, String locality, String otherParty, String lastMessage, Instant updatedAt) {}
record NotificationView(Long id,String title,String body,String href,boolean read,Instant createdAt) {}
record AuditView(Long id,String actor,String action,String entityType,Long entityId,String detail,Instant createdAt) {}
record AccountAdminView(Long id,String name,String role,boolean enabled,Instant createdAt) {}
record PageView<T>(List<T> content, int page, int size, long totalElements, int totalPages) {}
