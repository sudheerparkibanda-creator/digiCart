import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VerifyJwt {
    public static void main(String[] args) throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBZG1pbiIsImlhdCI6MTc3NzYwODUxOSwiZXhwIjoxNzc3Njk0OTE5fQ.YcE13NFWUdsUCnP1yxoI8kvV7A29pool19yjAgNwm5Q";
        String secret = "digiCartSuperSecretJWTKey2026ChangeInProd!!!";
        String[] parts = token.split("\\.");
        String header = new String(Base64.getUrlDecoder().decode(parts[0]));
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        byte[] key = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        byte[] sig = mac.doFinal((parts[0] + "." + parts[1]).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String computed = Base64.getUrlEncoder().withoutPadding().encodeToString(sig);
        System.out.println("header=" + header);
        System.out.println("payload=" + payload);
        System.out.println("computed=" + computed);
        System.out.println("tokenSig=" + parts[2]);
        System.out.println("valid=" + computed.equals(parts[2]));
    }
}
