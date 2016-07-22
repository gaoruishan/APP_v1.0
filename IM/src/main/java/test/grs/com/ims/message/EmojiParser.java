package test.grs.com.ims.message;

import android.content.Context;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import test.grs.com.ims.R;

public class EmojiParser {

	// Singleton stuff
	private static EmojiParser sInstance;
    private static int mType=0;

    public static EmojiParser getInstance(Context context,int type) {
        mType =type;
		if(sInstance == null){
			sInstance = new EmojiParser(context);
		}
		return sInstance;
	}
    public static EmojiParser getInstance(Context context) {
        mType = 0;
		if(sInstance == null){
			sInstance = new EmojiParser(context);
		}
		return sInstance;
	}

	private final Context mContext;
	public final String[] mSmileyTexts;
	public final String[] mEncodedSmileyTexts;
	public final Pattern mPattern;
	//encoded text - r.drawable.xxx
	public final HashMap<String, Integer> mSmileyToRes;
	
	public int getSmileyDrawableId(String text){
		//Log.d("XXX", "getSmileyDrawableId() >>> mSmileyToRes size = " + mSmileyToRes.size() +", query = " + text +", result = " + mSmileyToRes.get(text));
		return mSmileyToRes.get(text);
	}
	
	public String[] getEncodedSmilyTextArray(){
		return mEncodedSmileyTexts;
	}
	

	private EmojiParser(Context context) {
		mContext = context;
		mSmileyTexts = mContext.getResources().getStringArray(
				R.array.emoji_smiley_texts);
		
		mEncodedSmileyTexts = new String[mSmileyTexts.length];
		
		final int emojiLength = mSmileyTexts.length;
		for(int i=0; i< emojiLength; i++){
			//不去转码
            if (mType ==0){
                mEncodedSmileyTexts[i] = convertUnicode(mSmileyTexts[i]);
            }else  if (mType ==1){
                mEncodedSmileyTexts[i] = "[e]"+mSmileyTexts[i]+"[/e]";
            }
		}
		
		mSmileyToRes = buildSmileyToRes();
		mPattern = buildPattern();
	}

	/**
	 * Builds the hashtable we use for mapping the string version of a smiley
	 * (e.g. ":-)") to a resource ID for the icon version.
	 */
	private HashMap<String, Integer> buildSmileyToRes() {
		HashMap<String, Integer> smileyToRes = new HashMap<String, Integer>(
				mSmileyTexts.length);
		for (int i = 0; i < mSmileyTexts.length; i++) {
			smileyToRes.put(mEncodedSmileyTexts[i], getSmileyResources(mSmileyTexts[i]));
		}
		return smileyToRes;
	}
	
	private int getSmileyResources(String codeName) {
		int id = -1;
		String name = "emoji_" + codeName;
		Field field;
		try {
			field = R.drawable.class.getDeclaredField(name);
			if (field != null) {
				id = field.getInt(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Builds the regular expression we use to find smileys in
	 */
	private Pattern buildPattern() {
		// Set the StringBuilder capacity with the assumption that the average
		// smiley is 3 characters long.
		StringBuilder patternString = new StringBuilder(mEncodedSmileyTexts.length * 3);
		// Build a regex that looks like (:-)|:-(|...), but escaping the smilies
		// properly so they will be interpreted literally by the regex matcher.
		patternString.append('(');
		for (String s : mEncodedSmileyTexts) {
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		// Replace the extra '|' with a ')'
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");
		return Pattern.compile(patternString.toString());
	}


	private static String convertUnicode(String emo) {
		emo = emo.substring(emo.indexOf("_") + 1);
		if (emo.length() < 6) {
			String s = new String(Character.toChars(Integer.parseInt(emo, 16)));
//			byte[] textBytes = s.getBytes();
//			Log.d("XXX", "---------BYTE ENCODED---------" + emo);
//			for(byte b: textBytes){
//				Log.d("XXX", ""+(int)b);
//			}
//			Log.d("XXX", "-------------------------");
			return s;
		}
		String[] emos = emo.split("_");
		char[] char0 = Character.toChars(Integer.parseInt(emos[0], 16));
		char[] char1 = Character.toChars(Integer.parseInt(emos[1], 16));
		char[] emoji = new char[char0.length + char1.length];
		for (int i = 0; i < char0.length; i++) {
			emoji[i] = char0[i];
		}
		for (int i = char0.length; i < emoji.length; i++) {
			emoji[i] = char1[i - char0.length];
		}
		String s =  new String(emoji);
		return s;
	}
	private HashMap<List<Integer>, String> convertMap = new HashMap<List<Integer>, String>();

	public String parseEmoji(String input) {
		if (input == null || input.length() <= 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		int[] codePoints = toCodePointArray(input);
		List<Integer> key = null;
		for (int i = 0; i < codePoints.length; i++) {
			key = new ArrayList<Integer>();
			if (i + 1 < codePoints.length) {
				key.add(codePoints[i]);
				key.add(codePoints[i + 1]);
				if (convertMap.containsKey(key)) {
					String value = convertMap.get(key);
					if (value != null) {
						result.append("[e]" + value + "[/e]");
					}
					i++;
					continue;
				}
			}
			key.clear();
			key.add(codePoints[i]);
			if (convertMap.containsKey(key)) {
				String value = convertMap.get(key);
				if (value != null) {
					result.append("[e]" + value + "[/e]");
				}
				continue;
			}
			result.append(Character.toChars(codePoints[i]));
		}
		return result.toString();
	}
	private int[] toCodePointArray(String str) {
		char[] ach = str.toCharArray();
		int len = ach.length;
		int[] acp = new int[Character.codePointCount(ach, 0, len)];
		int j = 0;
		for (int i = 0, cp; i < len; i += Character.charCount(cp)) {
			cp = Character.codePointAt(ach, i);
			acp[j++] = cp;
		}
		return acp;
	}

}
