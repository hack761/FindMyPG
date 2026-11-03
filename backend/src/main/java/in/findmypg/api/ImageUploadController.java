package in.findmypg.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.*;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
class ImageUploadController implements WebMvcConfigurer {
    @Value("${app.upload-dir:./uploads}") String uploadDir;
    @Value("${app.public-api-url:http://localhost:8080}") String publicApiUrl;
    @Override public void addResourceHandlers(ResourceHandlerRegistry registry){try{Path dir=Paths.get(uploadDir).toAbsolutePath().normalize();Files.createDirectories(dir);registry.addResourceHandler("/uploads/**").addResourceLocations(dir.toUri().toString());}catch(IOException e){throw new IllegalStateException("Could not prepare image storage",e);}}
    @PostMapping(value="/api/owner/uploads",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> upload(@RequestPart("file") MultipartFile file,@AuthenticationPrincipal Jwt jwt){
        if(file.isEmpty()||file.getSize()>8*1024*1024)return ResponseEntity.badRequest().body(Map.of("message","Choose an image smaller than 8 MB."));
        if(!List.of("image/jpeg","image/png").contains(file.getContentType()))return ResponseEntity.badRequest().body(Map.of("message","Upload a JPEG or PNG image."));
        try{BufferedImage input=ImageIO.read(file.getInputStream());if(input==null||input.getWidth()>6000||input.getHeight()>6000||(long)input.getWidth()*input.getHeight()>20_000_000)return ResponseEntity.badRequest().body(Map.of("message","This image could not be processed or is too large."));
            BufferedImage safe=new BufferedImage(input.getWidth(),input.getHeight(),BufferedImage.TYPE_INT_RGB);Graphics2D graphics=safe.createGraphics();graphics.setColor(Color.WHITE);graphics.fillRect(0,0,safe.getWidth(),safe.getHeight());graphics.drawImage(input,0,0,null);graphics.dispose();String name=UUID.randomUUID()+".jpg";Path destination=Paths.get(uploadDir).toAbsolutePath().normalize().resolve(name);if(!destination.getParent().equals(Paths.get(uploadDir).toAbsolutePath().normalize()))return ResponseEntity.badRequest().build();ImageIO.write(safe,"jpg",destination.toFile());return ResponseEntity.status(201).body(Map.of("url",publicApiUrl.replaceAll("/$","")+"/uploads/"+name));
        }catch(IOException e){return ResponseEntity.status(500).body(Map.of("message","The image could not be stored. Please try again."));}
    }
}
