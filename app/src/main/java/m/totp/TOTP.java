package m.totp;

import java.net.URI;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TOTP {
	private String id;
	private String provider;
	private String account;
	private String secretKey;
	private int digits;
	private int refreshInterval;
	private String algorithm;
	private long timeBase;
	private long lastTimeSeed;
	private String otp;
	
	public TOTP(String id, String provider, String account, String secretKey, int digits, int refreshInterval, String algorithm, long timeBase) {
		this.id = id;
		this.provider = provider;
		this.account = account;
		this.secretKey = secretKey;
		this.digits = digits;
		this.refreshInterval = refreshInterval;
		this.algorithm = algorithm;
		this.timeBase = timeBase;
	}
	
	public TOTP(String id, String provider, String account, String secretKey, int digits, int refreshInterval, String algorithm) {
		this(id, provider, account, secretKey, digits, refreshInterval, algorithm, 0);
	}
	
	public TOTP(String id, String provider, String account, String secretKey, int digits, String algorithm) {
		this(id, provider, account, secretKey, digits, 30000, algorithm, 0);
	}
	
	public TOTP(String id, String provider, String account, String secretKey, int digits, int refreshInterval) {
		this(id, provider, account, secretKey, digits, refreshInterval, "HmacSHA1", 0);
	}
	
	public TOTP(String id, String provider, String account, String secretKey, int digits) {
		this(id, provider, account, secretKey, digits, 30000, "HmacSHA1", 0);
	}
	
	public TOTP(String id, String provider, String account, String secretKey) {
		this(id, provider, account, secretKey, 6, 30000, "HmacSHA1", 0);
	}
	
	public static TOTP fromUri(String id, String uri) {
		// otpauth://totp/${issuer}:${account}?secret=${secret}&issuer=${issuer}
		URI link = URI.create(uri);
		if (!"otpauth".equalsIgnoreCase(link.getScheme())) {
			throw new IllegalArgumentException("wrong uri: " + uri);
		}
		String provider = null;
		String secretKey = null;
		String[] queries = link.getQuery().split("&");
		for (String query : queries) {
			String[] kv = query.split("=");
			if ("secret".equalsIgnoreCase(kv[0])) {
				secretKey = kv[1];
			} else if ("issuer".equalsIgnoreCase(kv[0])) {
				provider = kv[1];
			}
		}
		String path = link.getPath();
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		String[] parts = path.split(":");
		provider = provider == null ? parts[0] : provider;
		String account = parts[1];
		return new TOTP(id, provider, account, secretKey);
	}
	
	public String getID() {
		return id;
	}
	
	public String getProvider() {
		return provider;
	}
	
	public String getAccount() {
		return account;
	}
	
	public String getSecretKey() {
		return secretKey;
	}
	
	public int getDigits() {
		return digits;
	}
	
	public int getRefreshInterval() {
		return refreshInterval;
	}
	
	public String getAlgorithm() {
		return algorithm;
	}
	
	public long getTimeBase() {
		return timeBase;
	}
	
	public String generate() throws Throwable {
		long timeSeed = (System.currentTimeMillis() - timeBase) / refreshInterval;
		if (otp == null || lastTimeSeed != timeSeed) {
			Mac mac = Mac.getInstance(algorithm);
			mac.init(new SecretKeySpec(base32Decode(secretKey), algorithm));
			byte[] hash = mac.doFinal(longToBytes(timeSeed));
			int binary = bytesToInt(hash, hash[hash.length - 1] & 0xf);
			otp = String.valueOf(binary);
			while (otp.length() < digits) {
				otp = "0" + otp;
			}
			otp = otp.substring(otp.length() - digits);
			lastTimeSeed = timeSeed;
		}
//		System.out.println(">>> " + getValidTime());
		return otp;
	}
	
	public long getValidTime() {
		long time = System.currentTimeMillis() - timeBase;
		long timeSeed = (time + refreshInterval - 1) / refreshInterval;
		return timeSeed * refreshInterval - time;
	}
	
	private byte[] base32Decode(String str) throws IllegalArgumentException {
		byte[] result = new byte[(str.length() * 5 + 7) / 8];
		int resultIndex = 0;
		int which = 0;
		int working = 0;
		for (char ch : str.toCharArray()) {
			int val;
			if (ch >= 'a' && ch <= 'z') {
				val = ch - 'a';
			} else if (ch >= 'A' && ch <= 'Z') {
				val = ch - 'A';
			} else if (ch >= '2' && ch <= '7') {
				val = 26 + (ch - '2');
			} else if (ch == '=') { // 结束符号
				which = 0;
				break;
			} else {
				throw new IllegalArgumentException("Invalid base-32 character: " + ch);
			}
			switch (which) {
				case 0: {
					// val是当前字节的前5位
					working = (val & 0x1F) << 3;
					which = 1;
				} break;
				case 1: {
					// val的头3位是当前字节的后3位
					working |= (val & 0x1C) >> 2;
					result[resultIndex++] = (byte) working;
					// val的后2位是下个字节的前2位
					working = (val & 0x03) << 6;
					which = 2;
				} break;
				case 2: {
					// val是当前字节的第3到第7位
					working |= (val & 0x1F) << 1;
					which = 3;
				} break;
				case 3: {
					// val的头1位是当前字节的最后1位
					working |= (val & 0x10) >> 4;
					result[resultIndex++] = (byte) working;
					// val的后4位是下个字节的前4位
					working = (val & 0x0F) << 4;
					which = 4;
				} break;
				case 4: {
					// val的头1位是当前字节的后4位
					working |= (val & 0x1E) >> 1;
					result[resultIndex++] = (byte) working;
					// val的最后1位是下个字节的最前1位
					working = (val & 0x01) << 7;
					which = 5;
				} break;
				case 5: {
					// val是当前字节的第2到第6位
					working |= (val & 0x1F) << 2;
					which = 6;
				} break;
				case 6: {
					// val的头2位是当前字节的后2位
					working |= (val & 0x18) >> 3;
					result[resultIndex++] = (byte) working;
					// val的后3位是下个字节的前3位
					working = (val & 0x07) << 5;
					which = 7;
				} break;
				case 7: {
					// val是当前字节的最后5位
					working |= (val & 0x1F);
					result[resultIndex++] = (byte) working;
					which = 0;
				} break;
			}
		}
		if (which != 0) {
			result[resultIndex++] = (byte) working;
		}
		if (resultIndex != result.length) {
			byte[] copy = new byte[resultIndex];
			System.arraycopy(result, 0, copy, 0, Math.min(result.length, resultIndex));
			result = copy;
		}
		return result;
	}
	
	private byte[] longToBytes(long l) {
		return new byte[] {
				(byte) (l >>> 56),
				(byte) (l >>> 48),
				(byte) (l >>> 40),
				(byte) (l >>> 32),
				(byte) (l >>> 24),
				(byte) (l >>> 16),
				(byte) (l >>> 8),
				(byte) (l >>> 0)
		};
	}
	
	private int bytesToInt(byte[] bytes, int offset) {
		return ((bytes[offset] & 0x7f) << 24)
				| ((bytes[offset + 1] & 0xff) << 16)
				| ((bytes[offset + 2] & 0xff) << 8)
				| ((bytes[offset + 3] & 0xff) << 0);
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		
		TOTP totp = (TOTP) o;
		return digits == totp.digits
				&& refreshInterval == totp.refreshInterval
				&& timeBase == totp.timeBase
				&& Objects.equals(id, totp.id)
				&& Objects.equals(provider, totp.provider)
				&& Objects.equals(account, totp.account)
				&& Objects.equals(secretKey, totp.secretKey)
				&& Objects.equals(algorithm, totp.algorithm);
	}
	
	public int hashCode() {
		int result = Objects.hashCode(id);
		result = 31 * result + Objects.hashCode(provider);
		result = 31 * result + Objects.hashCode(account);
		result = 31 * result + Objects.hashCode(secretKey);
		result = 31 * result + digits;
		result = 31 * result + refreshInterval;
		result = 31 * result + Objects.hashCode(algorithm);
		result = 31 * result + Long.hashCode(timeBase);
		return result;
	}
}
