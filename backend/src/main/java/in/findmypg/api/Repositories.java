package in.findmypg.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.*;

interface AccountRepository extends JpaRepository<Account,Long> { Optional<Account> findByEmailIgnoreCase(String email); }
interface ListingRepository extends JpaRepository<Listing,Long>, JpaSpecificationExecutor<Listing> {
    Optional<Listing> findBySlug(String slug);
    Page<Listing> findByAvailableTrueAndCityIgnoreCaseContainingAndLocalityIgnoreCaseContainingAndMonthlyRentBetween(String city,String locality,int min,int max,Pageable page);
    Page<Listing> findByAvailableTrueAndCityIgnoreCaseContainingAndMonthlyRentBetween(String city,int min,int max,Pageable page);
    List<Listing> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
interface FavoriteRepository extends JpaRepository<Favorite,Long> { List<Favorite> findByTenantIdOrderByCreatedAtDesc(Long id); Optional<Favorite> findByTenantIdAndListingId(Long tenant,Long listing); }
interface BookingRepository extends JpaRepository<Booking,Long> { List<Booking> findByTenantIdOrderByCreatedAtDesc(Long id); List<Booking> findByListingOwnerIdOrderByCreatedAtDesc(Long id); }
interface VisitRepository extends JpaRepository<Visit,Long> { List<Visit> findByTenantIdOrderByRequestedForAsc(Long id); List<Visit> findByListingOwnerIdOrderByRequestedForAsc(Long id); }
interface ReviewRepository extends JpaRepository<Review,Long> { List<Review> findByListingIdOrderByCreatedAtDesc(Long id); }
interface ReportRepository extends JpaRepository<ListingReport,Long> { List<ListingReport> findAllByOrderByCreatedAtDesc(); }
interface ConversationRepository extends JpaRepository<Conversation,Long> { Optional<Conversation> findByTenantIdAndListingId(Long tenantId,Long listingId); List<Conversation> findByTenantIdOrOwnerIdOrderByCreatedAtDesc(Long tenantId,Long ownerId); }
interface MessageRepository extends JpaRepository<Message,Long> { List<Message> findByConversationIdOrderByCreatedAtAsc(Long id); }
interface NotificationRepository extends JpaRepository<Notification,Long> { List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long id); }
interface AuditRepository extends JpaRepository<AuditEvent,Long> { List<AuditEvent> findTop100ByOrderByCreatedAtDesc(); }
