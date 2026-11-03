CREATE TYPE "Role" AS ENUM ('TENANT', 'OWNER', 'ADMIN');
CREATE TYPE "ListingStatus" AS ENUM ('DRAFT', 'PENDING_REVIEW', 'PUBLISHED', 'PAUSED', 'REJECTED');
CREATE TYPE "VerificationStatus" AS ENUM ('UNVERIFIED', 'IN_REVIEW', 'VERIFIED', 'REJECTED');
CREATE TYPE "BookingStatus" AS ENUM ('REQUESTED', 'ACCEPTED', 'DECLINED', 'CANCELLED', 'COMPLETED');
CREATE TYPE "VisitStatus" AS ENUM ('REQUESTED', 'CONFIRMED', 'COMPLETED', 'CANCELLED');
CREATE TYPE "ReportStatus" AS ENUM ('OPEN', 'REVIEWING', 'RESOLVED', 'DISMISSED');

CREATE TABLE "User" (
  "id" TEXT NOT NULL, "name" TEXT NOT NULL, "email" TEXT NOT NULL, "phone" TEXT,
  "passwordHash" TEXT NOT NULL, "role" "Role" NOT NULL DEFAULT 'TENANT', "image" TEXT,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP, "updatedAt" TIMESTAMP(3) NOT NULL,
  CONSTRAINT "User_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "OwnerProfile" (
  "id" TEXT NOT NULL, "userId" TEXT NOT NULL, "bio" TEXT, "verifiedAt" TIMESTAMP(3),
  CONSTRAINT "OwnerProfile_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "Listing" (
  "id" TEXT NOT NULL, "ownerId" TEXT NOT NULL, "title" TEXT NOT NULL, "slug" TEXT NOT NULL,
  "description" TEXT NOT NULL, "city" TEXT NOT NULL, "locality" TEXT NOT NULL, "landmark" TEXT,
  "address" TEXT NOT NULL, "latitude" DOUBLE PRECISION, "longitude" DOUBLE PRECISION,
  "rentFrom" INTEGER NOT NULL, "rentTo" INTEGER, "deposit" INTEGER NOT NULL, "genderPreference" TEXT NOT NULL,
  "curfew" TEXT, "foodDetails" TEXT, "cancellationPolicy" TEXT,
  "verifiedStatus" "VerificationStatus" NOT NULL DEFAULT 'UNVERIFIED',
  "status" "ListingStatus" NOT NULL DEFAULT 'DRAFT', "isDemo" BOOLEAN NOT NULL DEFAULT false,
  "availableFrom" TIMESTAMP(3), "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL, CONSTRAINT "Listing_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "ListingPhoto" (
  "id" TEXT NOT NULL, "listingId" TEXT NOT NULL, "url" TEXT NOT NULL, "alt" TEXT NOT NULL,
  "sortOrder" INTEGER NOT NULL DEFAULT 0, CONSTRAINT "ListingPhoto_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "RoomType" (
  "id" TEXT NOT NULL, "listingId" TEXT NOT NULL, "name" TEXT NOT NULL, "occupancy" INTEGER NOT NULL,
  "monthlyRent" INTEGER NOT NULL, "availableBeds" INTEGER NOT NULL DEFAULT 1, "totalBeds" INTEGER NOT NULL DEFAULT 1,
  "hasAc" BOOLEAN NOT NULL DEFAULT false, "attachedBath" BOOLEAN NOT NULL DEFAULT false,
  CONSTRAINT "RoomType_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "ListingAmenity" (
  "id" TEXT NOT NULL, "listingId" TEXT NOT NULL, "name" TEXT NOT NULL, "included" BOOLEAN NOT NULL DEFAULT true,
  CONSTRAINT "ListingAmenity_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "Favorite" (
  "userId" TEXT NOT NULL, "listingId" TEXT NOT NULL, "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT "Favorite_pkey" PRIMARY KEY ("userId", "listingId")
);
CREATE TABLE "SavedSearch" (
  "id" TEXT NOT NULL, "userId" TEXT NOT NULL, "name" TEXT NOT NULL, "filters" JSONB NOT NULL,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP, CONSTRAINT "SavedSearch_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "Booking" (
  "id" TEXT NOT NULL, "listingId" TEXT NOT NULL, "roomTypeId" TEXT NOT NULL, "tenantId" TEXT NOT NULL,
  "ownerId" TEXT NOT NULL, "moveIn" TIMESTAMP(3) NOT NULL, "moveOut" TIMESTAMP(3), "monthlyRent" INTEGER NOT NULL,
  "deposit" INTEGER NOT NULL, "platformFee" INTEGER NOT NULL DEFAULT 0,
  "status" "BookingStatus" NOT NULL DEFAULT 'REQUESTED', "paymentStatus" TEXT NOT NULL DEFAULT 'NOT_CONFIGURED',
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP, CONSTRAINT "Booking_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "Visit" (
  "id" TEXT NOT NULL, "listingId" TEXT NOT NULL, "tenantId" TEXT NOT NULL, "scheduledAt" TIMESTAMP(3) NOT NULL,
  "status" "VisitStatus" NOT NULL DEFAULT 'REQUESTED', "message" TEXT,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP, CONSTRAINT "Visit_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "Review" (
  "id" TEXT NOT NULL, "listingId" TEXT NOT NULL, "userId" TEXT NOT NULL, "rating" INTEGER NOT NULL,
  "text" TEXT NOT NULL, "isDemo" BOOLEAN NOT NULL DEFAULT false,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP, CONSTRAINT "Review_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "Conversation" (
  "id" TEXT NOT NULL, "listingId" TEXT NOT NULL, "tenantId" TEXT NOT NULL, "ownerId" TEXT NOT NULL,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP, CONSTRAINT "Conversation_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "Message" (
  "id" TEXT NOT NULL, "conversationId" TEXT NOT NULL, "senderId" TEXT NOT NULL, "body" TEXT NOT NULL,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP, CONSTRAINT "Message_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "Notification" (
  "id" TEXT NOT NULL, "userId" TEXT NOT NULL, "title" TEXT NOT NULL, "body" TEXT NOT NULL, "readAt" TIMESTAMP(3),
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP, CONSTRAINT "Notification_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "Report" (
  "id" TEXT NOT NULL, "listingId" TEXT NOT NULL, "reporterId" TEXT NOT NULL, "reason" TEXT NOT NULL,
  "details" TEXT, "status" "ReportStatus" NOT NULL DEFAULT 'OPEN',
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP, CONSTRAINT "Report_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "VerificationCase" (
  "id" TEXT NOT NULL, "listingId" TEXT NOT NULL, "status" "VerificationStatus" NOT NULL DEFAULT 'IN_REVIEW',
  "notes" TEXT, "reviewedBy" TEXT, "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP(3) NOT NULL, CONSTRAINT "VerificationCase_pkey" PRIMARY KEY ("id")
);
CREATE TABLE "AuditEvent" (
  "id" TEXT NOT NULL, "actorId" TEXT, "action" TEXT NOT NULL, "entityType" TEXT NOT NULL, "entityId" TEXT NOT NULL,
  "metadata" JSONB, "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT "AuditEvent_pkey" PRIMARY KEY ("id")
);

CREATE UNIQUE INDEX "User_email_key" ON "User"("email");
CREATE UNIQUE INDEX "User_phone_key" ON "User"("phone");
CREATE UNIQUE INDEX "OwnerProfile_userId_key" ON "OwnerProfile"("userId");
CREATE UNIQUE INDEX "Listing_slug_key" ON "Listing"("slug");
CREATE INDEX "Listing_city_locality_status_idx" ON "Listing"("city", "locality", "status");
CREATE UNIQUE INDEX "ListingAmenity_listingId_name_key" ON "ListingAmenity"("listingId", "name");
CREATE INDEX "Booking_tenantId_status_idx" ON "Booking"("tenantId", "status");
CREATE INDEX "Booking_ownerId_status_idx" ON "Booking"("ownerId", "status");
CREATE UNIQUE INDEX "Review_listingId_userId_key" ON "Review"("listingId", "userId");
CREATE UNIQUE INDEX "Conversation_listingId_tenantId_ownerId_key" ON "Conversation"("listingId", "tenantId", "ownerId");

ALTER TABLE "OwnerProfile" ADD CONSTRAINT "OwnerProfile_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Listing" ADD CONSTRAINT "Listing_ownerId_fkey" FOREIGN KEY ("ownerId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "ListingPhoto" ADD CONSTRAINT "ListingPhoto_listingId_fkey" FOREIGN KEY ("listingId") REFERENCES "Listing"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "RoomType" ADD CONSTRAINT "RoomType_listingId_fkey" FOREIGN KEY ("listingId") REFERENCES "Listing"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ListingAmenity" ADD CONSTRAINT "ListingAmenity_listingId_fkey" FOREIGN KEY ("listingId") REFERENCES "Listing"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Favorite" ADD CONSTRAINT "Favorite_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Favorite" ADD CONSTRAINT "Favorite_listingId_fkey" FOREIGN KEY ("listingId") REFERENCES "Listing"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "SavedSearch" ADD CONSTRAINT "SavedSearch_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Booking" ADD CONSTRAINT "Booking_listingId_fkey" FOREIGN KEY ("listingId") REFERENCES "Listing"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Booking" ADD CONSTRAINT "Booking_roomTypeId_fkey" FOREIGN KEY ("roomTypeId") REFERENCES "RoomType"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Booking" ADD CONSTRAINT "Booking_tenantId_fkey" FOREIGN KEY ("tenantId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Booking" ADD CONSTRAINT "Booking_ownerId_fkey" FOREIGN KEY ("ownerId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Visit" ADD CONSTRAINT "Visit_listingId_fkey" FOREIGN KEY ("listingId") REFERENCES "Listing"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Visit" ADD CONSTRAINT "Visit_tenantId_fkey" FOREIGN KEY ("tenantId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Review" ADD CONSTRAINT "Review_listingId_fkey" FOREIGN KEY ("listingId") REFERENCES "Listing"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Review" ADD CONSTRAINT "Review_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Message" ADD CONSTRAINT "Message_conversationId_fkey" FOREIGN KEY ("conversationId") REFERENCES "Conversation"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Message" ADD CONSTRAINT "Message_senderId_fkey" FOREIGN KEY ("senderId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Conversation" ADD CONSTRAINT "Conversation_listingId_fkey" FOREIGN KEY ("listingId") REFERENCES "Listing"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Conversation" ADD CONSTRAINT "Conversation_tenantId_fkey" FOREIGN KEY ("tenantId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Conversation" ADD CONSTRAINT "Conversation_ownerId_fkey" FOREIGN KEY ("ownerId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Notification" ADD CONSTRAINT "Notification_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "Report" ADD CONSTRAINT "Report_listingId_fkey" FOREIGN KEY ("listingId") REFERENCES "Listing"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Report" ADD CONSTRAINT "Report_reporterId_fkey" FOREIGN KEY ("reporterId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "AuditEvent" ADD CONSTRAINT "AuditEvent_actorId_fkey" FOREIGN KEY ("actorId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "VerificationCase" ADD CONSTRAINT "VerificationCase_listingId_fkey" FOREIGN KEY ("listingId") REFERENCES "Listing"("id") ON DELETE CASCADE ON UPDATE CASCADE;
