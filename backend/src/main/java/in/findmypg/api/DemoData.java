package in.findmypg.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;

@Configuration
class DemoData {
    @Bean @ConditionalOnProperty(name="app.demo-seed",havingValue="true")
    CommandLineRunner demoListings(AccountRepository accounts,ListingRepository listings,PasswordEncoder encoder){return args->{
        if(listings.count()>0)return;
        Account owner=accounts.findByEmailIgnoreCase("demo-owner@example.invalid").orElseGet(()->{Account a=new Account("demo-owner@example.invalid",encoder.encode(UUID.randomUUID().toString()),"FindMyPG Demo Host",Role.OWNER);a.demo=true;return accounts.save(a);});
        List<Object[]> samples=List.of(
            new Object[]{"A sunlit shared home near the park","Bengaluru","Indiranagar","Indiranagar Metro · 8 min walk",18500,37000,"Women","photo-1600210492486-724fe5c67fb0",new String[]{"Wi-Fi","Meals","Housekeeping","Power backup"}},
            new Object[]{"Calm corners in the heart of Koramangala","Bengaluru","Koramangala","Sony World Signal · 5 min walk",22000,44000,"Any","photo-1600607687939-ce8a6c25118c",new String[]{"AC","Wi-Fi","Laundry","Attached bathroom"}},
            new Object[]{"A warm, easygoing place in HSR","Bengaluru","HSR Layout","HSR BDA Complex · 6 min walk",16500,33000,"Men","photo-1600607687920-4e2a09cf159d",new String[]{"Wi-Fi","Meals","Parking","Security"}},
            new Object[]{"Thoughtful living, minutes from the metro","Hyderabad","Madhapur","Madhapur Metro · 9 min walk",19500,39000,"Any","photo-1600566753086-00f18fb6b3ea",new String[]{"AC","Wi-Fi","Housekeeping","Power backup"}},
            new Object[]{"A bright little home in Baner","Pune","Baner","Baner Road · 4 min walk",14500,29000,"Women","photo-1600607687939-ce8a6c25118c",new String[]{"Wi-Fi","Meals","Laundry","Security"}},
            new Object[]{"A quieter side of Andheri East","Mumbai","Andheri East","Metro Line 7 · 7 min walk",24500,49000,"Any","photo-1600210492486-724fe5c67fb0",new String[]{"AC","Wi-Fi","Laundry","Parking"}}
        );
        int i=0;for(Object[] row:samples){Listing l=new Listing();l.owner=owner;l.title=(String)row[0];l.city=(String)row[1];l.locality=(String)row[2];l.landmark=(String)row[3];l.monthlyRent=(Integer)row[4];l.deposit=(Integer)row[5];l.genderPreference=(String)row[6];l.coverImage="https://images.unsplash.com/"+row[7]+"?auto=format&fit=crop&w=1100&q=85";l.description="Demo sample listing. Amenities, prices, availability and location notes are owner-provided sample content for product evaluation and must be confirmed before any real booking.";l.availableBeds=i%2==0?2:1;l.demo=true;l.verified=false;l.slug="demo-"+(++i)+"-"+l.city.toLowerCase(Locale.ROOT);l.amenities.addAll(Arrays.asList((String[])row[8]));listings.save(l);}
    };}
}
