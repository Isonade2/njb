package njb.recipe.global.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.access-key:}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key:}")
    private String secretKey;

    @PostConstruct
    public void checkAwsConfig() {
        System.out.println("======== AWS CONFIG CHECK ========");
        System.out.println("Region: " + region);
        System.out.println("Access Key: " + (accessKey.isEmpty() ? "NOT SET" : "**********"));
        System.out.println("Secret Key: " + (secretKey.isEmpty() ? "NOT SET" : "**********"));
        System.out.println("Credentials Type: " + (accessKey.isEmpty() ? "IAM ROLE (EC2)" : "ACCESS KEY (Local)"));
        System.out.println("===================================");
    }

    /**
     * S3Client Bean (파일 업로드/삭제용)
     */
    @Bean
    public S3Client s3Client() {
        if (!accessKey.isBlank() && !secretKey.isBlank()) {
            System.out.println("S3Client → Using StaticCredentialsProvider (Access Key)");
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .build();
        } else {
            System.out.println("S3Client → Using DefaultCredentialsProvider (IAM Role)");
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }

    /**
     * S3Presigner Bean (Presigned URL 생성용)
     */
    @Bean
    public S3Presigner s3Presigner() {
        if (!accessKey.isBlank() && !secretKey.isBlank()) {
            System.out.println("S3Presigner → Using StaticCredentialsProvider (Access Key)");
            return S3Presigner.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .build();
        } else {
            System.out.println("S3Presigner → Using DefaultCredentialsProvider (IAM Role)");
            return S3Presigner.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }
}
