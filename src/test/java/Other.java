import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author lyy
 */
public class Other {
    public static void main(String[] args) {
//        genId();

        DateTimeFormatter withZone = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .withZone(ZoneId.of("+8"));

        String format = withZone.format(LocalDateTime.now());

        System.out.println(format);

        System.out.println(withZone.parse("2021-04-27 23:14:10"));
    }

    public static void genId(){
        String fastSimpleUUID = IdUtil.fastSimpleUUID();
        String randomUUID = IdUtil.randomUUID();
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
//        System.out.println(fastSimpleUUID);
//        System.out.println(randomUUID);
//        System.out.println(snowflake.nextIdStr());
//        System.out.println(snowflake.nextIdStr());
//        System.out.println(snowflake.nextIdStr());
//        System.out.println(snowflake.nextIdStr().length());
//        String encodeHexStr = HexUtil.toHex(snowflake.nextId());
//        System.out.println(encodeHexStr);
//        System.out.println(TimeUtil.parseDate("2021-01-11 00:00:00").getTime()); //1610294400000
//        System.out.println(1048575/1000/3600/12);
//        String user = Base62.encode("User", StandardCharsets.UTF_8);
//        System.out.println(user);
//        System.out.println(HexUtil.encodeHexStr(user));
//        byte[] decodeHex = HexUtil.decodeHex(HexUtil.encodeHexStr(user));
//        System.out.println(new String(decodeHex));

        System.out.println(SecureUtil.md5("123").length());
    }
}
